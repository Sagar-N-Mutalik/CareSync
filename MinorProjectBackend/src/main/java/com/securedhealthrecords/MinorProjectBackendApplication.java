package com.securedhealthrecords;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class MinorProjectBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MinorProjectBackendApplication.class, args);
		System.out.println("🚀 Secured Health Records Backend API is running!");
		System.out.println("📋 API Documentation: http://localhost:8080/api/v1/swagger-ui.html");
		System.out.println("🔒 Security: JWT Authentication enabled");
		System.out.println("📧 Email: SMTP configured for OTP and notifications");
		System.out.println("🗄️  Database: MongoDB connection established");
	}
}
