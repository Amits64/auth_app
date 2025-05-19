package org.auth_app.repository;

import org.auth_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;    // add this
import java.util.Optional;                      // add this

@Repository                                   // you can leave this or remove it
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
