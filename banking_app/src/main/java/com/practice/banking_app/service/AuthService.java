package com.practice.banking_app.service;

import com.practice.banking_app.dto.LoginRequest;
import com.practice.banking_app.dto.RegisterRequest;
import com.practice.banking_app.entity.Role;
import com.practice.banking_app.entity.User;
import com.practice.banking_app.exception.UserAlreadyExistsException;
import com.practice.banking_app.repository.RoleRepository;
import com.practice.banking_app.repository.UserRepository;
import com.practice.banking_app.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Assign USER role by default
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRoles(Set.of(userRole));

        userRepository.save(user);
    }

    public Map<String, Object> login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(authentication);
        
        // Get all user roles
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        
        // Check if user has admin role
        boolean isAdmin = roles.contains("ROLE_ADMIN");
        
        // Create response map
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("username", request.getUsername());
        response.put("roles", roles);
        response.put("isAdmin", isAdmin);
        response.put("message", "Login successful");
        
        return response;
    }
}