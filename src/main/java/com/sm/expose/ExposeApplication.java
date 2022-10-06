package com.sm.expose;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class ExposeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExposeApplication.class, args);
	}

}
