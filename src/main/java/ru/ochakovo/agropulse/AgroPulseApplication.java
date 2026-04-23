package ru.ochakovo.agropulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@ConfigurationPropertiesScan
@SpringBootApplication
public class AgroPulseApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgroPulseApplication.class, args);
    }

}
