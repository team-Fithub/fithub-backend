package com.fithub.fithubbackend.domain.Training.api;

import com.fithub.fithubbackend.domain.Training.application.TrainingService;
import com.fithub.fithubbackend.domain.Training.dto.TrainersReserveInfoDto;
import com.fithub.fithubbackend.domain.Training.dto.TrainingCreateDto;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.domain.AuthUser;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import com.fithub.fithubbackend.global.exception.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trainer/training")
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingService trainingService;

    @Operation(summary = "트레이닝 생성, swagger에서 테스트 불가능, 이미지는 모두 images로 주면 됨", responses = {
            @ApiResponse(responseCode = "200", description = "생성됨"),
            @ApiResponse(responseCode = "409", description = "예약 가능 날짜에 현재보다 이전 날짜가 들어있음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @PostMapping
    public ResponseEntity<Long> createTraining(@Valid TrainingCreateDto dto, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(trainingService.createTraining(dto, user));
    }

//    @Operation(summary = "트레이닝 수정", responses = {
//            @ApiResponse(responseCode = "200", description = "수정됨"),
//            @ApiResponse(responseCode = "409", description = "마감 처리 된 트레이닝을 수정하려고 함", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
//            @ApiResponse(responseCode = "409", description = "수정 날짜에 현재보다 이전 날짜가 들어있음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
//    })
//    @PutMapping
//    public ResponseEntity<Long> updateTraining(@RequestBody @Valid TrainingCreateDto dto, @RequestParam Long trainingId, @AuthenticationPrincipal UserDetails userDetails) {
//        // TODO: 예약 관련 기능 끝난 후 수정 필요
//        return ResponseEntity.ok(trainingService.updateTraining(dto, trainingId, userDetails.getUsername()));
//    }

//    @DeleteMapping
//    public ResponseEntity<Void> deleteTraining(@RequestParam Long trainingId) {
//        // TODO: delete 작업 필요
//        trainingService.deleteTraining(trainingId);
//        return ResponseEntity.ok().build();
//    }

    @PutMapping("/close")
    public ResponseEntity<Void> updateTrainingClosed(@RequestParam Long trainingId, @RequestParam boolean closed, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        trainingService.updateClosed(trainingId, closed, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "트레이너의 예약 리스트 조회", parameters = {
            @Parameter(name = "pageable", description = "조회할 목록의 page, size, sort(기본은 id (생성 순), 예약된 트레이닝 날짜 순은 reserveDateTime으로 주면 됨)")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "회원이 트레이너가 아님", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @GetMapping("/reservations")
    public ResponseEntity<Page<TrainersReserveInfoDto>> getReservationList(@AuthUser User user,
                                                                           @PageableDefault(sort="id", direction = Sort.Direction.DESC) Pageable pageable) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(trainingService.getReservationList(user, pageable));
    }
}
