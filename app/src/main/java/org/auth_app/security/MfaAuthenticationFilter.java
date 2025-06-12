// src/main/java/org/auth_app/security/MfaAuthenticationFilter.java
package org.auth_app.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.auth_app.model.User;
import org.auth_app.repository.UserRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class MfaAuthenticationFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    public MfaAuthenticationFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
                                    throws ServletException, IOException {

        String path = request.getRequestURI();

        // 1) Always let static assets & MFA endpoints through
        if ( path.startsWith("/mfa/") ||
             path.startsWith("/css/") ||
             path.startsWith("/js/") ||
             path.startsWith("/images/") ||
             path.startsWith("/static/") ) {
            chain.doFilter(request, response);
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null
            && auth.isAuthenticated()
            && !(auth instanceof AnonymousAuthenticationToken)) {

            String username = auth.getName();
            User user = userRepository.findByUsername(username).orElse(null);

            // 2) Only enforce second factor if the user has MFA turned on
            if (user != null && user.isMfaEnabled()) {
                HttpSession session = request.getSession(false);
                boolean passed = session != null &&
                                 Boolean.TRUE.equals(session.getAttribute("MFA_AUTHENTICATED"));

                if (!passed) {
                    // redirect them into the validate form
                    response.sendRedirect(request.getContextPath() + "/mfa/validate");
                    return;
                }
            }
        }

        // either not logged in, or no MFA needed, or MFA already passed
        chain.doFilter(request, response);
    }
}
