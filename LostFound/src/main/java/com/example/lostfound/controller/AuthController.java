package com.example.lostfound.controller;

import com.example.lostfound.model.User;
import com.example.lostfound.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // ---------- REGISTER ----------
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String registerNumber = body.get("registerNumber");
        String password = body.get("password");

        if (registerNumber == null || password == null || registerNumber.isEmpty() || password.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Register number and password are required"));
        }

        Optional<User> existingUser = userRepository.findByRegisterNumber(registerNumber);
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "User already exists"));
        }

        User user = new User();
        user.setRegisterNumber(registerNumber);
        user.setPassword(password);
        user.setRole("STUDENT");  // default role
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Student registered successfully"));
    }

    // ---------- LOGIN ----------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String registerNumber = body.get("registerNumber");
        String email = body.get("email");
        String password = body.get("password");

        if ((registerNumber == null || registerNumber.isEmpty()) &&
            (email == null || email.isEmpty())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Register number or email is required"));
        }

        if (password == null || password.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Password is required"));
        }

        Optional<User> opt;
        if (email != null && email.contains("@")) {
            opt = userRepository.findByEmail(email);
        } else {
            opt = userRepository.findByRegisterNumber(registerNumber);
        }

        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }

        User user = opt.get();

        if (!user.getPassword().equals(password)) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }

        // Role-based response
        return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "role", user.getRole(),
                "email", user.getEmail(),
                "registerNumber", user.getRegisterNumber()
        ));
    }
}
