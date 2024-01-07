package com.fithub.fithubbackend.domain.Training.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fithub.fithubbackend.global.domain.Document;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainingDocument{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.MERGE, optional = false)
    @JsonIgnore
    private Training training;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, optional = false, orphanRemoval = true)
    private Document document;

    @Builder
    public TrainingDocument(Training training, Document document) {
        this.training = training;
        this.document = document;
    }

}
