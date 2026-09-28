package com.portfolio.linksaver.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import com.portfolio.linksaver.security.SafeUrlValidator;

@Service
public class SafeHttpFetcher {

    private static final int MAX_REDIRECTS = 8;
    private static final int TIMEOUT_MS = 5000;
    private static final int MAX_BODY_BYTES = 2 * 1024 * 1024;
    private static final String USER_AGENT =
            "facebookexternalhit/1.1 (+http://www.facebook.com/externalhit_uatext.php)";
    private static final Set<Integer> REDIRECT_STATUSES = Set.of(301, 302, 303, 307, 308);

    private final SafeUrlValidator urlValidator;

    public SafeHttpFetcher(SafeUrlValidator urlValidator) {
        this.urlValidator = urlValidator;
    }

    public Document fetchDocument(String url) throws IOException {
        Connection.Response response = fetch(url);
        String contentType = response.contentType();
        if (contentType != null
                && !contentType.startsWith("text/html")
                && !contentType.startsWith("application/xhtml")
                && !contentType.startsWith("text/plain")) {
            throw new IOException("Nieobsługiwany typ treści: " + contentType);
        }
        return response.parse();
    }

    public String fetchBody(String url) throws IOException {
        return fetch(url).body();
    }

    /** Rozwija skrócone linki (vm.tiktok.com, youtu.be) do adresu docelowego. */
    public String resolveFinalUrl(String url) throws IOException {
        return fetch(url).url().toString();
    }

    private Connection.Response fetch(String startUrl) throws IOException {
        String currentUrl = startUrl;
        Map<String, String> cookies = new HashMap<>();

        for (int hop = 0; hop <= MAX_REDIRECTS; hop++) {
            URI safeUri = urlValidator.validate(currentUrl);

            Connection.Response response = Jsoup.connect(safeUri.toString())
                    .userAgent(USER_AGENT)
                    .referrer("http://www.google.com")
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .timeout(TIMEOUT_MS)
                    .maxBodySize(MAX_BODY_BYTES)
                    .cookies(cookies)
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .followRedirects(false)
                    .execute();

            cookies.putAll(response.cookies());

            int status = response.statusCode();
            if (!REDIRECT_STATUSES.contains(status)) {
                if (status >= 400) {
                    throw new IOException("Serwer zwrócił status HTTP " + status);
                }
                return response;
            }

            String location = response.header("Location");
            if (location == null || location.isBlank()) {
                throw new IOException("Przekierowanie bez nagłówka Location");
            }
            currentUrl = resolveLocation(safeUri, location.trim());
        }

        throw new IOException("Przekroczono limit przekierowań (" + MAX_REDIRECTS + ")");
    }

    
    private String resolveLocation(URI base, String location) throws IOException {
        try {
            return new URL(base.toURL(), location).toString();
        } catch (MalformedURLException e) {
            throw new IOException("Nieprawidłowy nagłówek Location: " + location);
        }
    }
}