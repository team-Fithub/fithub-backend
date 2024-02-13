package com.fithub.fithubbackend.domain.Training.repository;

import com.fithub.fithubbackend.domain.Training.dto.reservation.QTrainersReserveInfoDto;
import com.fithub.fithubbackend.domain.Training.dto.reservation.TrainersReserveInfoDto;
import com.fithub.fithubbackend.domain.Training.enums.ReserveStatus;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.fithub.fithubbackend.domain.Training.domain.QReserveInfo.reserveInfo;
import static com.fithub.fithubbackend.domain.Training.domain.QTraining.training;
import static com.fithub.fithubbackend.domain.trainer.domain.QTrainer.trainer;

@Repository
public class CustomReserveInfoRepository {
    private JPAQueryFactory jpaQueryFactory;

    public CustomReserveInfoRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public Page<TrainersReserveInfoDto> searchTrainersReserveInfo(Long trainerId, ReserveStatus status, Pageable pageable) {
        List<TrainersReserveInfoDto> content = jpaQueryFactory.select(
                        new QTrainersReserveInfoDto(
                                reserveInfo.training.id,
                                reserveInfo.training.title,
                                reserveInfo.user.id,
                                reserveInfo.user.name,
                                reserveInfo.status,
                                reserveInfo.price,
                                reserveInfo.reserveDateTime,
                                reserveInfo.createdDate
                        )
                ).from(reserveInfo)
                .where(reserveInfo.trainer.id.eq(trainerId),
                        statusCondition(status))
                .join(reserveInfo.trainer, trainer)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(reserveInfoSort(status, pageable).toArray(OrderSpecifier[]::new))
                .fetch();

        Long count = jpaQueryFactory.select(reserveInfo.count())
                .from(reserveInfo)
                .where(reserveInfo.trainer.id.eq(trainerId),
                        statusCondition(status))
                .join(reserveInfo.trainer, trainer)
                .fetchOne();

        return new PageImpl<>(content, pageable, count);
    }

    public Page<TrainersReserveInfoDto> searchTrainersReserveInfo(Long trainerId, Long trainingId, ReserveStatus status, Pageable pageable) {
        List<TrainersReserveInfoDto> content = jpaQueryFactory.select(
                        new QTrainersReserveInfoDto(
                                reserveInfo.training.id,
                                reserveInfo.training.title,
                                reserveInfo.user.id,
                                reserveInfo.user.name,
                                reserveInfo.status,
                                reserveInfo.price,
                                reserveInfo.reserveDateTime,
                                reserveInfo.createdDate
                        )
                ).from(reserveInfo)
                .where(reserveInfo.trainer.id.eq(trainerId),
                        reserveInfo.training.id.eq(trainingId),
                        statusCondition(status))
                .join(reserveInfo.trainer, trainer)
                .join(reserveInfo.training, training)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(reserveInfoSort(status, pageable).toArray(OrderSpecifier[]::new))
                .fetch();

        Long count = jpaQueryFactory.select(reserveInfo.count())
                .from(reserveInfo)
                .where(reserveInfo.trainer.id.eq(trainerId),
                        reserveInfo.training.id.eq(trainingId),
                        statusCondition(status))
                .fetchOne();

        return new PageImpl<>(content, pageable, count);
    }

    private BooleanExpression statusCondition(ReserveStatus status) {
        return status != null ? reserveInfo.status.eq(status) : reserveInfo.status.in(ReserveStatus.START, ReserveStatus.BEFORE);
    }

    private List<OrderSpecifier> reserveInfoSort(ReserveStatus status, Pageable pageable) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        if (status == null) {
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, reserveInfo.status));
        }

        if (pageable.getSort().isEmpty()) return orderSpecifiers;

        for (Sort.Order order : pageable.getSort()) {
            Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
            switch (order.getProperty()) {
                case "id" -> orderSpecifiers.add(new OrderSpecifier<>(direction, reserveInfo.id));
                case "trainingDateTime" -> orderSpecifiers.add(new OrderSpecifier<>(direction, reserveInfo.reserveDateTime));
            }
        }
        return orderSpecifiers;
    }
}
