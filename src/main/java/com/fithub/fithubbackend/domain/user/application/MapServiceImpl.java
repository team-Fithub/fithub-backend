package com.fithub.fithubbackend.domain.user.application;

import com.fithub.fithubbackend.domain.user.dto.MapDocumentDto;
import com.fithub.fithubbackend.domain.user.dto.MapDto;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import com.fithub.fithubbackend.global.util.KakaoMapUtil;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MapServiceImpl implements MapService{
    private final KakaoMapUtil kakaoMapUtil;
    @Override
    public MapDto getLocationByFitness(int page, double x, double y) {
        List<MapDocumentDto> documentList = new ArrayList<>();
        boolean isEnd;
        String fitness = kakaoMapUtil.getAddressByFitness(page,x,y);
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(fitness);
            JSONObject metaObject = (JSONObject) jsonObject.get("meta");
            JSONArray documentsList = (JSONArray) jsonObject.get("documents");

            isEnd = (boolean) metaObject.get("is_end");

            for (Object documentsObject : documentsList) {
                JSONObject documents = (JSONObject) documentsObject;
                documentList.add(MapDocumentDto.builder()
                        .addressName((String) documents.get("address_name"))
                        .phone((String) documents.get("phone"))
                        .placeName((String) documents.get("place_name"))
                        .placeUrl((String) documents.get("place_url"))
                        .roadAddressName((String) documents.get("road_address_name"))
                        .x((String) documents.get("x"))
                        .y((String) documents.get("y"))
                        .build());
                }

        }catch(ParseException e){
            throw new CustomException(ErrorCode.PARSING_ERROR,ErrorCode.PARSING_ERROR.getMessage());
        }
        MapDto result = MapDto.builder()
                .mapDocumentDto(documentList)
                .isEnd(isEnd)
                .build();
        return result;
    }
}
