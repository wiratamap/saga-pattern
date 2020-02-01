package com.personal.sagapattern;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@Configuration
@EnableJpaAuditing
public class ServiceAccountApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceAccountApplication.class, args);
	}

}
