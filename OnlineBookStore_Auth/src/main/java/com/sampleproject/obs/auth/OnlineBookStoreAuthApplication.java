package com.sampleproject.obs.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@SpringBootApplication(scanBasePackages = { "com.sampleproject.obs", "com.sampleproject.obs.*" })
@PropertySources({ @PropertySource("classpath:env.properties"), @PropertySource("classpath:message.properties") })
public class OnlineBookStoreAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineBookStoreAuthApplication.class, args);

	}
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("*");
			}
		};
	}

}
