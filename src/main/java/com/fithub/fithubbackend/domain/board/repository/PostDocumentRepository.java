package com.fithub.fithubbackend.domain.board.repository;

import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.board.post.domain.PostDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostDocumentRepository extends JpaRepository<PostDocument, Long> {

    List<PostDocument> findByPost(Post post);

    void deleteByPost(Post post);
}
