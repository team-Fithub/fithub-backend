package com.fithub.fithubbackend.domain.board.repository;

import com.fithub.fithubbackend.domain.board.post.domain.PostDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostDocumentRepository extends JpaRepository<PostDocument, Long> {

}
