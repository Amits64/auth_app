package org.auth_app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    @Order(1)
    SecurityFilterChain oauth2EndpointsFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/oauth2/**")
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .formLogin(form -> form.loginPage("/login").permitAll());
        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain globalSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", "/login", "/login?error", "/error",
                    "/static/**", "/css/**", "/js/**" // Allow static resources
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .failureUrl("/login?error")
                .permitAll()
            );
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}