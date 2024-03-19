package com.fithub.fithubbackend.domain.trainer.repository;

import com.fithub.fithubbackend.domain.trainer.domain.Trainer;
import com.fithub.fithubbackend.domain.trainer.dto.TrainerSearchFilterDto;
import com.fithub.fithubbackend.domain.user.enums.Gender;
import com.fithub.fithubbackend.global.common.Category;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.fithub.fithubbackend.domain.trainer.domain.QTrainer.trainer;
import static com.fithub.fithubbackend.domain.user.domain.QUser.user;

@Repository
public class CustomTrainerRepositoryImpl implements CustomTrainerRepository {
    private JPAQueryFactory jpaQueryFactory;

    public CustomTrainerRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Trainer> searchTrainers(TrainerSearchFilterDto filter, Pageable pageable) {
        QueryResults<Trainer> trainers = jpaQueryFactory.selectFrom(trainer)
                .where(interestEq(filter.getInterest()), genderEq((Gender) filter.getGender()), keywordContains((String) filter.getKeyword()))
                .join(trainer.user, user).fetchJoin()
                .orderBy(getOrderSpecifier(pageable).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(trainers.getResults(), pageable, trainers.getTotal());
    }

    private BooleanExpression genderEq(Gender gender) {
        return gender != null ? trainer.user.gender.eq(gender) : null;
    }

    private BooleanExpression keywordContains(String keyword) {
        return keyword != null ? trainer.name.containsIgnoreCase(keyword) : null;
    }

    private BooleanExpression interestEq(Category category) {
        return category != null ? trainer.user.interests.any().interest.eq(category) : null;
    }

    private List<OrderSpecifier> getOrderSpecifier(Pageable pageable) {

        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        for (Sort.Order order : pageable.getSort()) {
            Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

            switch (order.getProperty()) {
                case "id":
                    orderSpecifiers.add(new OrderSpecifier<>(direction, trainer.id));
                    break;
            }
        }
        return orderSpecifiers;
    }
}
