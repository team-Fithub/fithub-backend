package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.dto.PostCreateDto;
import com.fithub.fithubbackend.domain.board.dto.PostInfoDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

public interface PostService {
    void createPost(List<MultipartFile> image, PostCreateDto postCreateDto, UserDetails userDetails);

    void updatePost(List<MultipartFile> images, PostInfoDto postInfoDto, UserDetails userDetails);
}
