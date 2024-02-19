package com.fithub.fithubbackend.domain.board.repository;

import com.fithub.fithubbackend.domain.board.dto.PostSearchFilterDto;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import org.springframework.data.domain.Page;

public interface CustomPostRepository {

    Page<Post> searchPostsByKeyword(PostSearchFilterDto postSearchFilterDTO);

}
