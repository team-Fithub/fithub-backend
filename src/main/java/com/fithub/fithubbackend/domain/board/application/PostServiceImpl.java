package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.dto.*;
import com.fithub.fithubbackend.domain.board.dto.likes.LikedUsersInfoDto;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.board.repository.PostRepository;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<PostInfoDto> getAllPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        Page<PostInfoDto> postInfoDtos = posts.map(PostInfoDto::toDto);
        return postInfoDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public PostInfoDto getPostDetail(long postId) {
        Post post = postRepository.findByPostIdWithFetchJoin(postId);

        if (post == null)
            throw new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 게시글");

        return PostInfoDto.toDto(post);
    }

    @Override
    @Transactional(readOnly = true)
    public LikedUsersInfoDto getLikedUsersForPostDetail(Long postId) {
        Post post = getPost(postId);
        return LikedUsersInfoDto.toDo(post);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LikedUsersInfoDto> getLikedUsersForPosts(List<PostRequestDto> postRequestDtos) {

        List<LikedUsersInfoDto> likedUsersInfoDtos = new ArrayList<>();

        for (PostRequestDto postRequestDto : postRequestDtos) {
            Post post = getPost(postRequestDto.getPostId());
            likedUsersInfoDtos.add(LikedUsersInfoDto.toDo(post));
        }

        return likedUsersInfoDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostInfoDto> searchPostsByKeyword(PostSearchFilterDto filter) {
        Page<Post> posts = postRepository.searchPostsByKeyword(filter);
        return posts.map(PostInfoDto::toDto);
    }

    @Transactional
    public Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 게시글"));
    }

}
