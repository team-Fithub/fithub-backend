package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.dto.PostCreateDto;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostDocumentService postDocumentService;
    private final PostHashtagService postHashtagService;

    @Override
    @Transactional
    public void createPost(PostCreateDto postCreateDto, UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND));

        Post post = Post.builder().content(postCreateDto.getContent()).user(user).build();
        postRepository.save(post);

        if (postCreateDto.getHashTags() != null)
            postHashtagService.createOrUpdateHashtag(postCreateDto.getHashTags(), post);

        if (!postCreateDto.getImages().isEmpty()) {

            postCreateDto.getImages().forEach(image -> {
                try {
                    postDocumentService.createDocument(image, post);
                } catch (IOException e) {
                    throw new CustomException(ErrorCode.FILE_UPLOAD_ERROR);
                }
            });
        }
    }

    @Override
    public void updatePost(PostUpdateDto postUpdateDto, UserDetails userDetails) {

        Post post = postRepository.findById(postUpdateDto.getId()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 게시글"));
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 회원"));

        if (isWriter(user, post)) {
            post.updatePost(postUpdateDto.getContent());
            postDocumentService.updateDocument(postUpdateDto.getImages(), post);
            postHashtagService.createOrUpdateHashtag(postUpdateDto.getHashTags(), post);
        }
        else {
            // TODO 게시글 수정 시, 반환값 수정 필요
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
