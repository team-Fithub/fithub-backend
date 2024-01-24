package com.fithub.fithubbackend.domain.user.repository;

import com.fithub.fithubbackend.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByNickname(String nickname);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndProvider(String email, String provider);
    Optional<User> findByProviderId(String providerId);

    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
    boolean existsByEmailAndProviderIsNull(String email);
    boolean existsByEmailAndProvider(String email, String provider);
}
