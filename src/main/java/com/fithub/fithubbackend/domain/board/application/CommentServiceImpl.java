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
import java.util.stream.Collectors;

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

        // 1. 최상위 댓글 가져오기
        Comment parentComment = getComment(parentCommentId);

        // 2. 댓글의 자식 답글 가져오기
        List<Comment> childComments = parentComment.getChildren();

        // 3. 최상위 댓글의 자식 답글을 넣을 list 선언
        List<CommentInfoDto> result = new ArrayList<>();

        // 4. 재귀 함수를 이용하여 답글의 답글까지 모두 가져와 result에 추가
        childComments.forEach(childComment -> {
            if (childComment.getChildren().isEmpty())
                result.add(CommentInfoDto.toDto(childComment));
            else
                getChildCommentsWithRecursive(result, childComment);
        });

        // 5. 마지막으로 comment 아이디로 정렬 후 반환
        return result.stream().sorted(Comparator.comparing(CommentInfoDto::getCommentId))
                .collect(Collectors.toList());
    }

    public void getChildCommentsWithRecursive(List<CommentInfoDto> result, Comment parentComment) {
        result.add(CommentInfoDto.toDto(parentComment));
        if (parentComment.getChildren().isEmpty())
            return;
        parentComment.getChildren().forEach(child-> getChildCommentsWithRecursive(result, child));
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
