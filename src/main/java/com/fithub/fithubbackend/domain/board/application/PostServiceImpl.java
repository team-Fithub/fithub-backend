package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.dto.PostCreateDto;
import com.fithub.fithubbackend.domain.board.dto.PostDocumentUpdateDto;
import com.fithub.fithubbackend.domain.board.dto.PostUpdateDto;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.board.repository.PostRepository;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.repository.UserRepository;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostDocumentService postDocumentService;
    private final PostHashtagService postHashtagService;

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void createPost(PostCreateDto postCreateDto, UserDetails userDetails) throws IOException {

        // 이미지 확장자 검사
        postDocumentService.isValidDocument(postCreateDto.getImages());

        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 회원"));

        Post post = Post.builder().content(postCreateDto.getContent()).user(user).build();
        postRepository.save(post);

        if (postCreateDto.getHashTags() != null)
            postHashtagService.createPostHashtag(postCreateDto.getHashTags(), post);

        postCreateDto.getImages().forEach(image -> {
            try {
                postDocumentService.createDocument(image, post);
            } catch (IOException e) {
                throw new CustomException(ErrorCode.FILE_UPLOAD_ERROR);
            }
        });
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void updatePost(PostUpdateDto postUpdateDto, UserDetails userDetails) throws IOException {

        Post post = postRepository.findById(postUpdateDto.getId()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 게시글"));
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 회원"));

        if (isWriter(user, post)) {
            if (postUpdateDto.isImageChanged()) {
                List<MultipartFile> multipartFileList = postUpdateDto.getEditedImages().stream().map(PostDocumentUpdateDto::getImage).toList();

                postDocumentService.isValidDocument(multipartFileList);
                postDocumentService.updateDocument(postUpdateDto.getEditedImages(), post);
            }
            post.updatePost(postUpdateDto.getContent());
            postHashtagService.updateHashtag(postUpdateDto.getHashTags(), post);
        } else {
            throw new CustomException(ErrorCode.NOT_FOUND, "해당 회원은 게시글 작성자가 아님");
        }
    }


    @Transactional
    public boolean isWriter(User user, Post post) {
        if (post.getUser().getEmail().equals(user.getEmail()))
            return true;
        return false;
    }


}
