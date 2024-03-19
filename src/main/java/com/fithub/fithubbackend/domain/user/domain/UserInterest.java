package com.fithub.fithubbackend.domain.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fithub.fithubbackend.global.common.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Category interest;

    @Builder
    public UserInterest(User user, Category interest) {
        this.user = user;
        this.interest = interest;
    }
}
