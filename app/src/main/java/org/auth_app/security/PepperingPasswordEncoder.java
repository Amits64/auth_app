// src/main/java/org/auth_app/security/PepperingPasswordEncoder.java
package org.auth_app.security;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * Wraps a DelegatingPasswordEncoder (Argon2 + BCrypt) and
 * always prepends a fixed "pepper" to the raw password.
 */
public class PepperingPasswordEncoder implements PasswordEncoder {

    private final PasswordEncoder delegate;
    private final String pepper;

    public PepperingPasswordEncoder(String pepper) {
        this.pepper = pepper;

        // 1) Prepare the two encoders
        Argon2PasswordEncoder argon2 = new Argon2PasswordEncoder(
            /* saltLength */ 16,
            /* hashLength */ 32,
            /* parallelism */ 1,
            /* memory(kiB) */ 4096,
            /* iterations */ 3
        );
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

        // 2) Build a DelegatingPasswordEncoder that defaults to Argon2 for new hashes
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("argon2", argon2);
        encoders.put("bcrypt", bcrypt);

        DelegatingPasswordEncoder delegator =
            new DelegatingPasswordEncoder("argon2", encoders);

        // 3) For any stored hash without a prefix, fall back to BCrypt
        delegator.setDefaultPasswordEncoderForMatches(bcrypt);

        this.delegate = delegator;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        // prepend pepper before encoding
        return delegate.encode(rawPassword.toString() + pepper);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        // prepend pepper before matching
        return delegate.matches(rawPassword.toString() + pepper, encodedPassword);
    }
}
