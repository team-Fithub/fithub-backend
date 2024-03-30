package com.fithub.fithubbackend.domain.user.dto.map;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "응답 결과")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MapDocument {

    @Schema(description = "H(행정동) 또는 B(법정동)")
    public String region_type;

    @Schema(description = "region 코드")
    public String code;

    @Schema(description = "전체 지역 명칭")
    public String address_name;

    @Schema(description = "지역 1Depth, 시도 단위")
    public String region_1depth_name;

    @Schema(description = "지역 2Depth, 구 단위")
    public String region_2depth_name;

    @Schema(description = "지역 3Depth, 동 단위")
    public String region_3depth_name;

    @Schema(description = "지역 4Depth. region_type 이 법정동이며, 리 영역인 경우만 존재")
    public String region_4depth_name;

    @Schema(description = "경도(longitude)")
    public double x;

    @Schema(description = " 위도(latitude)")
    public double y;
}
