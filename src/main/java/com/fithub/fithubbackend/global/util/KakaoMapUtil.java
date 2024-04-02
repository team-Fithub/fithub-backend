package com.fithub.fithubbackend.global.util;

import com.fithub.fithubbackend.domain.user.dto.map.MapResDto;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class KakaoMapUtil {
    private final RestTemplate restTemplate;
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String key;

    public String getAddressByFitness(int page,double x, double y) {
        URI uri = UriComponentsBuilder
                .newInstance()
                .scheme("https")
                .host("dapi.kakao.com")
                .path("/v2/local/search/keyword.json")
                .queryParam("query", "헬스클럽")
                .queryParam("page",page)
                .queryParam("x",x)
                .queryParam("y",y)
                .encode()
                .build()
                .toUri();

        RequestEntity requestEntity = RequestEntity
                .get(uri)
                .header("Authorization","KakaoAK " + key)
                .build();
        ResponseEntity<String> result = restTemplate.exchange(requestEntity, String.class);
        return result.getBody();
    }

    public String getRegionAddress(double x, double y) {
        String url = "https://dapi.kakao.com/v2/local/geo/coord2regioncode.json";

        UriComponents  uri = UriComponentsBuilder.newInstance()
                .fromHttpUrl(url)
                .queryParam("x",x)
                .queryParam("y",y)
                .build();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "KakaoAK " + key);

        HttpEntity requestMessage = new HttpEntity(httpHeaders);

        ResponseEntity response = restTemplate.exchange(uri.toUriString(), HttpMethod.GET, requestMessage, String.class);

        Gson gson = new Gson();

        MapResDto mapped_data = gson.fromJson(response.getBody().toString(), MapResDto.class);
        String target = mapped_data.documents.get(0).address_name;
        return target;
    }
}
