package com.fithub.fithubbackend.domain.trainer.api;

import com.fithub.fithubbackend.domain.Training.dto.Location;
import com.fithub.fithubbackend.domain.trainer.application.UserTrainerService;
import com.fithub.fithubbackend.domain.trainer.dto.TrainerRecommendationOutlineDto;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.domain.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "user's trainer recommendation (트레이너 추천)", description = "회원이 사용하는 트레이너 추천 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserTrainerController {

    private final UserTrainerService userTrainerService;

    @Operation(summary = "트레이너 추천", description = "회원이 설정한 관심사와 위치(반경 1.5km 이내), 트레이너 평점(3.5 이상)을 토대로 트레이너 추천(정렬 기준은 평점이 제일 높은 순으로). \n 관심사가 2개 이상인 경우 랜덤으로 한 개의 관심사 추출하여 선정",
            parameters = {
                    @Parameter(name = "location", description = "위도, 경도"),
                    @Parameter(name = "size", description = "출력할 데이터의 수. 안 보낼 경우 기본 10.")
            }, responses = {
            @ApiResponse(responseCode = "200", description = "추천 완료"),
    })
    @GetMapping("/trainer/recommendation")
    public ResponseEntity<TrainerRecommendationOutlineDto> recommendTrainers(@AuthUser User user, @ModelAttribute Location location, @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok().body(userTrainerService.recommendTrainers(user, location, size));
    }
}