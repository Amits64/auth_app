package org.auth_app.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Bucket loginBucket = Bucket.builder()
        .addLimit(Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1))))
        .build();

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest req,
        @NonNull HttpServletResponse res,
        @NonNull FilterChain chain
    ) throws ServletException, IOException {
        if ("/auth/login".equals(req.getRequestURI())) {
            ConsumptionProbe probe = loginBucket.tryConsumeAndReturnRemaining(1);
            if (!probe.isConsumed()) {
                res.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                res.getWriter().write("Too many attempts. Try again in a bit.");
                return;
            }
        }
        chain.doFilter(req, res);
    }
}
