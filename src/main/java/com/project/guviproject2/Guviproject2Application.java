package com.project.guviproject2;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Guviproject2Application {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(Guviproject2Application.class);
		String activeProfile = System.getenv("SPRING_PROFILES_ACTIVE");
		String mysqlUrl = firstNonBlank(System.getenv("MYSQL_URL"), System.getenv("MYSQL_PUBLIC_URL"));
		boolean hasRailwayMysqlConfig = isConfigured(System.getenv("MYSQLHOST"))
				|| isConfigured(System.getenv("RAILWAY_PRIVATE_DOMAIN"))
				|| isConfigured(System.getenv("MYSQLDATABASE"))
				|| isConfigured(System.getenv("MYSQL_DATABASE"))
				|| isConfigured(mysqlUrl);

		Map<String, Object> defaults = new LinkedHashMap<>();

		if (!hasText(activeProfile) && hasRailwayMysqlConfig) {
			defaults.put("spring.profiles.active", "mysql");
		}

		if (!hasText(System.getenv("SPRING_DATASOURCE_URL")) && isConfigured(mysqlUrl)) {
			MySqlConnectionInfo connectionInfo = parseMysqlUrl(mysqlUrl);
			if (connectionInfo != null) {
				defaults.put("spring.datasource.url", connectionInfo.jdbcUrl());
				if (!hasText(System.getenv("SPRING_DATASOURCE_USERNAME")) && hasText(connectionInfo.username())) {
					defaults.put("spring.datasource.username", connectionInfo.username());
				}
				if (!hasText(System.getenv("SPRING_DATASOURCE_PASSWORD")) && hasText(connectionInfo.password())) {
					defaults.put("spring.datasource.password", connectionInfo.password());
				}
			}
		}

		if (!defaults.isEmpty()) {
			application.setDefaultProperties(defaults);
		}

		application.run(args);
	}

	private static String firstNonBlank(String... values) {
		for (String value : values) {
			if (hasText(value)) {
				return value;
			}
		}
		return null;
	}

	private static MySqlConnectionInfo parseMysqlUrl(String url) {
		if (url.startsWith("jdbc:mysql://")) {
			return new MySqlConnectionInfo(url, null, null);
		}

		if (!url.startsWith("mysql://")) {
			return null;
		}

		try {
			URI uri = new URI(url);
			String host = uri.getHost();
			if (!hasText(host)) {
				return null;
			}
			int port = uri.getPort() > 0 ? uri.getPort() : 3306;
			String database = uri.getPath() != null ? uri.getPath().replaceFirst("^/", "") : "railway";
			String query = uri.getRawQuery();

			StringBuilder jdbc = new StringBuilder("jdbc:mysql://")
					.append(host)
					.append(":")
					.append(port)
					.append("/")
					.append(database);

			Map<String, String> params = new LinkedHashMap<>();
			if (query != null && !query.isBlank()) {
				for (String pair : query.split("&")) {
					String[] parts = pair.split("=", 2);
					String key = parts[0];
					String value = parts.length > 1 ? parts[1] : "";
					params.put(key, value);
				}
			}

			String username = null;
			String password = null;
			String userInfo = uri.getUserInfo();
			if (hasText(userInfo)) {
				String[] userParts = userInfo.split(":", 2);
				if (hasText(userParts[0])) {
					username = userParts[0];
				}
				if (userParts.length > 1 && hasText(userParts[1])) {
					password = userParts[1];
				}
			}

			params.putIfAbsent("useSSL", "false");
			params.putIfAbsent("allowPublicKeyRetrieval", "true");
			params.putIfAbsent("serverTimezone", "UTC");

			if (!params.isEmpty()) {
				jdbc.append("?");
				boolean first = true;
				for (Map.Entry<String, String> entry : params.entrySet()) {
					if (!first) {
						jdbc.append("&");
					}
					jdbc.append(entry.getKey()).append("=").append(entry.getValue());
					first = false;
				}
			}

			return new MySqlConnectionInfo(jdbc.toString(), username, password);
		} catch (URISyntaxException ex) {
			return null;
		}
	}

	private static boolean isConfigured(String value) {
		return hasText(value) && !value.contains("${{");
	}

	private static boolean hasText(String value) {
		return value != null && !value.isBlank();
	}

	private record MySqlConnectionInfo(String jdbcUrl, String username, String password) {}

}
