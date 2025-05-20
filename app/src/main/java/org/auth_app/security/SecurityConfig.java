package org.auth_app.security;

import org.auth_app.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    // 1️⃣ DAO authentication provider
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // 2️⃣ Secure /oauth2/** for the auth server endpoints
    @Bean
    @Order(1)
    SecurityFilterChain oauth2EndpointsFilterChain(HttpSecurity http) throws Exception {
        http
          .securityMatcher("/oauth2/**")
          .authorizeHttpRequests(a -> a.anyRequest().authenticated())
          .formLogin(f -> f.loginPage("/login").permitAll())
          .authenticationProvider(authenticationProvider());
        return http.build();
    }

    // 3️⃣ Default filter chain: static assets, registration, login, dashboard
    @Bean
    @Order(2)
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
          .csrf(c -> c.disable())
          .authorizeHttpRequests(a -> a
            .requestMatchers(
              "/", "/login**", "/error",
              "/auth/register", "/actuator/**",
              "/auth/forgot-password", "/auth/reset-password",
              "/actuator/prometheus", "/actuator/health",
              "/static/**", "/css/**", "/js/**"
            ).permitAll()
            .anyRequest().authenticated()
          )
          .formLogin(f -> f
            .loginPage("/auth/login")
            .loginProcessingUrl("/auth/login")
            .defaultSuccessUrl("/dashboard", true)
            .failureUrl("/auth/login?error=true")
            .permitAll()
          )
          .logout(l -> l
            .logoutSuccessUrl("/auth/login?logout")
            .permitAll()
          )
          .authenticationProvider(authenticationProvider());

        return http.build();
    }

    // 4️⃣ Expose AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // 5️⃣ BCryptPasswordEncoder bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
