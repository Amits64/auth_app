package org.auth_app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
public class MfaCredentials {

    @Id
    private String username;
    
    @Column(nullable = false)
    private String secretKey;
    
    // We'll store scratch codes as a comma-separated string.
    private String scratchCodes;

    // Getters and setters
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getSecretKey() {
        return secretKey;
    }
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
    public String getScratchCodes() {
        return scratchCodes;
    }
    public void setScratchCodes(String scratchCodes) {
        this.scratchCodes = scratchCodes;
    }
}