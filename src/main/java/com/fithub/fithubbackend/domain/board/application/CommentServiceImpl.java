package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.comment.domain.Comment;
import com.fithub.fithubbackend.domain.board.dto.CommentCreateDto;
import com.fithub.fithubbackend.domain.board.dto.CommentInfoDto;
import com.fithub.fithubbackend.domain.board.dto.CommentUpdateDto;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.board.repository.CommentRepository;
import com.fithub.fithubbackend.domain.board.repository.PostRepository;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.repository.UserRepository;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

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
    @Transactional
    public List<CommentInfoDto> getComments(Post post) {

        List<CommentInfoDto> commentInfoDtoList  = new ArrayList<>();
        List<Comment> comments = commentRepository.findByPostWithFetch(post);
        Map<Long, CommentInfoDto> CommentInfoDtoHashMap = new HashMap<>();

        comments.forEach(c -> {
            CommentInfoDto commentInfoDto = CommentInfoDto.fromEntity(c);
            CommentInfoDtoHashMap.put(commentInfoDto.getCommentId(), commentInfoDto);
            if (c.getParent() != null)
                CommentInfoDtoHashMap.get(c.getParent().getId()).getChildComment().add(commentInfoDto);
            else commentInfoDtoList.add(commentInfoDto);
        });

        return commentInfoDtoList;
    }

    @Override
    @Transactional
    public List<CommentInfoDto> getCommentsVer2(Post post) {

        List<CommentInfoDto> commentInfoDtoList  = new ArrayList<>();

        List<Comment> comments = commentRepository.findByPostWithFetch(post);

        Map<Long, CommentInfoDto> commentInfoDtoHashMap = new HashMap<>();

        comments.forEach(c -> {
            CommentInfoDto commentInfoDto = CommentInfoDto.fromEntity(c);
            commentInfoDtoHashMap.put(commentInfoDto.getCommentId(), commentInfoDto);

            if (c.getParent() != null)
                findParentComment(commentInfoDtoHashMap, c.getParent().getId(), commentInfoDto);
            else commentInfoDtoList.add(commentInfoDto);
        });

        return commentInfoDtoList;
    }

    @Transactional
    public void findParentComment(Map<Long, CommentInfoDto> commentInfoDtoHashMap, Long parentId, CommentInfoDto childComment) {

        CommentInfoDto parentComment = commentInfoDtoHashMap.get(parentId);

        if (parentComment.getParentCommentId() == null) {
            parentComment.getChildComment().add(childComment);
            return;
        }

        findParentComment(commentInfoDtoHashMap, parentComment.getParentCommentId(), childComment);

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
