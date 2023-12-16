package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.dto.PostCreateDto;
import com.fithub.fithubbackend.domain.board.dto.PostInfoDto;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.board.post.domain.PostDocument;
import com.fithub.fithubbackend.domain.board.repository.PostDocumentRepository;
import com.fithub.fithubbackend.domain.board.repository.PostRepository;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.repository.UserRepository;
import com.fithub.fithubbackend.global.config.s3.AwsS3Uploader;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostDocumentRepository postDocumentRepository;
    private final PostHashtagService postHashtagService;

    @Value("${default.image.address}")
    private String profileImgUrl;

    private final AwsS3Uploader awsS3Uploader;

    @Override
    @Transactional
    public void createPost(List<MultipartFile> images, PostCreateDto postCreateDto, UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND));

        Post post = Post.builder().content(postCreateDto.getContent()).user(user).build();
        postRepository.save(post);

        if (postCreateDto.getHashTags() != null)
            postHashtagService.createOrUpdateHashtag(postCreateDto.getHashTags(), post);

        if (!images.isEmpty()) {
            String path = awsS3Uploader.imgPath("postDocuments");
            images.forEach(image -> {
                try {
                    createDocument(image, post, path);
                } catch (IOException e) {
                    throw new CustomException(ErrorCode.UPLODE_FAIL);
                }
            });
        }
    }

    @Override
    public void updatePost(List<MultipartFile> images, PostInfoDto postInfoDto, UserDetails userDetails) {

        Post post = postRepository.findById(postInfoDto.getId()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 게시글"));
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 회원"));

        if (isWriter(user, post)) {
            post.updatePost(postInfoDto.getContent());

            // TODO 게시글 이미지 수정

            postHashtagService.createOrUpdateHashtag(postInfoDto.getHashTags(), post);
        }
        else {
            // TODO 게시글 수정 시, 반환값 수정 필요
            throw new CustomException(ErrorCode.NOT_FOUND, "해당 회원은 게시글 작성자가 아님");
        }
    }

    @Transactional
    public void createDocument(MultipartFile image, Post post, String path) throws IOException {

        String url = awsS3Uploader.putS3(image, path);

        PostDocument postDocument = PostDocument.builder()
                .url(url)
                .inputName(image.getOriginalFilename())
                .path(path)
                .post(post).build();

        postDocumentRepository.save(postDocument);
    }

    @Transactional
    public boolean isWriter(User user, Post post) {
        if (post.getUser().getEmail().equals(user.getEmail()))
            return true;
        return false;
    }


}
