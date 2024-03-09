package com.fithub.fithubbackend.domain.Training.api;

import com.fithub.fithubbackend.domain.Training.application.TrainingService;
import com.fithub.fithubbackend.domain.Training.dto.Location;
import com.fithub.fithubbackend.domain.Training.dto.TrainingInfoDto;
import com.fithub.fithubbackend.domain.Training.dto.TrainingOutlineDto;
import com.fithub.fithubbackend.domain.Training.dto.review.TrainingReviewDto;
import com.fithub.fithubbackend.domain.Training.dto.TrainingSearchConditionDto;
import com.fithub.fithubbackend.global.exception.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "training (트레이닝)", description = "인증이 필요없는 트레이닝 api")
@RestController
@RequestMapping("/training")
@RequiredArgsConstructor
public class TrainingController {
    private final TrainingService trainingService;

    @Operation(summary = "트레이닝 전체 조회, page 사용 (size = 9, sort=\"id\" desc 적용되어있음. swagger에서 보낼 때 따로 지정하는 거 없으면 parameter에 pageable은 다 지우고 보내도 됨)")
    @GetMapping("/all")
    public ResponseEntity<Page<TrainingOutlineDto>> searchAll(@PageableDefault(size = 9, sort = "id",  direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(trainingService.searchAll(pageable));
    }

    @Operation(summary = "트레이닝 하나 상세 조회", parameters = {
            @Parameter(name = "trainingId", description = "조회할 트레이닝의 primary key(id)")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "삭제된 트레이닝", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "해당하는 트레이닝 존재 X", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping
    public ResponseEntity<TrainingInfoDto> searchById(@RequestParam Long trainingId) {
        return ResponseEntity.ok(trainingService.searchById(trainingId));
    }

    @Operation(summary = "트레이닝 상세 조회에서 트레이닝 리뷰 리스트 조회", parameters = {
            @Parameter(name = "trainingId", description = "조회할 트레이닝의 primary key(id)")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
    })
    @GetMapping("/reviews")
    public ResponseEntity<List<TrainingReviewDto>> getTrainingReviews (@RequestParam Long trainingId) {
        return ResponseEntity.ok(trainingService.getTrainingReviews(trainingId));
    }

    @Operation(summary = "트레이닝 검색 (필터 포함)", parameters = {
            @Parameter(name = "conditions", description = "검색 조건들 (제목 키워드, 최저/최고가, 시작/마감일), 사용할 조건만 보내야됨"),
            @Parameter(name = "pageable", description = "조회할 목록의 page, size, sort(기본은 id desc(최신 생성 순) 변경 안 할거면 sort 부분은 지우기, title, startDate, endDate, price 지정 가능)")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
    })
    @PostMapping("/search")
    public ResponseEntity<Page<TrainingOutlineDto>> searchTrainingByConditions(TrainingSearchConditionDto conditions
            , @PageableDefault(size = 9, sort = "id",  direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(trainingService.searchTrainingByConditions(conditions, pageable));
    }

    @Operation(summary = "트레이닝 지정 위치로 검색", parameters = {
            @Parameter(name = "location", description = "위도, 경도")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "좌표 파싱중에 에러 발생. 좌표 다시 설정해서 보내주기"),
    })
    @PostMapping("/search/location")
    public ResponseEntity<List<TrainingOutlineDto>> searchTrainingByConditions(@RequestBody Location location) {
        return ResponseEntity.ok(trainingService.searchTrainingByLocation(location.getLatitude(), location.getLongitude()));
    }
}
