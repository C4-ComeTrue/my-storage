package com.c4cometrue.mystorage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class C4CometrueStorageApplication {
	public static void main(String[] args) {
		SpringApplication.run(C4CometrueStorageApplication.class, args);
	}
}
