package com.fithub.fithubbackend.domain.user.application;

import com.fithub.fithubbackend.domain.user.dto.constants.MapDto;

import java.util.List;

public interface MapService {
    MapDto getLocationByFitness(int page,double x, double y);
}
