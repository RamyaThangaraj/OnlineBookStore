package com.sampleproject.obs.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(scanBasePackages = { "com.sampleproject.obs", "com.sampleproject.obs.*" })
public class OnlineBookStoreDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineBookStoreDataApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
