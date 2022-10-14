package com.sm.expose.global.security.repository;

import com.sm.expose.global.security.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
//    Optional<User> findByEmail(String email);
    User findByEmail(String email);
//    Boolean existsByEmail(String email);
}
