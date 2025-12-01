package com.taskflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.taskflow") 
@EnableJpaRepositories(basePackages = "com.taskflow")
@EntityScan(basePackages = "com.taskflow")
public class SolucionesApplication {

	public static void main(String[] args) {
		SpringApplication.run(SolucionesApplication.class, args);
	}

}
