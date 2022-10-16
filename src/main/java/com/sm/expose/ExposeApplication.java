package com.sm.expose;

import com.sm.expose.global.common.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class ExposeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExposeApplication.class, args);
	}

}
