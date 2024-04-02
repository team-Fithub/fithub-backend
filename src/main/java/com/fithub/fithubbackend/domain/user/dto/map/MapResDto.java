package com.fithub.fithubbackend.domain.user.dto.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MapResDto {

    @Schema(description = "응답 관련 정보")
    public Meta meta;

    @Schema(description = "응답 결과")
    public List<MapDocument> documents;

}
