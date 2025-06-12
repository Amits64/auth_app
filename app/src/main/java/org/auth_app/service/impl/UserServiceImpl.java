// src/main/java/org/auth_app/service/impl/UserServiceImpl.java
package org.auth_app.service.impl;

import java.util.Optional;

import org.auth_app.model.User;
import org.auth_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository repo, PasswordEncoder encoder) {
        this.userRepository = repo;
        this.passwordEncoder = encoder;
    }

    /**
     * Persist a new User (encoding their password).
     */
    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Look up a User by username.
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Spring Security contract.
     */
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }

    /**
     * Update the three new preferences fields on the existing User.
     */
    @Transactional
    public void updatePreferences(
            String username,
            boolean enableNotifications,
            boolean darkMode,
            String language
    ) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        user.setEnableNotifications(enableNotifications);
        user.setDarkMode(darkMode);
        user.setLanguage(language);
        // No need to call save(): within @Transactional the entity is dirty-checked and flushed.
    }

    /** Return the stored MFA secret (or null if none). */
    public String getMfaSecret(String username) {
        return userRepository.findByUsername(username)
                             .map(User::getMfaSecret)
                             .orElse(null);
    }

    /** Persist (or overwrite) the TOTP secret on the user. */
    @Transactional
    public void updateMfaSecret(String username, String secret) {
        User u = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        u.setMfaSecret(secret);
    }

    /** Flip the “enabled” flag on. */
    @Transactional
    public void enableMfa(String username) {
        User u = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        u.setMfaEnabled(true);
    }

    /** Turn MFA off and clear the secret. */
    @Transactional
    public void disableMfa(String username) {
        User u = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        u.setMfaEnabled(false);
        u.setMfaSecret(null);
    }
}