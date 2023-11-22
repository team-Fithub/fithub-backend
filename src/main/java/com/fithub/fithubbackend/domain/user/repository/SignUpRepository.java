package com.fithub.fithubbackend.domain.user.repository;

import com.fithub.fithubbackend.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SignUpRepository extends JpaRepository<User,Long> {
    User findByUserId(String userId);
    User findByNickname(String nickname);
    User findByEmail(String email);
}
