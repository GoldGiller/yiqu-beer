package com.cocktailmaker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 调酒模拟器主应用类
 * 
 * @author Cocktail Maker Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableAsync
public class CocktailMakerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CocktailMakerApplication.class, args);
    }
}