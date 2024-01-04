package com.fithub.fithubbackend.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class MapDto {
    private List<MapDocumentDto> mapDocumentDto;
    private boolean isEnd; // 마지막 페이지 여부
}
