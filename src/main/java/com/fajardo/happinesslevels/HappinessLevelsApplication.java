package com.fajardo.happinesslevels;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(PipelineProperties.class)
public class HappinessLevelsApplication {
    
    public static void main(String args[]) {
        SpringApplication.run(HappinessLevelsApplication.class, args);
    }
}
