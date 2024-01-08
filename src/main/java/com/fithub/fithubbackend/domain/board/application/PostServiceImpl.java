package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.dto.PostCreateDto;
import com.fithub.fithubbackend.domain.board.dto.PostInfoDto;
import com.fithub.fithubbackend.domain.board.dto.PostUpdateDto;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.board.repository.PostRepository;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import com.fithub.fithubbackend.global.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostDocumentService postDocumentService;
    private final PostHashtagService postHashtagService;
    private final LikesService likesService;
    private final BookmarkService bookmarkService;
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
    public void likesPost(long postId, User user) {
        Post post = getPost(postId);

        likesService.addLikes(user, post);
    }

    @Override
    @Transactional
    public void createBookmark(long postId, User user) {
        Post post = getPost(postId);

        bookmarkService.addBookmark(user, post);
    }

    @Override
    @Transactional
    public void notLikesPost(long postId, User user) {
        Post post = getPost(postId);

        likesService.deleteLikes(user, post);
    }

    @Override
    @Transactional
    public void deleteBookmark(long postId, User user) {
        Post post = getPost(postId);

        bookmarkService.deleteBookmark(user, post);
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
    public Page<PostInfoDto> getAllPosts(Pageable pageable, User user) {

        Page<Post> posts = postRepository.findAll(pageable);
        Page<PostInfoDto> postInfoDtos = posts.map(PostInfoDto::fromEntity);

        if (user != null) {

            postInfoDtos.forEach(postInfoDto -> {
                if (!postInfoDto.getPostLikedUser().isEmpty() && postInfoDto.getPostLikedUser() != null ) {
                    List<String> likedUsers = postInfoDto.getPostLikedUser().stream().map(likesInfoDto -> likesInfoDto.getLikedUser()).collect(Collectors.toList());
                    if (likedUsers.contains(user.getNickname()))
                        postInfoDto.checkLikes(true);
                }
            });

            postInfoDtos.forEach(postInfoDto -> {
                if (!postInfoDto.getPostBookmarkedUser().isEmpty() && postInfoDto.getPostBookmarkedUser() != null ) {
                    List<String> bookmarkedUsers = postInfoDto.getPostBookmarkedUser().stream().map(bookmark -> bookmark.getUser().getNickname()).collect(Collectors.toList());
                    if (bookmarkedUsers.contains(user.getNickname()))
                        postInfoDto.checkBookmark(true);
                }

            });

        }
        return postInfoDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public PostInfoDto getPostDetail(long postId, User user) {

        Post post = getPost(postId);

        PostInfoDto postInfoDto = PostInfoDto.fromEntity(post);

        if (user != null) {
            if(post.getLikes() != null && !post.getLikes().isEmpty()) {
                List<String> likedUsers = postInfoDto.getPostLikedUser().stream().map(likesInfoDto -> likesInfoDto.getLikedUser()).collect(Collectors.toList());
                if (likedUsers.contains(user.getNickname()))
                    postInfoDto.checkLikes(true);
            }

            if (!post.getBookmarks().isEmpty() && post.getBookmarks() != null ) {
                List<String> bookmarkedUsers = postInfoDto.getPostBookmarkedUser().stream().map(bookmark -> bookmark.getUser().getNickname()).collect(Collectors.toList());
                if (bookmarkedUsers.contains(user.getNickname()))
                    postInfoDto.checkBookmark(true);
            }
        }

        if (post.getComments() != null && !post.getComments().isEmpty())
//            postInfoDto.setComment(commentService.getComments(post));
            postInfoDto.setComment(commentService.getCommentsVer2(post));

        return postInfoDto;
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
