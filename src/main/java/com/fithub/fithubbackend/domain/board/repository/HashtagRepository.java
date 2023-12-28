package com.fithub.fithubbackend.domain.board.repository;

import com.fithub.fithubbackend.global.domain.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    Optional<Hashtag> findByContent(String hashtagContent);

}
