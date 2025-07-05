package com.practice.banking_app.config;

import com.practice.banking_app.entity.Role;
import com.practice.banking_app.entity.User;
import com.practice.banking_app.repository.RoleRepository;
import com.practice.banking_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create roles if they don't exist
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ROLE_USER");
                    Role savedRole = roleRepository.save(role);
                    log.info("Created ROLE_USER");
                    return savedRole;
                });

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ROLE_ADMIN");
                    Role savedRole = roleRepository.save(role);
                    log.info("Created ROLE_ADMIN");
                    return savedRole;
                });

        // Create admin user if doesn't exist
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@banking.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            
            // Create a new Set and add both roles
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            adminRoles.add(userRole);
            admin.setRoles(adminRoles);
            
            User savedAdmin = userRepository.save(admin);
            log.info("Created admin user with roles: {}", 
                savedAdmin.getRoles().stream()
                    .map(Role::getName)
                    .toList());
        } else {
            // Check if existing admin has both roles
            User existingAdmin = userRepository.findByUsername("admin").get();
            boolean hasUserRole = existingAdmin.getRoles().stream()
                    .anyMatch(role -> "ROLE_USER".equals(role.getName()));
            boolean hasAdminRole = existingAdmin.getRoles().stream()
                    .anyMatch(role -> "ROLE_ADMIN".equals(role.getName()));
            
            if (!hasUserRole || !hasAdminRole) {
                Set<Role> adminRoles = new HashSet<>();
                adminRoles.add(adminRole);
                adminRoles.add(userRole);
                existingAdmin.setRoles(adminRoles);
                userRepository.save(existingAdmin);
                log.info("Updated admin user with both roles");
            }
            
            log.info("Admin user exists with roles: {}", 
                existingAdmin.getRoles().stream()
                    .map(Role::getName)
                    .toList());
        }
    }
}