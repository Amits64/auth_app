CREATE TABLE oauth2_registered_client (
    id                       VARCHAR(100)   NOT NULL,
    client_id                VARCHAR(100)   NOT NULL,
    client_id_issued_at      TIMESTAMP      DEFAULT CURRENT_TIMESTAMP NOT NULL,
    client_secret            VARCHAR(200)   DEFAULT NULL,
    client_secret_expires_at TIMESTAMP      DEFAULT NULL,
    client_name              VARCHAR(200)   NOT NULL,
    client_authentication_methods   VARCHAR(1000) NOT NULL,
    authorization_grant_types       VARCHAR(1000) NOT NULL,
    redirect_uris            VARCHAR(1000)  DEFAULT NULL,
    post_logout_redirect_uris VARCHAR(1000) DEFAULT NULL,
    scopes                   VARCHAR(1000)  NOT NULL,
    client_settings          VARCHAR(2000)  NOT NULL,
    token_settings           VARCHAR(2000)  NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE oauth2_authorization (
    id                       VARCHAR(100)   NOT NULL,
    registered_client_id     VARCHAR(100)   NOT NULL,
    principal_name           VARCHAR(200)   NOT NULL,
    authorization_grant_type VARCHAR(100)   NOT NULL,
    authorized_scopes        VARCHAR(1000)  DEFAULT NULL,
    attributes               BLOB           DEFAULT NULL,
    state                    VARCHAR(500)   DEFAULT NULL,
    authorization_code_value        BLOB    DEFAULT NULL,
    authorization_code_issued_at    TIMESTAMP DEFAULT NULL,
    authorization_code_expires_at   TIMESTAMP DEFAULT NULL,
    authorization_code_metadata      BLOB    DEFAULT NULL,
    access_token_value        BLOB          DEFAULT NULL,
    access_token_issued_at    TIMESTAMP     DEFAULT NULL,
    access_token_expires_at   TIMESTAMP     DEFAULT NULL,
    access_token_metadata     BLOB          DEFAULT NULL,
    access_token_type         VARCHAR(100)  DEFAULT NULL,
    access_token_scopes       VARCHAR(1000) DEFAULT NULL,
    oidc_id_token_value       BLOB          DEFAULT NULL,
    oidc_id_token_issued_at   TIMESTAMP     DEFAULT NULL,
    oidc_id_token_expires_at  TIMESTAMP     DEFAULT NULL,
    oidc_id_token_metadata    BLOB          DEFAULT NULL,
    refresh_token_value       BLOB          DEFAULT NULL,
    refresh_token_issued_at   TIMESTAMP     DEFAULT NULL,
    refresh_token_expires_at  TIMESTAMP     DEFAULT NULL,
    refresh_token_metadata    BLOB          DEFAULT NULL,
    user_code_value           VARCHAR(255)  DEFAULT NULL,
    user_code_issued_at       TIMESTAMP     DEFAULT NULL,
    user_code_expires_at      TIMESTAMP     DEFAULT NULL,
    user_code_metadata        BLOB          DEFAULT NULL,
    device_code_value         VARCHAR(255)  DEFAULT NULL,
    device_code_issued_at     TIMESTAMP     DEFAULT NULL,
    device_code_expires_at    TIMESTAMP     DEFAULT NULL,
    device_code_metadata      BLOB          DEFAULT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_oauth2_client FOREIGN KEY (registered_client_id)
        REFERENCES oauth2_registered_client (id)
);

CREATE TABLE oauth2_authorization_consent (
    registered_client_id VARCHAR(100) NOT NULL,
    principal_name       VARCHAR(200) NOT NULL,
    authorities          VARCHAR(1000) NOT NULL,
    PRIMARY KEY (registered_client_id, principal_name),
    CONSTRAINT fk_oauth2_client_consent FOREIGN KEY (registered_client_id)
        REFERENCES oauth2_registered_client (id)
);

CREATE TABLE oauth2_token_revocation (
    id                       VARCHAR(100)   NOT NULL,
    registered_client_id     VARCHAR(100)   NOT NULL,
    principal_name           VARCHAR(200)   NOT NULL,
    token_type              VARCHAR(100)    NOT NULL,
    token_value             BLOB            NOT NULL,
    revoked_at              TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_oauth2_client_revocation FOREIGN KEY (registered_client_id)
        REFERENCES oauth2_registered_client (id)
);

CREATE TABLE oauth2_token_introspection (
    id                       VARCHAR(100)   NOT NULL,
    registered_client_id     VARCHAR(100)   NOT NULL,
    principal_name           VARCHAR(200)   NOT NULL,
    token_type              VARCHAR(100)    NOT NULL,
    token_value             BLOB            NOT NULL,
    active                  BOOLEAN         DEFAULT TRUE NOT NULL,
    scope                   VARCHAR(1000)   DEFAULT NULL,
    client_id               VARCHAR(100)    DEFAULT NULL,
    username                VARCHAR(200)    DEFAULT NULL,
    expiration              TIMESTAMP       DEFAULT NULL,
    issued_at               TIMESTAMP       DEFAULT NULL,
    not_before              TIMESTAMP       DEFAULT NULL,
    claims                  BLOB            DEFAULT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_oauth2_client_introspection FOREIGN KEY (registered_client_id)
        REFERENCES oauth2_registered_client (id)
);

CREATE TABLE oauth2_client_registration (
    id                       VARCHAR(100)   NOT NULL,
    client_id                VARCHAR(100)   NOT NULL,
    client_id_issued_at      TIMESTAMP      DEFAULT CURRENT_TIMESTAMP NOT NULL,
    client_secret            VARCHAR(200)   DEFAULT NULL,
    client_secret_expires_at TIMESTAMP      DEFAULT NULL,
    client_name              VARCHAR(200)   NOT NULL,
    client_authentication_methods   VARCHAR(1000) NOT NULL,
    authorization_grant_types       VARCHAR(1000) NOT NULL,
    redirect_uris            VARCHAR(1000)  DEFAULT NULL,
    post_logout_redirect_uris VARCHAR(1000) DEFAULT NULL,
    scopes                   VARCHAR(1000)  NOT NULL,
    client_settings          VARCHAR(2000)  NOT NULL,
    token_settings           VARCHAR(2000)  NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE oauth2_client_registration_consent (
    registered_client_id VARCHAR(100) NOT NULL,
    principal_name       VARCHAR(200) NOT NULL,
    authorities          VARCHAR(1000) NOT NULL,
    PRIMARY KEY (registered_client_id, principal_name),
    CONSTRAINT fk_oauth2_client_registration_consent FOREIGN KEY (registered_client_id)
        REFERENCES oauth2_client_registration (id)
);

CREATE TABLE oauth2_client_registration_token_revocation (
    id                       VARCHAR(100)   NOT NULL,
    registered_client_id     VARCHAR(100)   NOT NULL,
    principal_name           VARCHAR(200)   NOT NULL,
    token_type              VARCHAR(100)    NOT NULL,
    token_value             BLOB            NOT NULL,
    revoked_at              TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_oauth2_client_registration_revocation FOREIGN KEY (registered_client_id)
        REFERENCES oauth2_client_registration (id)
);

CREATE TABLE oauth2_client_registration_token_introspection (
    id                       VARCHAR(100)   NOT NULL,
    registered_client_id     VARCHAR(100)   NOT NULL,
    principal_name           VARCHAR(200)   NOT NULL,
    token_type              VARCHAR(100)    NOT NULL,
    token_value             BLOB            NOT NULL,
    active                  BOOLEAN         DEFAULT TRUE NOT NULL,
    scope                   VARCHAR(1000)   DEFAULT NULL,
    client_id               VARCHAR(100)    DEFAULT NULL,
    username                VARCHAR(200)    DEFAULT NULL,
    expiration              TIMESTAMP       DEFAULT NULL,
    issued_at               TIMESTAMP       DEFAULT NULL,
    not_before              TIMESTAMP       DEFAULT NULL,
    claims                  BLOB            DEFAULT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_oauth2_client_registration_introspection FOREIGN KEY (registered_client_id)
        REFERENCES oauth2_client_registration (id)
);

CREATE TABLE oauth2_client_registration_authorization (
    id                       VARCHAR(100)   NOT NULL,
    registered_client_id     VARCHAR(100)   NOT NULL,
    principal_name           VARCHAR(200)   NOT NULL,
    authorization_grant_type VARCHAR(100)   NOT NULL,
    authorized_scopes        VARCHAR(1000)  DEFAULT NULL,
    attributes               BLOB           DEFAULT NULL,
    state                    VARCHAR(500)   DEFAULT NULL,
    authorization_code_value        BLOB    DEFAULT NULL,
    authorization_code_issued_at    TIMESTAMP DEFAULT NULL,
    authorization_code_expires_at   TIMESTAMP DEFAULT NULL,
    authorization_code_metadata      BLOB    DEFAULT NULL,
    access_token_value        BLOB          DEFAULT NULL,
    access_token_issued_at    TIMESTAMP     DEFAULT NULL,
    access_token_expires_at   TIMESTAMP     DEFAULT NULL,
    access_token_metadata     BLOB          DEFAULT NULL,
    access_token_type         VARCHAR(100)  DEFAULT NULL,
    access_token_scopes       VARCHAR(1000) DEFAULT NULL,
    oidc_id_token_value       BLOB          DEFAULT NULL,
    oidc_id_token_issued_at   TIMESTAMP     DEFAULT NULL,
    oidc_id_token_expires_at  TIMESTAMP     DEFAULT NULL,
    oidc_id_token_metadata    BLOB          DEFAULT NULL,
    refresh_token_value       BLOB          DEFAULT NULL,
    refresh_token_issued_at   TIMESTAMP     DEFAULT NULL,
    refresh_token_expires_at  TIMESTAMP     DEFAULT NULL,
    refresh_token_metadata    BLOB          DEFAULT NULL,
    user_code_value           VARCHAR(255)  DEFAULT NULL,
    user_code_issued_at       TIMESTAMP     DEFAULT NULL,
    user_code_expires_at      TIMESTAMP     DEFAULT NULL,
    user_code_metadata        BLOB          DEFAULT NULL,
    device_code_value         VARCHAR(255)  DEFAULT NULL,
    device_code_issued_at     TIMESTAMP     DEFAULT NULL,
    device_code_expires_at    TIMESTAMP     DEFAULT NULL,
    device_code_metadata      BLOB          DEFAULT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_oauth2_client_registration FOREIGN KEY (registered_client_id)
        REFERENCES oauth2_client_registration (id)
);

CREATE TABLE oauth2_client_registration_authorization_consent (
    registered_client_id VARCHAR(100) NOT NULL,
    principal_name       VARCHAR(200) NOT NULL,
    authorities          VARCHAR(1000) NOT NULL,
    PRIMARY KEY (registered_client_id, principal_name),
    CONSTRAINT fk_oauth2_client_registration_consent FOREIGN KEY (registered_client_id)
        REFERENCES oauth2_client_registration (id)
);

