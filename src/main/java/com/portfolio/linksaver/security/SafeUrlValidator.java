package com.portfolio.linksaver.security;

import java.net.IDN;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class SafeUrlValidator {

    private static final int MAX_URL_LENGTH = 2048;
    private static final Set<String> ALLOWED_SCHEMES = Set.of("http", "https");
    private static final Set<Integer> ALLOWED_PORTS = Set.of(80, 443);

    public static class BlockedUrlException extends RuntimeException {
        public BlockedUrlException(String message) {
            super(message);
        }
    }

    /** Zwraca znormalizowany URI, jeśli adres jest bezpieczny. W przeciwnym razie rzuca BlockedUrlException. */
    public URI validate(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            throw new BlockedUrlException("Brak adresu URL");
        }
        if (rawUrl.length() > MAX_URL_LENGTH) {
            throw new BlockedUrlException("Adres URL jest zbyt długi");
        }

        URI uri;
        try {
            uri = new URI(rawUrl.trim());
        } catch (URISyntaxException e) {
            throw new BlockedUrlException("Nieprawidłowy adres URL");
        }

        if (!uri.isAbsolute() || uri.getScheme() == null) {
            throw new BlockedUrlException("Adres URL musi być absolutny");
        }

        String scheme = uri.getScheme().toLowerCase(Locale.ROOT);
        if (!ALLOWED_SCHEMES.contains(scheme)) {
            throw new BlockedUrlException("Dozwolone są tylko adresy http i https");
        }

        // http://youtube.com@169.254.169.254/ - userinfo służy tylko do mylenia walidacji
        if (uri.getUserInfo() != null) {
            throw new BlockedUrlException("Adres URL nie może zawierać danych uwierzytelniających");
        }

        String host = uri.getHost();
        if (host == null || host.isBlank()) {
            throw new BlockedUrlException("Nie można ustalić hosta z adresu URL");
        }


        int port = uri.getPort() == -1 ? ("https".equals(scheme) ? 443 : 80) : uri.getPort();
        if (!ALLOWED_PORTS.contains(port)) {
            throw new BlockedUrlException("Dozwolone są tylko porty 80 i 443");
        }

        
        for (InetAddress address : resolve(host)) {
            if (!isPubliclyRoutable(address)) {
                throw new BlockedUrlException("Adres wskazuje na zasób w sieci wewnętrznej");
            }
        }

        return uri;
    }

    private InetAddress[] resolve(String host) {
        // URI.getHost() returns the IP address in brackets if it is an IPv6 address we need to remove the brackets
        String lookupHost = host.startsWith("[") && host.endsWith("]")
                ? host.substring(1, host.length() - 1)
                : host;
        try {
            // IDN.toASCII - convert the host to ASCII to avoid Unicode characters
            return InetAddress.getAllByName(IDN.toASCII(lookupHost.toLowerCase(Locale.ROOT)));
        } catch (UnknownHostException | IllegalArgumentException e) {
            throw new BlockedUrlException("Nie udało się rozwiązać nazwy hosta");
        }
    }

    // check if the adress is dangerous
    private boolean isPubliclyRoutable(InetAddress address) {
        if (address.isAnyLocalAddress() || address.isLoopbackAddress()
                || address.isLinkLocalAddress() || address.isSiteLocalAddress()
                || address.isMulticastAddress()) {
            return false;
        }

        byte[] bytes = address.getAddress();

        if (address instanceof Inet4Address) {
            int b0 = bytes[0] & 0xFF;
            int b1 = bytes[1] & 0xFF;
            int b2 = bytes[2] & 0xFF;

            if (b0 == 0 || b0 == 127) return false;                     // 0.0.0.0/8, 127.0.0.0/8
            if (b0 == 10) return false;                                 // 10.0.0.0/8
            if (b0 == 100 && b1 >= 64 && b1 <= 127) return false;       // 100.64.0.0/10 CGNAT
            if (b0 == 169 && b1 == 254) return false;                   // 169.254.0.0/16 metadane chmury
            if (b0 == 172 && b1 >= 16 && b1 <= 31) return false;        // 172.16.0.0/12
            if (b0 == 192 && b1 == 168) return false;                   // 192.168.0.0/16
            if (b0 == 192 && b1 == 0 && (b2 == 0 || b2 == 2)) return false;
            if (b0 == 198 && (b1 == 18 || b1 == 19)) return false;      // 198.18.0.0/15
            if (b0 == 198 && b1 == 51 && b2 == 100) return false;
            if (b0 == 203 && b1 == 0 && b2 == 113) return false;
            if (b0 >= 224) return false;                                // multicast, zarezerwowane, broadcast
            return true;
        }

        if (address instanceof Inet6Address) {
            int b0 = bytes[0] & 0xFF;
            int b1 = bytes[1] & 0xFF;

            if ((b0 & 0xFE) == 0xFC) return false;                      // fc00::/7 unique local
            if (b0 == 0xFF) return false;                               // ff00::/8 multicast
            if (b0 == 0xFE && (b1 & 0xC0) == 0x80) return false;        // fe80::/10 link local
            if (b0 == 0x20 && b1 == 0x02) return false;                 // 2002::/16 6to4
            if (b0 == 0x20 && b1 == 0x01 && bytes[2] == 0 && bytes[3] == 0) return false; // Teredo
            if (bytes[0] == 0x00 && bytes[1] == 0x64
                    && (bytes[2] & 0xFF) == 0xFF && (bytes[3] & 0xFF) == 0x9B) return false; // 64:ff9b::/96 NAT64
            return true;
        }

        return false;
    }
}