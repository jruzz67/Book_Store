package com.examly.springapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import springfox.documentation.swagger2.annotations.EnableSwagger2;

// import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
// @EnableSwagger2 // Enables Swagger (Springfox) for API documentation
public class SpringappApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringappApplication.class, args);
    }
}