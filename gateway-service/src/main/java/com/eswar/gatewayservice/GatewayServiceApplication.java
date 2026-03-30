package com.eswar.gatewayservice;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SpringBootApplication
public class GatewayServiceApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(GatewayServiceApplication.class);

		// Add initializer to inject .env values into Spring Environment
		app.addInitializers(ctx -> {
			Dotenv dotenv = Dotenv.load();

			Map<String, Object> properties = new HashMap<>();
			properties.put("SECRET", Objects.requireNonNull(dotenv.get("SECRET")));

			// Add as first property source so it overrides other defaults
			ctx.getEnvironment()
					.getPropertySources()
					.addFirst(new MapPropertySource("dotenvProperties", properties));
		});

		app.run(args);
	}

}
