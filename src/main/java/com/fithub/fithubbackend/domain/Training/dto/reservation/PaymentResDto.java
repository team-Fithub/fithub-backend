package com.fithub.fithubbackend.domain.Training.dto.reservation;

import lombok.Builder;

@Builder
public class PaymentResDto {
    private String impUid;
    private String merchantUid;
    private String payMethod;
    private int amount;
}
