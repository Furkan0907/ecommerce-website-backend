package com.furkan.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = "com.furkan")
@ComponentScan(basePackages = "com.furkan")
@EnableJpaRepositories(basePackages = "com.furkan")
@SpringBootApplication
public class EcommerceBackendApplicationStarter {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceBackendApplicationStarter.class, args);
	}

}
