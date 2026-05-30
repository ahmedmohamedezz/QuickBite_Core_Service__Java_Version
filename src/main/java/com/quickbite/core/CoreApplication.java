package com.quickbite.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.quickbite.core.common.config")
public class CoreApplication {

    public static void main(String[] args) {
		SpringApplication.run(CoreApplication.class, args);

        // in case of a startup error
//        try {
//            SpringApplication.run(CoreApplication.class, args);
//        } catch (Exception e) {
//            // This will print the raw underlying error that JwtService threw
//            Throwable rootCause = e;
//            while (rootCause.getCause() != null) {
//                rootCause = rootCause.getCause();
//            }
//            rootCause.printStackTrace();
//        }
    }
}