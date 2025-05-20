package org.auth_app.security;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.event.*;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityMetricsListener implements ApplicationListener<AbstractAuthenticationEvent> {

    private final MeterRegistry meterRegistry;

    public SecurityMetricsListener(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void onApplicationEvent(@NonNull AbstractAuthenticationEvent event) {
        String username = event.getAuthentication().getName();
        String ip = extractIp(event);

        if (event instanceof AuthenticationSuccessEvent) {
            meterRegistry.counter("login_attempts_total", "outcome", "success", "username", username, "ip", ip).increment();
        } else if (event instanceof AuthenticationFailureBadCredentialsEvent) {
            meterRegistry.counter("login_attempts_total", "outcome", "failure", "reason", "bad_credentials", "username", username, "ip", ip).increment();
        } else if (event instanceof AuthenticationFailureLockedEvent) {
            meterRegistry.counter("login_attempts_total", "outcome", "failure", "reason", "account_locked", "username", username, "ip", ip).increment();
            meterRegistry.counter("security.account_locked", "username", username).increment();
        } else if (event instanceof AuthenticationFailureDisabledEvent) {
            meterRegistry.counter("login_attempts_total", "outcome", "failure", "reason", "account_disabled", "username", username, "ip", ip).increment();
        } else if (event instanceof AuthenticationFailureExpiredEvent) {
            meterRegistry.counter("login_attempts_total", "outcome", "failure", "reason", "account_expired", "username", username, "ip", ip).increment();
        } else if (event instanceof AuthenticationFailureCredentialsExpiredEvent) {
            meterRegistry.counter("login_attempts_total", "outcome", "failure", "reason", "credentials_expired", "username", username, "ip", ip).increment();
        } else {
            meterRegistry.counter("login_attempts_total", "outcome", "failure", "reason", "unknown", "username", username, "ip", ip).increment();
        }
    }

    private String extractIp(AbstractAuthenticationEvent event) {
        Object details = event.getAuthentication().getDetails();
        if (details instanceof WebAuthenticationDetails) {
            return ((WebAuthenticationDetails) details).getRemoteAddress();
        }
        return "unknown";
    }
}
// This class listens for authentication events and records metrics using Micrometer.
// It tracks successful and failed login attempts, including reasons for failure (e.g., bad credentials, account locked).