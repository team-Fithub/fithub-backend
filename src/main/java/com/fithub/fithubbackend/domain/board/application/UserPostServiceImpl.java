package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.dto.*;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.board.repository.PostRepository;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import com.fithub.fithubbackend.global.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPostServiceImpl implements UserPostService {
    private final PostRepository postRepository;
    private final PostDocumentService postDocumentService;
    private final PostHashtagService postHashtagService;
    private final UserPostLikesService userPostLikesService;
    private final UserPostBookmarkService userPostBookmarkService;
    private final CommentService commentService;

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void createPost(PostCreateDto postCreateDto, User user) {
        // 이미지 확장자 검사
        FileUtils.isValidDocument(postCreateDto.getImages());

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
    public void updatePost(PostUpdateDto postUpdateDto, User user) {
        Post post = getPost(postUpdateDto.getId());

        if (isWriter(user, post)) {
            if (postUpdateDto.isImageChanged())
                postDocumentService.updateDocument(postUpdateDto.getEditedImages(), post);

            post.updatePost(postUpdateDto.getContent());
            postHashtagService.updateHashtag(postUpdateDto.getHashTags(), post);
        } else {
            throw new CustomException(ErrorCode.NOT_FOUND, "해당 회원은 게시글 작성자가 아님");
        }
    }

    @Override
    @Transactional
    public void deletePost(long postId, User user) {

        Post post = getPost(postId);

        if (isWriter(user, post)) {
            if (commentService.countComment(post) > 1)
                throw new CustomException(ErrorCode.UNCORRECTABLE_DATA, "댓글이 있어 게시글 삭제 불가");
            postRepository.delete(post);
        } else {
            throw new CustomException(ErrorCode.NOT_FOUND, "해당 회원은 게시글 작성자가 아님");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public LikesBookmarkStatusDto checkPostLikeAndBookmarkStatus(User user, long postId) {
        Post post = getPost(postId);
        return checkLikeAndBookmarkStatus(user, post.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LikesBookmarkStatusDto> checkPostsLikeAndBookmarkStatus(List<PostRequestDto> postRequestDtos, User user) {

        List<LikesBookmarkStatusDto> likesBookmarkStatusDtos = new ArrayList<>();
        for (PostRequestDto postRequestDto : postRequestDtos) {
            likesBookmarkStatusDtos.add(checkLikeAndBookmarkStatus(user, postRequestDto.getPostId()));
        }
        return likesBookmarkStatusDtos;
    }

    public LikesBookmarkStatusDto checkLikeAndBookmarkStatus(User user, long postId) {

        LikesBookmarkStatusDto likesBookmarkStatusDto = LikesBookmarkStatusDto.builder()
                .postId(postId).build();

        if (userPostBookmarkService.isBookmarked(user, postId))
            likesBookmarkStatusDto.updateBookmarkStatus(true);

        if (userPostLikesService.isLiked(user, postId))
            likesBookmarkStatusDto.updateLikesStatus(true);

        return likesBookmarkStatusDto;
    }

    @Transactional
    public boolean isWriter(User user, Post post) {
        if (post.getUser().getEmail().equals(user.getEmail()))
            return true;
        return false;
    }

    @Transactional
    public Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 게시글"));
    }

}
