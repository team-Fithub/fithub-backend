package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.comment.domain.Comment;
import com.fithub.fithubbackend.domain.board.dto.CommentCreateDto;
import com.fithub.fithubbackend.domain.board.dto.CommentUpdateDto;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.board.repository.CommentRepository;
import com.fithub.fithubbackend.domain.board.repository.PostRepository;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.repository.UserRepository;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void createComment(CommentCreateDto commentCreateDto, UserDetails userDetails) {

        Comment comment = Comment.builder()
                .content(commentCreateDto.getContent())
                .user(getUser(userDetails))
                .post(getPost(commentCreateDto.getPostId()))
                .parent(commentCreateDto.getParentCommentId() == null ? null : getComment(commentCreateDto.getParentCommentId()))
                .build();
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void updateComment(CommentUpdateDto commentUpdateDto, UserDetails userDetails) {

        Comment comment = getComment(commentUpdateDto.getCommentId());

        if (isWriter(getUser(userDetails), comment)) {
            comment.setContent(commentUpdateDto.getContent());
        }
        else {
            throw new CustomException(ErrorCode.NOT_FOUND, "해당 회원은 댓글 작성자가 아님");
        }
    }

    @Override
    @Transactional
    public void deleteComment(long commentId, UserDetails userDetails) {

        Comment comment = getComment(commentId);

        if (isWriter(getUser(userDetails), comment)) {
            if (comment.getParent() == null)  {     // 최상위 댓글 삭제 시, 그 아래 댓글들 모두 삭제
                commentRepository.deleteByParentAndPost(comment, comment.getPost());
            }
            commentRepository.delete(comment);
        }
        else {
            throw new CustomException(ErrorCode.NOT_FOUND, "해당 회원은 댓글 작성자가 아님");
        }
    }

    @Override
    @Transactional
    public long countComment(Post post) {
        return commentRepository.countByPost(post);
    }

    @Override
    @Transactional
    public void deleteComments(Post post) {

    }

    @Transactional
    public User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 회원"));
    }

    @Transactional
    public Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 게시글"));
    }

    @Transactional
    public Comment getComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 댓글"));
    }

    @Transactional
    public boolean isWriter(User user, Comment comment) {
        if (comment.getUser().getEmail().equals(user.getEmail()))
            return true;
        return false;
    }
}
