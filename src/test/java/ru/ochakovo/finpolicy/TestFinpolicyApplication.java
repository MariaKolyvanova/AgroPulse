package ru.ochakovo.finpolicy;

import org.springframework.boot.SpringApplication;

public class TestFinpolicyApplication {

    public static void main(String[] args) {
        SpringApplication.from(FinpolicyApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
