package org.auth_app.security;

import org.auth_app.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationFailureHandler failureHandler;
    private final CustomAuthenticationSuccessHandler successHandler;

    @Autowired
    public SecurityConfig(
            CustomUserDetailsService customUserDetailsService,
            CustomAuthenticationFailureHandler failureHandler,
            CustomAuthenticationSuccessHandler successHandler
    ) {
        this.customUserDetailsService = customUserDetailsService;
        this.failureHandler = failureHandler;
        this.successHandler = successHandler;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Security filter chain for OAuth2 endpoints (if you’re using Spring Authorization Server).
     */
    @Bean
    @Order(1)
    SecurityFilterChain oauth2EndpointsFilterChain(HttpSecurity http) throws Exception {
        http
          .securityMatcher("/oauth2/**")
          .authorizeHttpRequests(a -> a.anyRequest().authenticated())
          .formLogin(f -> f.loginPage("/auth/login").permitAll())
          .authenticationProvider(authenticationProvider());
        return http.build();
    }

    /**
     * Default security for everything else.
     * We’ve added “/actuator/prometheus” to the permitAll() list so Prometheus can scrape without login.
     */
    @Bean
    @Order(2)
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
          .csrf(c -> c.disable())
          .authorizeHttpRequests(a -> a
            .requestMatchers(
              "/auth/login",
              "/auth/register",
              "/auth/forgot-password",
              "/error",
              "/static/**", "/css/**", "/js/**",
              "/actuator/prometheus"         // allow unauthenticated Prometheus scraping
            ).permitAll()
            .anyRequest().authenticated()
          )
          .formLogin(f -> f
            .loginPage("/auth/login")
            .loginProcessingUrl("/auth/login")
            .defaultSuccessUrl("/dashboard", true)
            .successHandler(successHandler)
            .failureHandler(failureHandler)
            .permitAll()
          )
          .logout(l -> l
            .logoutSuccessUrl("/auth/login?logout")
            .permitAll()
          )
          .authenticationProvider(authenticationProvider());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
// This configuration class sets up Spring Security for the application, defining security rules for OAuth2 endpoints and default security settings.
// It includes custom authentication handlers for success and failure scenarios, and uses BCrypt for password encoding.
// The `SecurityConfig` class is annotated with `@Configuration` and `@EnableWebSecurity`, indicating that it provides Spring Security configuration.
// The `@Order` annotation specifies the order of the security filter chains, allowing for different configurations for OAuth2 endpoints and other requests.
// The `authenticationProvider` method creates a `DaoAuthenticationProvider` that uses the custom user details service and password encoder.
// The `oauth2EndpointsFilterChain` method configures security for OAuth2 endpoints, requiring authentication for all requests.
// The `defaultSecurityFilterChain` method configures security for all other requests, allowing unauthenticated access to specific endpoints and requiring authentication for others.
// The `authenticationManager` and `passwordEncoder` methods provide beans for authentication management and password encoding, respectively.
// The `SecurityConfig` class is a key part of the application's security setup, ensuring that user authentication and authorization are handled correctly.
// It integrates with the custom user details service and authentication handlers to provide a seamless login experience.