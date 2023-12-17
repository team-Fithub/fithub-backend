package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.dto.PostCreateDto;
import com.fithub.fithubbackend.domain.board.dto.PostInfoDto;
import com.fithub.fithubbackend.domain.board.dto.PostUpdateDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

public interface PostService {
    void createPost(PostCreateDto postCreateDto, UserDetails userDetails);

    void updatePost(PostUpdateDto postUpdateDto, UserDetails userDetails);
}
