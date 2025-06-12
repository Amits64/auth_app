package org.auth_app.security;

import org.auth_app.model.MfaCredentials;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MfaCredentialsRepository extends CrudRepository<MfaCredentials, String> {
    // Spring Data JPA will provide basic CRUD operations for MfaCredentials,
    // where the primary key is of type String.
}