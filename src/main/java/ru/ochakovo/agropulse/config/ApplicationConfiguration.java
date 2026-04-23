package ru.ochakovo.finpolicy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.ochakovo.web.config.WebConfiguration;

@Import(WebConfiguration.class)
@Configuration
public class ApplicationConfiguration {
}
