package org601.config;

import org601.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class DataInitializer {
    private final UserService userService;
    public DataInitializer(UserService userService) { this.userService = userService; }

    @Bean
    CommandLineRunner init() {
        return args -> {
            try {
                userService.register("admin", "admin123", Set.of("ROLE_ADMIN","ROLE_USER"));
            } catch(Exception e) {}
        };
    }
}
