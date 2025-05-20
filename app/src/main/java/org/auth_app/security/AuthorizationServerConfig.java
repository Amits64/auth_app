package org.auth_app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import javax.sql.DataSource;
import java.time.Duration;
import java.util.UUID;

@Configuration
public class AuthorizationServerConfig {

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource ds) {
        return new JdbcTemplate(ds);
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbc) {
        var repo = new JdbcRegisteredClientRepository(jdbc);

        if (repo.findByClientId("spa-client") == null) {
            var spa = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("spa-client")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost:3000/login/oauth2/code/spa-client")
                .clientSettings(ClientSettings.builder()
                    .requireAuthorizationConsent(false)
                    .build())
                .tokenSettings(TokenSettings.builder()
                    .accessTokenTimeToLive(Duration.ofHours(1))
                    .build())
                .build();
            repo.save(spa);
        }
        return repo;
    }

    @Bean
    public JdbcOAuth2AuthorizationService authorizationService(
        JdbcTemplate jdbc,
        RegisteredClientRepository clients
    ) {
        return new JdbcOAuth2AuthorizationService(jdbc, clients);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
            .issuer("http://localhost:8080")
            .build();
    }
}
