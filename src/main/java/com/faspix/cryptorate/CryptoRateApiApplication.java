package com.faspix.cryptorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CryptoRateApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CryptoRateApiApplication.class, args);
    }

}
