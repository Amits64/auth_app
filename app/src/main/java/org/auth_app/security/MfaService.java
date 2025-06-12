// src/main/java/org/auth_app/security/MfaService.java
package org.auth_app.security;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.springframework.stereotype.Service;

@Service
public class MfaService {
    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    /** Create a fresh secret + key. */
    public GoogleAuthenticatorKey createCredentials() {
        return gAuth.createCredentials();
    }

    /** Extract the raw Base32 secret. */
    public String getSecret(GoogleAuthenticatorKey key) {
        return key.getKey();
    }

    /** Build an OTP-Auth URI that Authenticator apps understand. */
    public String getOtpAuthUri(String accountName, GoogleAuthenticatorKey key) {
        return GoogleAuthenticatorQRGenerator.getOtpAuthURL("SinghEnterprise", accountName, key);
    }

    /** Verify a one-time code against the secret. */
    public boolean verifyCode(String secretKey, int code) {
        return gAuth.authorize(secretKey, code);
    }
}
