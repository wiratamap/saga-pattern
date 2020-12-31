package com.personal.sagapattern;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class ServiceAccountApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceAccountApplication.class, args);
    }

}
