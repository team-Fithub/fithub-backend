package com.fithub.fithubbackend.domain.board.repository;

import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.board.post.domain.PostDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostDocumentRepository extends JpaRepository<PostDocument, Long> {

    List<PostDocument> findByPost(Post post);

    void deleteByUrl(String url);

    PostDocument findByUrl(String url);

    @Query("SELECT pd.url FROM PostDocument pd WHERE pd.post = :post")
    List<String> findUrlsByPost(@Param("post") Post post);

    List<PostDocument> findBySizeAndInputNameAndPost(long size, String inputName, Post post);

}
