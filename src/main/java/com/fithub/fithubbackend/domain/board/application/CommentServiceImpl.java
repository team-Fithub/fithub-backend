package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.comment.domain.Comment;
import com.fithub.fithubbackend.domain.board.dto.CommentCreateDto;
import com.fithub.fithubbackend.domain.board.dto.CommentInfoDto;
import com.fithub.fithubbackend.domain.board.dto.ParentCommentInfoDto;
import com.fithub.fithubbackend.domain.board.dto.CommentUpdateDto;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.board.repository.CommentRepository;
import com.fithub.fithubbackend.domain.board.repository.PostRepository;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    // TODO 댓글 작성 시, 게시글 작성자(+부모 댓글 작성자)에게 알림 전송
    @Override
    @Transactional
    public void createComment(CommentCreateDto commentCreateDto, User user) {

        Comment comment = Comment.builder()
                .content(commentCreateDto.getContent())
                .user(user)
                .post(getPost(commentCreateDto.getPostId()))
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
            throw new CustomException(ErrorCode.NOT_FOUND, "해당 회원은 댓글 작성자가 아님");
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
            throw new CustomException(ErrorCode.NOT_FOUND, "해당 회원은 댓글 작성자가 아님");
        }
    }

    @Override
    @Transactional
    public long countComment(Post post) {
        return commentRepository.countByPostAndDeletedIsNull(post);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ParentCommentInfoDto> getCommentsWithPage(Pageable pageable,
                                                          long postId) {
        Page<Comment> comments = commentRepository.findByPostIdAndParentIsNull(pageable, postId);
        return comments.map(ParentCommentInfoDto::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentInfoDto> getDetailComments(long parentCommentId) {

        Comment parentComment = getComment(parentCommentId);

        List<Comment> childComments = parentComment.getChildren();

        List<CommentInfoDto> commentInfoDtos = new ArrayList<>();

        if  (! childComments.isEmpty()) {
            Map<Long, CommentInfoDto> commentInfoDtoHashMap = new HashMap<>();

            childComments.forEach(childComment -> {
                if (childComment.getChildren().isEmpty())
                    commentInfoDtos.add(CommentInfoDto.toDto(childComment));
                else
                    getChildCommentsWithRecursive(parentCommentId, commentInfoDtoHashMap, commentInfoDtos, childComment);
            });
        }

        return commentInfoDtos;
    }

    public void getChildCommentsWithRecursive(long parentCommentId, Map<Long, CommentInfoDto> commentInfoDtoHashMap,
                                              List<CommentInfoDto> commentInfoDtos, Comment comment) {

        CommentInfoDto commentInfoDto = CommentInfoDto.toDto(comment);
        commentInfoDtoHashMap.put(comment.getId(), commentInfoDto);

        if (comment.getParent().getId() == parentCommentId)
            commentInfoDtos.add(commentInfoDto);
        else
            commentInfoDtoHashMap.get(comment.getParent().getId()).getChildComments().add(commentInfoDto);

        if (comment.getChildren().isEmpty())
            return;

        comment.getChildren().forEach(child-> getChildCommentsWithRecursive(parentCommentId, commentInfoDtoHashMap, commentInfoDtos, child));
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
