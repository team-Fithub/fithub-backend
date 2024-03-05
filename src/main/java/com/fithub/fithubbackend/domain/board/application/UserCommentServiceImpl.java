package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.comment.domain.Comment;
import com.fithub.fithubbackend.domain.board.dto.PostInfoDto;
import com.fithub.fithubbackend.domain.board.dto.comment.CommentCreateDto;
import com.fithub.fithubbackend.domain.board.dto.comment.CommentUpdateDto;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.board.repository.CommentRepository;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserCommentServiceImpl implements UserCommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;

    // TODO 댓글 작성 시, 게시글 작성자(+부모 댓글 작성자)에게 알림 전송
    @Override
    @Transactional
    public void createComment(CommentCreateDto commentCreateDto, User user) {

        Comment comment = Comment.builder()
                .content(commentCreateDto.getContent())
                .user(user)
                .post(postService.getPost(commentCreateDto.getPostId()))
                .parent(commentCreateDto.getParentCommentId() == null ? null : getComment(commentCreateDto.getParentCommentId()))
                .build();
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void updateComment(CommentUpdateDto commentUpdateDto, User user) {

        Comment comment = getComment(commentUpdateDto.getCommentId());

        if (isWriter(user, comment)) {
            comment.updateContent(commentUpdateDto.getContent());
        }
        else {
            throw new CustomException(ErrorCode.PERMISSION_DENIED, "해당 회원은 댓글 작성자가 아님");
        }
    }

    @Override
    @Transactional
    public void deleteComment(long commentId, User user) {

        Comment comment = getComment(commentId);

        if (isWriter(user, comment)) {
            if (comment.getParent() == null)  {     // 최상위 댓글 삭제 시, 그 아래 댓글들 모두 삭제
                commentRepository.deleteByParentAndPost(comment, comment.getPost());
                commentRepository.delete(comment);
            }
            else {
                if (comment.getChildren().isEmpty())
                    commentRepository.delete(comment);
                else
                    comment.deleteComment();
            }
        }
        else {
            throw new CustomException(ErrorCode.PERMISSION_DENIED, "해당 회원은 댓글 작성자가 아님");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostInfoDto> getPostsByUserAndComments(Pageable pageable, User user) {
        Page<Post> posts = commentRepository.findPostsByUserAndComments(pageable, user);
        return posts.map(PostInfoDto::toDto);
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
