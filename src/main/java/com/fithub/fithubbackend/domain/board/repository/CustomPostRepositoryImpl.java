package com.fithub.fithubbackend.domain.board.repository;

import com.fithub.fithubbackend.domain.board.dto.PageableDto;
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
    public Page<Post> searchPostsByKeyword(PostSearchFilterDto filter) {
         QueryResults<Post> posts = jpaQueryFactory.selectFrom(post)
                .where(scopeEq(filter))
                 .join(post.user, user).fetchJoin()
                .orderBy(getOrderSpecifier(filter.getPageable()).toArray(OrderSpecifier[]::new))
                .offset(filter.getPageable().getPage() * filter.getPageable().getSize())
                .limit(filter.getPageable().getSize())
                .fetchResults();

        return new PageImpl<>(posts.getResults(), PageRequest.of(filter.getPageable().getPage(), filter.getPageable().getSize()), posts.getTotal());
    }

    private BooleanExpression scopeEq(PostSearchFilterDto filter) {
        if (filter.getScope().equals("content"))
            return post.content.containsIgnoreCase(filter.getKeyword());
        else if (filter.getScope().equals("writer"))
            return post.user.nickname.containsIgnoreCase(filter.getKeyword());
        else
            return post.postHashtags.any().hashtag.content.containsIgnoreCase(filter.getKeyword());
    }


    private List<OrderSpecifier> getOrderSpecifier(PageableDto pageable) {

        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();


        if (pageable == null || pageable.getSort() == null || pageable.getSort().isEmpty()) {
            orderSpecifiers.add(post.id.desc());
            return orderSpecifiers;
        }

        for (String sortProperty : pageable.getSort()) {
            Sort.Order order = Sort.Order.by(sortProperty);

            switch (order.getProperty()) {
                case "likes":
                    orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, post.likes.size()));
                    break;
                case "comments":
                    orderSpecifiers.add(new OrderSpecifier<>(Order.DESC,
                            JPAExpressions.select(post.comments.size())
                                    .from(post)
                                    .where(post.comments.any().deleted.eq(false))));
                    break;
            }
        }
        return orderSpecifiers;
    }


}
