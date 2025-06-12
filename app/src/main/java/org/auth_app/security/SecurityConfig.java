// src/main/java/org/auth_app/security/SecurityConfig.java
package org.auth_app.security;

import org.auth_app.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final CustomAuthenticationFailureHandler failureHandler;
    private final CustomAuthenticationSuccessHandler successHandler;
    private final MfaAuthenticationFilter mfaFilter;
    private final RateLimitingFilter rateLimitingFilter;
    private final String pepper;

    @Autowired
    public SecurityConfig(
        CustomUserDetailsService userDetailsService,
        CustomAuthenticationFailureHandler failureHandler,
        CustomAuthenticationSuccessHandler successHandler,
        MfaAuthenticationFilter mfaFilter,
        RateLimitingFilter rateLimitingFilter,
        @Value("${security.pepper:change_this_to_a_secret!}") String pepper
    ) {
        this.userDetailsService  = userDetailsService;
        this.failureHandler      = failureHandler;
        this.successHandler      = successHandler;
        this.mfaFilter           = mfaFilter;
        this.rateLimitingFilter  = rateLimitingFilter;
        this.pepper              = pepper;
    }

    /**
     * 1) Pepper + Argon2 password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PepperingPasswordEncoder(pepper);
    }

    /**
     * 2) Our DaoAuthenticationProvider (no event‐publisher here)
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userDetailsService);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    /**
     * 3) Wrap Spring’s ApplicationEventPublisher so that
     *    AuthenticationSuccessEvent / AuthenticationFailureBadCredentialsEvent etc. get published.
     */
    @Bean
    public AuthenticationEventPublisher authenticationEventPublisher(
            ApplicationEventPublisher publisher
    ) {
        return new DefaultAuthenticationEventPublisher(publisher);
    }

    /**
     * 4) OAuth2 filter chain (if you have /oauth2/** endpoints)
     */
    @Bean
    @Order(1)
    SecurityFilterChain oauth2FilterChain(HttpSecurity http) throws Exception {
        http
          .securityMatcher("/oauth2/**")
          .authenticationProvider(authenticationProvider())
          .authorizeHttpRequests(a -> a.anyRequest().authenticated())
          .formLogin(f -> f.loginPage("/auth/login").permitAll());
        return http.build();
    }

    /**
     * 5) The “main” filter chain for everything else
     */
    @Bean
    @Order(2)
    SecurityFilterChain defaultFilterChain(
            HttpSecurity http,
            AuthenticationEventPublisher eventPublisher  // injected so we can wire it into the ProviderManager
    ) throws Exception {

        http
          .csrf(c -> c.disable())
          .authorizeHttpRequests(a -> a
              // public
              .requestMatchers(HttpMethod.GET,  "/auth/login", "/auth/register", "/auth/forgot-password").permitAll()
              .requestMatchers(HttpMethod.POST, "/auth/login", "/auth/register", "/auth/forgot-password").permitAll()
              // static
              .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**").permitAll()
              // errors & Prometheus
              .requestMatchers("/error", "/actuator/prometheus").permitAll()
              // MFA endpoints—must at least have entered correct username/password
              .requestMatchers("/mfa/**").authenticated()
              // all others require full (MFA‐passed) login
              .anyRequest().authenticated()
          )
          // 1st: rate–limit login attempts
          .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
          // 2nd: after username/password, enforce MFA
          .addFilterAfter(mfaFilter, UsernamePasswordAuthenticationFilter.class)
          .formLogin(f -> f
              .loginPage("/auth/login")
              .loginProcessingUrl("/auth/login")
              .defaultSuccessUrl("/dashboard", true)
              .successHandler(successHandler)
              .failureHandler(failureHandler)
              .permitAll()
          )
          .logout(l -> l
              .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout", "GET"))
              .logoutSuccessUrl("/auth/login?logout")
              .invalidateHttpSession(true)
              .deleteCookies("JSESSIONID")
              .permitAll()
          )
          // register our DAO provider
          .authenticationProvider(authenticationProvider());

        // 6) **After** building the chain, grab the underlying ProviderManager
        //     and hook in our AuthenticationEventPublisher so events fire.
        //
        AuthenticationManager mgr = http.getSharedObject(AuthenticationManager.class);
        if (mgr instanceof ProviderManager pm) {
            pm.setAuthenticationEventPublisher(eventPublisher);
        }

        return http.build();
    }

    /**
     * 7) Expose the AuthenticationManager so Spring can build it for us
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration cfg
    ) throws Exception {
        return cfg.getAuthenticationManager();
    }
}
