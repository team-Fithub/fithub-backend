package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.comment.domain.Comment;
import com.fithub.fithubbackend.domain.board.dto.comment.CommentInfoDto;
import com.fithub.fithubbackend.domain.board.dto.comment.ParentCommentInfoDto;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.board.repository.CommentRepository;
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

    @Override
    @Transactional
    public long countComment(Post post) {
        return commentRepository.countByPostAndDeletedIsNull(post);
    }

    @Override
    @Transactional
    public long countCommentByPostId(long postId) {
        return commentRepository.countByPostIdAndDeletedIsNull(postId);
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
    public Comment getComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 댓글"));
    }

}
