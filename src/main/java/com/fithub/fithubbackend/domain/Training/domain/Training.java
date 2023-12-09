package com.fithub.fithubbackend.domain.Training.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fithub.fithubbackend.domain.Training.dto.TrainingCreateDto;
import com.fithub.fithubbackend.domain.trainer.domain.Trainer;
import com.fithub.fithubbackend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Training extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Trainer trainer;

    @NotNull
    @Size(min = 2, max = 100)
    private String title;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String content;

    @NotNull
    private boolean closed;

    @NotNull
    private String location;

    @NotNull
    @ColumnDefault("1")
    @Min(1)
    private int quota;

    @NotNull
    @ColumnDefault("0")
    private int price;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotNull
    private LocalTime startHour;

    @NotNull
    private LocalTime endHour;

    @OneToMany(mappedBy = "training", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"training"})
    private List<AvailableDate> availableDates;

    @Builder
    public Training(TrainingCreateDto dto, Trainer trainer) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.closed = false;
        this.location = dto.getLocation();
        this.quota = dto.getQuota();
        this.price = dto.getPrice();
        this.startDate = dto.getStartDate();
        this.endDate = dto.getEndDate();
        this.startHour = dto.getStartHour();
        this.endHour = dto.getEndHour();
        this.trainer = trainer;
        this.availableDates = new ArrayList<>();
    }

    public void updateTraining(TrainingCreateDto dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.location = dto.getLocation();
        this.quota = dto.getQuota();
        this.price = dto.getPrice();
        this.startDate = dto.getStartDate();
        this.endDate = dto.getEndDate();
        this.startHour = dto.getStartHour();
        this.endHour = dto.getEndHour();
    }

    public void updateClosed(boolean closed) {
        this.closed = closed;
    }
}
