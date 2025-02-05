package com.muse.RhyFeel.service;

import com.muse.RhyFeel.model.User;
import com.muse.RhyFeel.repository.UserRepository;
import com.muse.RhyFeel.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String register(String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            return "Email already exists";
        }

        String encryptedPassword = passwordEncoder.encode(password);

        User user = new User();
        user.setEmail(email);
        user.setPassword(encryptedPassword);
        userRepository.save(user);

        return "User registered successfully";
    }

    public String login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return "User not found";
        }

        User user = userOptional.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return "Invalid password";
        }

        // JWT 발급
        String token = JwtUtil.generateToken(email);
        return "Bearer " + token;
    }

}
