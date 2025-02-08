package com.sgc.urlgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import com.sgc.config.EnvironmentConfig;

@SpringBootApplication
public class UrlgatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(UrlgatewayApplication.class, args);
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		String helloUrl = EnvironmentConfig.get("HELLO_URL");
		String generateUrlService = EnvironmentConfig.get("GENERATE_URL_SERVICE");
		String redirectUrlService = EnvironmentConfig.get("REDIRECT_URL_SERVICE");

		RouteLocator routes = builder.routes()
				.route("hello_id", r -> r.path("/hello/{id}")
						.filters(f -> f.rewritePath("/hello/(?<id>.*)", "/get?Hello=${id}"))
						.uri(helloUrl))
				.route("generate_router", r -> r.path("/create")
						.filters(f -> f.rewritePath("/create", "/api/create"))
						.uri(generateUrlService))
				.route("redirect_router", r -> r.path("/{id}")
						.filters(f -> f.rewritePath("/(?<id>.*)", "/api/${id}"))
						.uri(redirectUrlService))
				.build();
		return routes;
	}
}