package com.project.guviproject2;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
		boolean hasRailwayMysqlConfig = hasText(System.getenv("MYSQLHOST"))
				|| hasText(System.getenv("RAILWAY_PRIVATE_DOMAIN"))
				|| hasText(System.getenv("MYSQLDATABASE"))
				|| hasText(System.getenv("MYSQL_DATABASE"))
				|| hasText(mysqlUrl);

		Map<String, Object> defaults = new LinkedHashMap<>();

		if (!hasText(activeProfile) && hasRailwayMysqlConfig) {
			defaults.put("spring.profiles.active", "mysql");
		}

		if (!hasText(System.getenv("SPRING_DATASOURCE_URL")) && hasText(mysqlUrl)) {
			defaults.put("spring.datasource.url", toJdbcMysqlUrl(mysqlUrl));
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

	private static String toJdbcMysqlUrl(String url) {
		if (url.startsWith("jdbc:mysql://")) {
			return url;
		}

		if (!url.startsWith("mysql://")) {
			return url;
		}

		try {
			URI uri = new URI(url);
			String host = uri.getHost();
			int port = uri.getPort() > 0 ? uri.getPort() : 3306;
			String database = uri.getPath() != null ? uri.getPath().replaceFirst("^/", "") : "";
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

			String userInfo = uri.getUserInfo();
			if (hasText(userInfo)) {
				String[] userParts = userInfo.split(":", 2);
				if (hasText(userParts[0])) {
					params.putIfAbsent("user", encode(userParts[0]));
				}
				if (userParts.length > 1 && hasText(userParts[1])) {
					params.putIfAbsent("password", encode(userParts[1]));
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

			return jdbc.toString();
		} catch (URISyntaxException ex) {
			return url;
		}
	}

	private static String encode(String value) {
		return URLEncoder.encode(value, StandardCharsets.UTF_8);
	}

	private static boolean hasText(String value) {
		return value != null && !value.isBlank();
	}

}
