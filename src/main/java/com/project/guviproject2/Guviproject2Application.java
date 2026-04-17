package com.project.guviproject2;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Guviproject2Application {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(Guviproject2Application.class);
		String activeProfile = System.getenv("SPRING_PROFILES_ACTIVE");
		boolean hasRailwayMysqlConfig = hasText(System.getenv("MYSQLHOST"))
				|| hasText(System.getenv("RAILWAY_PRIVATE_DOMAIN"))
				|| hasText(System.getenv("MYSQLDATABASE"))
				|| hasText(System.getenv("MYSQL_DATABASE"));

		if (!hasText(activeProfile) && hasRailwayMysqlConfig) {
			Map<String, Object> defaults = new HashMap<>();
			defaults.put("spring.profiles.active", "mysql");
			application.setDefaultProperties(defaults);
		}

		application.run(args);
	}

	private static boolean hasText(String value) {
		return value != null && !value.isBlank();
	}

}
