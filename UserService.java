package org601.service;

import org601.entity.User;
import org601.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository; this.encoder = encoder;
    }

    public User register(String username, String password, Set<String> roles) {
        User u = new User(username, encoder.encode(password), roles);
        return userRepository.save(u);
    }
}
