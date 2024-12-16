package com.example.PIAPS.service;
import com.example.PIAPS.model.User;
import com.example.PIAPS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TwoFactorAuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    public boolean sendTwoFactorToken(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.isTwoFactorEnabled()) {
            throw new RuntimeException("Two-factor authentication is not enabled for this user.");
        }

        String token = UUID.randomUUID().toString();
        user.setTwoFactorTempToken(token);
        userRepository.save(user);

        // Отправка токена на email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your Two-Factor Authentication Code");
        message.setText("Your authentication token is: " + token);

        mailSender.send(message);
        return true;
    }

    public boolean verifyTwoFactorToken(String email, String token) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.isTwoFactorEnabled()) {
            throw new RuntimeException("Two-factor authentication is not enabled for this user.");
        }

        if (user.getTwoFactorTempToken().equals(token)) {
            user.setTwoFactorVerified(true);
            userRepository.save(user);
            return true;
        } else {
            throw new RuntimeException("Invalid token.");
        }
    }
}