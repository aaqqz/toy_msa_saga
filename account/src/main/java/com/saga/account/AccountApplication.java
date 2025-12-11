package com.saga.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class AccountApplication {

    @Bean
    public WebClient webClientaaa() {
        return WebClient.builder().build();
    }

    public static void main(String[] args) {
        SpringApplication.run(AccountApplication.class, args);
    }

}
