package com.fithub.fithubbackend.domain.Training.repository;

import com.fithub.fithubbackend.domain.Training.domain.Training;
import com.fithub.fithubbackend.domain.Training.dto.TrainingSearchConditionDto;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

import static com.fithub.fithubbackend.domain.Training.domain.QTraining.training;

@Repository
public class CustomTrainingRepository {
    private JPAQueryFactory jpaQueryFactory;

    public CustomTrainingRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public Page<Training> searchByConditions(TrainingSearchConditionDto conditions, Pageable pageable) {
        List<Training> content = jpaQueryFactory.selectFrom(training)
                .where(training.closed.isFalse(),
                        keywordContains(conditions.getKeyword()),
                        startDateGoe(conditions.getStartDate()),
                        endDateLoe(conditions.getEndDate()),
                        priceBetween(conditions.getLowestPrice(), conditions.getHighestPrice())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(trainingSort(pageable))
                .fetch();

        Long count = jpaQueryFactory.select(training.count())
                .from(training)
                .where(training.closed.isFalse(),
                        keywordContains(conditions.getKeyword()),
                        startDateGoe(conditions.getStartDate()),
                        endDateLoe(conditions.getEndDate()),
                        priceBetween(conditions.getLowestPrice(), conditions.getHighestPrice())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, count);
    }

    private BooleanExpression keywordContains(String keyword) {
        return StringUtils.hasText(keyword) ? training.title.containsIgnoreCase(keyword) : null;
    }

    private BooleanExpression startDateGoe(LocalDate startDate) {
        return startDate != null ? training.startDate.goe(startDate) : null;
    }

    private BooleanExpression endDateLoe(LocalDate endDate) {
        return endDate != null ? training.endDate.loe(endDate) : null;
    }

    private BooleanExpression priceBetween(Integer lowestPrice, Integer highestPrice) {
        return lowestPrice != 0 || highestPrice != 0 ? training.price.between(lowestPrice, highestPrice) : null;
    }

    private OrderSpecifier<?> trainingSort(Pageable pageable) {
        if (pageable.getSort().isEmpty()) return null;

        for (Sort.Order order : pageable.getSort()) {
            Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
            switch (order.getProperty()) {
                case "id":
                    return new OrderSpecifier<>(direction, training.id);
                case "title":
                    return new OrderSpecifier<>(direction, training.title);
                case "startDate":
                    return new OrderSpecifier<>(direction, training.startDate);
                case "endDate":
                    return new OrderSpecifier<>(direction, training.endDate);
                case "price":
                    return new OrderSpecifier<>(direction, training.price);
            }
        }
        return null;
    }
}
