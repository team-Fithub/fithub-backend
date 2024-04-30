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
import com.fithub.fithubbackend.global.notify.NotificationType;
import com.fithub.fithubbackend.global.notify.dto.NotifyRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserCommentServiceImpl implements UserCommentService {
    private final ApplicationEventPublisher eventPublisher;
    private final CommentRepository commentRepository;
    private final PostService postService;

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
        sendNotify(comment);

    }

    private void sendNotify(Comment comment) {

        Long postWriter = comment.getPost().getUser().getId();
        Long commentWriter = comment.getUser().getId();
        Long parentCommentWriter = comment.getParent() == null ? null : comment.getParent().getUser().getId();

        if (parentCommentWriter != null) {
            if (postWriter != commentWriter && parentCommentWriter != commentWriter && postWriter != parentCommentWriter) {
                eventPublisher.publishEvent(createCommentNotifyRequestForPostWriter(comment));
                eventPublisher.publishEvent(createCommentNotifyRequestForParentCommentWriter(comment));
            }
            else if(postWriter != commentWriter && postWriter == parentCommentWriter)
                eventPublisher.publishEvent(createCommentNotifyRequestForPostWriter(comment));
            else if(postWriter != commentWriter && commentWriter == parentCommentWriter)
                eventPublisher.publishEvent(createCommentNotifyRequestForPostWriter(comment));
            else if(postWriter == commentWriter && commentWriter != parentCommentWriter)
                eventPublisher.publishEvent(createCommentNotifyRequestForParentCommentWriter(comment));

        } else {
            if (commentWriter != postWriter)
                eventPublisher.publishEvent(createCommentNotifyRequestForPostWriter(comment));
        }
    }

    private NotifyRequestDto createCommentNotifyRequestForPostWriter(Comment comment) {
        return NotifyRequestDto.builder()
                .receiver(comment.getPost().getUser())
                .content(comment.getUser().getNickname() + "님이 회원님의 게시글에 댓글을 남겼습니다.")
                .urlId(comment.getPost().getId())
                .type(NotificationType.COMMENT)
                .build();
    }

    private NotifyRequestDto createCommentNotifyRequestForParentCommentWriter(Comment comment) {
        return NotifyRequestDto.builder()
                .receiver(comment.getParent().getUser())
                .content(comment.getUser().getNickname() + "님이 회원님의 댓글에 답글을 남겼습니다. ")
                .urlId(comment.getPost().getId())
                .type(NotificationType.COMMENT)
                .build();
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
