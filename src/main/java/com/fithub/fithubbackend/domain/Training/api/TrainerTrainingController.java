package com.fithub.fithubbackend.domain.Training.api;

import com.fithub.fithubbackend.domain.Training.application.TrainerTrainingService;
import com.fithub.fithubbackend.domain.Training.dto.reservation.TrainersReserveInfoDto;
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
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "trainer's training (트레이너의 트레이닝)", description = "트레이너가 사용하는 트레이닝 관련 api (트레이너 권한 필요)")
@RestController
@RequestMapping("/trainer/training")
@RequiredArgsConstructor
public class TrainerTrainingController {

    private final TrainerTrainingService trainerTrainingService;

    @Operation(summary = "트레이닝 생성, swagger에서 테스트 불가능, 이미지는 모두 images로 주면 됨", responses = {
            @ApiResponse(responseCode = "200", description = "생성됨"),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "예약 가능 날짜에 현재보다 이전 날짜가 들어있음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @PostMapping
    public ResponseEntity<Long> createTraining(@Valid TrainingCreateDto dto, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(trainerTrainingService.createTraining(dto, user));
    }

//    @Operation(summary = "트레이닝 수정", responses = {
//            @ApiResponse(responseCode = "200", description = "수정됨"),
//            @ApiResponse(responseCode = "400", description = "수정 날짜에 현재보다 이전 날짜가 들어있음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
//            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
//            @ApiResponse(responseCode = "409", description = "마감 처리 된 트레이닝을 수정하려고 함", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
//    })
//    @PutMapping
//    public ResponseEntity<Long> updateTraining(@RequestBody @Valid TrainingCreateDto dto, @RequestParam Long trainingId, @AuthUser User user) {
//        // TODO: 예약 관련 기능 끝난 후 수정 필요
//        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
//        return ResponseEntity.ok(trainingService.updateTraining(dto, trainingId, user.getEmail()));
//    }

    @Operation(summary = "트레이닝 삭제 (soft delete)", parameters = {
            @Parameter(name = "trainingId", description = "삭제할 트레이닝 id")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "400", description = "해당 트레이닝에 취소/완료되지 않은 예약이 남아 삭제 불가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "해당 트레이닝을 작성한 트레이너가 아니라 삭제 권한이 없음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "id에 해당하는 트레이닝이 없음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @DeleteMapping
    public ResponseEntity<String> deleteTraining(@RequestParam Long trainingId, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        trainerTrainingService.deleteTraining(trainingId, user.getEmail());
        return ResponseEntity.ok().body("완료");
    }

    @Operation(summary = "트레이닝 수동 마감 설정", parameters = {
            @Parameter(name = "trainingId", description = "마감할 트레이닝 id")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "해당 트레이닝을 작성한 트레이너가 아니라 수정 권한이 없음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "id에 해당하는 트레이닝이 없음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @PutMapping("/close")
    public ResponseEntity<String> closeTraining(@RequestParam Long trainingId, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        trainerTrainingService.closeTraining(trainingId, user);
        return ResponseEntity.ok().body("완료");
    }

    @Operation(summary = "트레이닝 수동 마감 해제", parameters = {
            @Parameter(name = "trainingId", description = "오픈할 트레이닝 id")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "해당 트레이닝을 작성한 트레이너가 아니라 수정 권한이 없음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "id에 해당하는 트레이닝이 없음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "트레이닝 예약 가능한 마지막 날짜가 현재보다 이전이므로 수정 불가능. 트레이닝 수정을 통해 날짜 추가 필요", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @PutMapping("/open")
    public ResponseEntity<String> openTraining(@RequestParam Long trainingId, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        trainerTrainingService.openTraining(trainingId, user);
        return ResponseEntity.ok().body("완료");
    }

    @Operation(summary = "트레이너의 예약 리스트 조회", parameters = {
            @Parameter(name = "pageable", description = "조회할 목록의 page, size, sort(기본은 id (생성 순), 예약된 트레이닝 날짜 순은 reserveDateTime으로 주면 됨)")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 회원은 트레이너가 아님", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @GetMapping("/reservations")
    public ResponseEntity<Page<TrainersReserveInfoDto>> getReservationList(@AuthUser User user,
                                                                           @PageableDefault(sort="id", direction = Sort.Direction.DESC) Pageable pageable) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(trainerTrainingService.getReservationList(user, pageable));
    }

    @Operation(summary = "트레이너의 예약 노쇼 처리", parameters = {
            @Parameter(name = "reservationId", description = "상태를 변경하려는 예약의 id")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "노쇼 처리 성공"),
            @ApiResponse(responseCode = "400", description = "해당 트레이닝이 완료 상태가 아님 (노쇼는 완료 상태인 예약만 가능, 완료 상태는 정각마다 스케줄러에 의해 변경됨)", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "해당 트레이닝을 생성한 트레이너가 아님. 권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 예약", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @PutMapping("/reservation/status/noshow")
    public ResponseEntity<String> updateReservationStatusNoShow(@AuthUser User user, @RequestParam Long reservationId) {
        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        trainerTrainingService.updateReservationStatusNoShow(user.getEmail(), reservationId);
        return ResponseEntity.ok().body("완료");
    }
}
