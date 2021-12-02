package com.sampleproject.obs.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "com.sampleproject.obs", "com.sampleproject.obs.*" })
public class OnlineBookStoreAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineBookStoreAdminApplication.class, args);
	}

}
