package com.fithub.fithubbackend.domain.board.repository;

import com.fithub.fithubbackend.domain.board.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

//    @EntityGraph(attributePaths = {"user", "user.profileImg", "likes", "postHashtags", "postDocuments", "postHashtags.hashtag", "comments"})
//    @Query(value = "SELECT post " +
//            "FROM Post post " +
//            "WHERE post.id = :postId")
//    Post findPostWithHashtags(@Param("postId") long postId);

    @Query(value = "SELECT post " +
            "FROM Post post " +
            "JOIN FETCH post.user user " +
            "JOIN FETCH user.profileImg document " +
            "JOIN FETCH post.postHashtags postHashtag " +
            "JOIN FETCH postHashtag.hashtag hashtag " +
            "WHERE post.id = :postId")
    Post findByPostIdWithFetchJoin(@Param("postId") long postId);
}
