package com.backend.jwtauthorization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity
public class JWTAuthorizationApplication {

	public static void main(String[] args) {
		SpringApplication.run(JWTAuthorizationApplication.class, args);
	}

}
