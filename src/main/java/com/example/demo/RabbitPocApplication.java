package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication//(exclude = RabbitAutoConfiguration.class)
public class RabbitPocApplication {

	public static void main(String[] args) {
		SpringApplication.run(RabbitPocApplication.class, args);
	}

}
