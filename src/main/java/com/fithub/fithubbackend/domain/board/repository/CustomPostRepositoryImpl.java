package com.fithub.fithubbackend.domain.board.repository;

import com.fithub.fithubbackend.domain.board.dto.PostSearchFilterDto;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static com.fithub.fithubbackend.domain.board.post.domain.QPost.post;
import static com.fithub.fithubbackend.domain.board.comment.domain.QComment.comment;
import static com.fithub.fithubbackend.domain.user.domain.QUser.user;

import java.util.*;

@Repository
@Slf4j
public class CustomPostRepositoryImpl implements CustomPostRepository {

    private JPAQueryFactory jpaQueryFactory;

    public CustomPostRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }


    @Override
    @Transactional(readOnly = true)
    public Page<Post> searchPostsByKeyword(PostSearchFilterDto filter, Pageable pageable) {
         QueryResults<Post> posts = jpaQueryFactory.selectFrom(post)
                .where(scopeEq(filter))
                 .join(post.user, user).fetchJoin()
                .orderBy(getOrderSpecifier(pageable).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(posts.getResults(), pageable, posts.getTotal());
    }

    private BooleanExpression scopeEq(PostSearchFilterDto filter) {
        if (filter.getKeyword() == null)
            return null;

        if (filter.getScope() == null || filter.getScope().equals("content"))
            return post.content.containsIgnoreCase(filter.getKeyword());
        else if (filter.getScope().equals("writer"))
            return post.user.nickname.containsIgnoreCase(filter.getKeyword());
        else
            return post.postHashtags.any().hashtag.content.containsIgnoreCase(filter.getKeyword());
    }


    private List<OrderSpecifier> getOrderSpecifier(Pageable pageable) {

        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        for (Sort.Order order : pageable.getSort()) {
            Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

            switch (order.getProperty()) {
                case "id":
                    orderSpecifiers.add(new OrderSpecifier<>(direction, post.id));
                    break;
                case "likes":
                    orderSpecifiers.add(new OrderSpecifier<>(direction, post.likes.size()));
                    break;
                case "comments":
                    orderSpecifiers.add(
                            new OrderSpecifier<>(direction,
                                    JPAExpressions.select(comment.count())
                                            .from(comment)
                                            .where(comment.post.eq(post).and(comment.deleted.isNull()))));
                    break;
            }
        }
        return orderSpecifiers;
    }


}
