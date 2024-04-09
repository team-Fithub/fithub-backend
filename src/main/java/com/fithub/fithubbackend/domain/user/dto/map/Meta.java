package com.fithub.fithubbackend.domain.user.dto.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Schema(description = "응답 관련 정보")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Meta {

    @Schema(description = "검색어에 검색된 문서 수")
    public int total_count;

}
