package com.example.demo;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
class LoadDB {

    @Bean
    CommandLineRunner initDatabase(ItemRepository repository) {
        return args -> {
            log.info("Preloading " + repository.save(new Item("Iphone", 5 , "3553353Iphone")));
            log.info("Preloading " + repository.save(new Item("macbook", 9, "1331macbook")));
        };
    }
}
