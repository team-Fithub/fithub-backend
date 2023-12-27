package com.fithub.fithubbackend.domain.Training.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class TrainingCancelOrRefundDto {
    private Long id;
    private String title;
    private int price;
    @JsonFormat(pattern = "yyyy.MM.dd(E)")
    private LocalDateTime createdDate;
}
