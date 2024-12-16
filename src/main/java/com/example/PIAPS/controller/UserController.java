package com.example.PIAPS.controller;

import com.example.PIAPS.model.User;
import com.example.PIAPS.service.UserService;
import com.example.PIAPS.service.TwoFactorAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TwoFactorAuthService twoFactorAuthService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PreAuthorize("!@userService.isChildProfile(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/send-two-factor-token")
    public ResponseEntity<Void> sendTwoFactorToken(@RequestParam String email) {
        twoFactorAuthService.sendTwoFactorToken(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-two-factor-token")
    public ResponseEntity<Void> verifyTwoFactorToken(@RequestParam String email, @RequestParam String token) {
        twoFactorAuthService.verifyTwoFactorToken(email, token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send-registration-confirmation")
    public ResponseEntity<Void> sendRegistrationConfirmation(@RequestParam String email) {
        String token = UUID.randomUUID().toString();
        User user = userService.getUserByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        user.setRegistrationToken(token);
        userService.sendConfirmationEmail(email, token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/confirm-registration")
    public ResponseEntity<Void> confirmRegistration(@RequestParam String token) {
        userService.confirmRegistration(token);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/{id}/enable-two-factor")
    public ResponseEntity<Void> enableTwoFactor(@PathVariable Long id) {
        userService.enableTwoFactorAuth(id, true);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/disable-two-factor")
    public ResponseEntity<Void> disableTwoFactor(@PathVariable Long id) {
        userService.enableTwoFactorAuth(id, false);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("!@userService.isChildProfile(#id)")
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        userService.updateUser(id, updatedUser);
        return ResponseEntity.ok(updatedUser);
    }

    // Новый метод для удаления пользователя
    @PreAuthorize("!@userService.isChildProfile(#id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}