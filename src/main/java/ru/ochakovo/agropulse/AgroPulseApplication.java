package ru.ochakovo.agropulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import ru.ochakovo.web.config.WebConfiguration;

@EnableJpaAuditing
@ConfigurationPropertiesScan
@SpringBootApplication
public class agropulseApplication {

    public static void main(String[] args) {
        SpringApplication.run(agropulseApplication.class, args);
    }

}
