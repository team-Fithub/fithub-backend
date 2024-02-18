package com.fithub.fithubbackend.domain.Training.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Where;

import java.time.LocalTime;

@Entity
@Where(clause = "deleted = false")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AvailableTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private AvailableDate availableDate;

    @NotNull
    private LocalTime time;

    @NotNull
    @ColumnDefault("true")
    private boolean enabled;

    private boolean deleted;

    @Builder
    public AvailableTime (LocalTime time, AvailableDate date, boolean enabled) {
        this.time = time;
        this.availableDate = date;
        this.enabled = enabled;
    }

    public void closeTime() {
        this.enabled = false;
    }

    public void openTime() {
        this.enabled = true;
    }

    public void deleteTime() {
        this.deleted = true;
    }
}
