package com.fithub.fithubbackend.domain.Training.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fithub.fithubbackend.domain.Training.dto.trainersTraining.TrainingContentUpdateDto;
import com.fithub.fithubbackend.domain.Training.dto.trainersTraining.TrainingCreateDto;
import com.fithub.fithubbackend.domain.trainer.domain.Trainer;
import com.fithub.fithubbackend.domain.trainer.domain.TrainerCareer;
import com.fithub.fithubbackend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.locationtech.jts.geom.Point;

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

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JsonIgnore
    private Trainer trainer;

    @NotNull
    private String address;

    @Column(columnDefinition = "point")
    private Point point;

    @NotNull
    @Size(min = 2, max = 100)
    private String title;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "training", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"training"})
    private List<TrainingDocument> images;

    @OneToMany(mappedBy = "training", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"training"})
    private List<TrainingCategory> categories;

    @NotNull
    private boolean closed;

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

    @OneToMany(mappedBy = "training", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnoreProperties({"training"})
    @OrderBy(value = "date")
    private List<AvailableDate> availableDates;

    @NotNull
    private boolean deleted;

    @Builder
    public Training(TrainingCreateDto dto, Trainer trainer) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.closed = false;
        this.price = dto.getPrice();
        this.startDate = dto.getStartDate();
        this.endDate = dto.getEndDate();
        this.startHour = dto.getStartHour();
        this.endHour = dto.getEndHour();
        this.trainer = trainer;
        this.address = trainer.getAddress();
        this.point = trainer.getPoint();
        this.availableDates = new ArrayList<>();
        this.images = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.deleted = false;
    }

    public void updateTraining(TrainingContentUpdateDto dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.price = dto.getPrice();
    }

    public void updateClosed(boolean closed) {
        this.closed = closed;
    }

    public void executeDelete() {
        this.deleted = true;
        this.closed = true;
        this.getAvailableDates().forEach(AvailableDate::deleteDate);
    }

    public void addImages(TrainingDocument document) {
        this.images.add(document);
    }

    public void removeImage(TrainingDocument document) {
        this.images.remove(document);
    }

    public void removeDate(AvailableDate date) {
        this.availableDates.remove(date);
    }

    public void updateStartAndEndDate(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void updateAddress(TrainerCareer trainerCareer) {
        this.address = trainerCareer.getAddress();
        this.point = trainerCareer.getPoint();
    }

    public void addCategory(TrainingCategory category) {
        this.categories.add(category);
    }

    public void removeCategory(TrainingCategory category) {
        this.categories.remove(category);
    }

}
