package org601.controller;

import org601.dto.AuthRequest;
import org601.dto.AuthResponse;
import org601.entity.User;
import org601.repository.UserRepository;
import org601.security.JwtUtil;
import org601.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public AuthController(UserService userService, UserRepository userRepository, JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.authManager = new ProviderManager(new DaoAuthenticationProvider() {{
            setUserDetailsService(userDetailsService);
            setPasswordEncoder(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder());
        }});
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest req) {
        if (userRepository.findByUsername(req.username).isPresent()) return ResponseEntity.badRequest().body("User exists");
        User u = userService.register(req.username, req.password, Set.of("ROLE_USER"));
        return ResponseEntity.ok(u.getUsername());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(req.username, req.password));
            var userDetails = userDetailsService.loadUserByUsername(req.username);
            var roles = userDetails.getAuthorities().stream().map(a -> a.getAuthority()).collect(java.util.stream.Collectors.toSet());
            String token = jwtUtil.generateToken(req.username, roles);
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}
