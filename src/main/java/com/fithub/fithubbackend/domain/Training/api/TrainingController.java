package com.fithub.fithubbackend.domain.Training.api;

import com.fithub.fithubbackend.domain.Training.application.TrainingService;
import com.fithub.fithubbackend.domain.Training.dto.TrainingCreateDto;
import com.fithub.fithubbackend.global.exception.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/training")
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingService trainingService;

    @Operation(summary = "트레이닝 생성", responses = {
            @ApiResponse(responseCode = "200", description = "생성됨"),
            @ApiResponse(responseCode = "409", description = "예약 가능 날짜에 현재보다 이전 날짜가 들어있음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    }, parameters = {
            @Parameter(name="trainerId", description = "트레이닝을 생성하는 트레이너의 primary key(id)")
    })
    @PostMapping
    public ResponseEntity<Long> createTraining(@RequestBody @Valid TrainingCreateDto dto, @RequestParam Long trainerId) {
        return ResponseEntity.ok(trainingService.createTraining(dto, trainerId));
    }

    @Operation(summary = "트레이닝 수정", responses = {
            @ApiResponse(responseCode = "200", description = "수정됨"),
            @ApiResponse(responseCode = "409", description = "마감 처리 된 트레이닝을 수정하려고 함", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "수정 날짜에 현재보다 이전 날짜가 들어있음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PutMapping
    public ResponseEntity<Long> updateTraining(@RequestBody @Valid TrainingCreateDto dto, @RequestParam Long trainerId) {
        // TODO: 예약 관련 기능 끝난 후 수정 필요
        return ResponseEntity.ok(trainingService.updateTraining(dto, trainerId));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteTraining(@RequestParam Long trainingId) {
        // TODO: delete 작업 필요
        trainingService.deleteTraining(trainingId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/close")
    public ResponseEntity<Void> updateTrainingClosed(@RequestParam Long trainingId, @RequestParam boolean closed) {
        trainingService.updateClosed(trainingId, closed);
        return ResponseEntity.ok().build();
    }
}
