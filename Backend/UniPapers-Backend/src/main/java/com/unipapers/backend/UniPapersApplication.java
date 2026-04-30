package com.unipapers.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UniPapersApplication {

    public static void main(String[] args) {
        SpringApplication.run(UniPapersApplication.class, args);
    }

}
