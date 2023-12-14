package com.fithub.fithubbackend.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
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
}
