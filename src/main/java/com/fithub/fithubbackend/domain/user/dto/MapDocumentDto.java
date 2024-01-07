package com.fithub.fithubbackend.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MapDocumentDto {
    private String addressName; // 전체 지번 주소
    private String phone; // 전화번호
    private String placeName; // 건물 이름
    private String placeUrl; // 장소 url
    private String roadAddressName; // 전체 도로명 주소
    private String x; // x좌표
    private String y; // y좌표
}
