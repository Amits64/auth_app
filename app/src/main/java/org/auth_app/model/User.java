// src/main/java/org/auth_app/model/User.java
package org.auth_app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private int failedAttempt;
    private boolean accountNonLocked = true;
    private LocalDateTime lockTime;

    // ——— MFA fields ———
    @Column(length = 32)
    private String mfaSecret;
    @Column(nullable = false)
    private boolean mfaEnabled = false;

    @Column(nullable = false)
    private boolean enabled = true;

    // ——— NEW PREFERENCES fields ———
    @Column(name = "enable_notifications", nullable = false)
    private boolean enableNotifications = false;

    @Column(name = "dark_mode", nullable = false)
    private boolean darkMode = false;

    /** 2-letter ISO code, e.g. "en", "es" */
    @Column(name = "language", length = 2, nullable = false)
    private String language = "en";

    // —— Getters & Setters —— 

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getFailedAttempt() { return failedAttempt; }
    public void setFailedAttempt(int failedAttempt) { this.failedAttempt = failedAttempt; }

    public boolean isAccountNonLocked() { return accountNonLocked; }
    public void setAccountNonLocked(boolean accountNonLocked) { this.accountNonLocked = accountNonLocked; }

    public LocalDateTime getLockTime() { return lockTime; }
    public void setLockTime(LocalDateTime lockTime) { this.lockTime = lockTime; }

    public String getMfaSecret() { return mfaSecret; }
    public void setMfaSecret(String mfaSecret) { this.mfaSecret = mfaSecret; }

    public boolean isMfaEnabled() { return mfaEnabled; }
    public void setMfaEnabled(boolean mfaEnabled) { this.mfaEnabled = mfaEnabled; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    // ——— New getters/setters for preferences ———

    public boolean isEnableNotifications() {
        return enableNotifications;
    }
    public void setEnableNotifications(boolean enableNotifications) {
        this.enableNotifications = enableNotifications;
    }

    public boolean isDarkMode() {
        return darkMode;
    }
    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }

    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }
}
