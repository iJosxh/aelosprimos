package com.aeropuertolosprimos.aeropuerto_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class AeropuertoBackendApplication {

	public static void main(String[] args) {

		SpringApplication.run(AeropuertoBackendApplication.class, args);
	}
}