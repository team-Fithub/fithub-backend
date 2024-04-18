package com.fithub.fithubbackend.domain.user.repository;

import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.domain.UserInterest;
import com.fithub.fithubbackend.global.common.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {
    List<UserInterest> findByUser(User user);
    List<UserInterest> findByUserId(Long userId);
    void deleteByUser(User user);
    @Modifying
    @Query("delete from UserInterest u where u.user = :user and u.interest in :interests")
    void deleteAllByUserAndInterests(@Param("user") User user, @Param("interests")List<Category> interests);
}
