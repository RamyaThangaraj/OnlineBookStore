package com.sampleproject.obs.buyer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "com.sampleproject.obs", "com.sampleproject.obs.*" })
public class OnlineBookStoreBuyerApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineBookStoreBuyerApplication.class, args);
	}

}
