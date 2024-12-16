package com.example.PIAPS.service;

import com.example.PIAPS.model.User;
import com.example.PIAPS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    public User createUser(User user) {
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void sendConfirmationEmail(String email, String token) {
        String confirmationUrl = "http://localhost:8080/confirm-registration?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Confirm Your Registration");
        message.setText("Please click the link below to confirm your registration:\n" + confirmationUrl);

        mailSender.send(message);
    }
    public boolean isChildProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.isChildProfile();
    }
    public void enableTwoFactorAuth(Long userId, boolean enable) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setTwoFactorEnabled(enable);
        userRepository.save(user);
    }

    public boolean confirmRegistration(String token) {
        User user = userRepository.findByRegistrationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid registration token"));

        // Подтверждаем регистрацию
        user.setRegistrationToken(null); // Убираем токен подтверждения
        userRepository.save(user);
        return true;
    }

    public void updateUser(Long id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(updatedUser.getUsername());
        user.setPassword(updatedUser.getPassword());
        user.setEmail(updatedUser.getEmail());
        user.setRole(updatedUser.getRole());
        user.setProfileVisibility(updatedUser.isProfileVisibility());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(user);
    }
}

