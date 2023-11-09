package com.fithub.fithubbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class FithubBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FithubBackendApplication.class, args);
	}

}
