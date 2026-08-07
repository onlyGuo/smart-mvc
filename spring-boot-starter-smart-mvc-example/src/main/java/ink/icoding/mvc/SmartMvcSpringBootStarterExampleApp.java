package ink.icoding.mvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the executable SmartMVC Spring Boot starter example.
 *
 * <p>The application provides a database-free Servlet environment for verifying starter
 * auto-configuration, configuration metadata, standardized responses, authentication defaults,
 * temporal conversion, validation, request logging, and exception handling.</p>
 */
@SpringBootApplication
public class SmartMvcSpringBootStarterExampleApp {

    public static void main(String[] args){
        SpringApplication.run(SmartMvcSpringBootStarterExampleApp.class, args);
    }
}
