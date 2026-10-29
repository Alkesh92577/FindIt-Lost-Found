
package com.findit.config;

import com.findit.model.User;
import com.findit.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SeedData {

    @Bean
    CommandLineRunner seed(
            UserRepository users,
            PasswordEncoder encoder) {

        return args -> {
            User admin = users
                    .findByEmailIgnoreCase("alkesh6420@gmail.com")
                    .orElseGet(User::new);

            admin.name = "FindIt Admin";
            admin.email = "alkesh6420@gmail.com";
            admin.passwordHash = encoder.encode("choudhary");
            admin.role = User.Role.ADMIN;

            users.save(admin);
        };
    }
}
