package com.fithub.fithubbackend.domain.Training.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fithub.fithubbackend.domain.Training.dto.reservation.PaymentReqDto;
import com.fithub.fithubbackend.domain.Training.dto.reservation.ReserveReqDto;
import com.fithub.fithubbackend.domain.Training.enums.ReserveStatus;
import com.fithub.fithubbackend.domain.trainer.domain.Trainer;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReserveInfo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Trainer trainer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"trainer"})
    private Training training;

    @NotNull
    private LocalDateTime reserveDateTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ReserveStatus status;

    @NotNull
    @Comment("결제 방법")
    @Column(length = 100)
    private String payMethod;

    @NotNull
    @Comment("주문 번호")
    @Column(length = 100)
    private String impUid;

    @NotNull
    @Comment("구매 번호")
    @Column(length = 100)
    private  String merchantUid;

    @NotNull
    @ColumnDefault("0")
    private int price;

    @Builder
    public ReserveInfo(User user, Training training, ReserveReqDto dto) {
        this.user = user;
        this.trainer = training.getTrainer();
        this.training = training;
        this.reserveDateTime = dto.getReserveDateTime();
        this.status = ReserveStatus.BEFORE;
        this.price = dto.getAmount();
    }

    public void updateStatus(ReserveStatus status) {
        this.status = status;
    }

    public void updatePaymentInfo(PaymentReqDto dto) {
        this.impUid = dto.getImpUid();
        this.merchantUid = dto.getMerchantUid();
        this.payMethod = dto.getPayMethod();
    }
}
