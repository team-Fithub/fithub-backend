package com.fithub.fithubbackend.domain.Training.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fithub.fithubbackend.domain.Training.domain.Training;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainersTrainingOutlineDto {
    private Long trainingId;
    private String title;
    private int price;
    private String location;

    private int participants;
    private int quota;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private boolean closed;

    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    @Builder
    public TrainersTrainingOutlineDto (Training training) {
        this.trainingId = training.getId();
        this.title = training.getTitle();
        this.price = training.getPrice();
        this.location = training.getLocation();
        this.participants = training.getParticipants();
        this.quota = training.getQuota();
        this.startDate = training.getStartDate();
        this.endDate = training.getEndDate();
        this.closed = training.isClosed();
        this.createdDate = training.getCreatedDate();
        this.modifiedDate = training.getModifiedDate();
    }
}
