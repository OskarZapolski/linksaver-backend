package com.portfolio.linksaver;

import java.security.Security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LinksaverApplication {

	public static void main(String[] args) {
		// Musi być ustawione przed pierwszym zapytaniem DNS: bez dodatniego TTL walidacja
		// adresu i samo połączenie mogłyby dostać różne IP (DNS rebinding).
		Security.setProperty("networkaddress.cache.ttl", "30");

		SpringApplication.run(LinksaverApplication.class, args);
	}

}
