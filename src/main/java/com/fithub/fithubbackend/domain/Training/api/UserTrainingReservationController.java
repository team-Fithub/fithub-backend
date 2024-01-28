package com.fithub.fithubbackend.domain.Training.api;

import com.fithub.fithubbackend.domain.Training.application.UserTrainingReservationService;
import com.fithub.fithubbackend.domain.Training.dto.*;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "user's training reservation & review (회원의 트레이닝 예약,리뷰)", description = "회원이 사용하는 트레이닝 예약, 리뷰 관련 api")
@RestController
@RequestMapping("/users/training/reservation")
@RequiredArgsConstructor
public class UserTrainingReservationController {
    private final UserTrainingReservationService userTrainingReservationService;

    @Operation(summary = "회원의 트레이닝 예약 리스트", parameters = {
            @Parameter(name = "pageable", description = "조회할 목록의 page, size, sort(기본은 id desc(생성 순), 예약된 트레이닝 날짜 순은 reserveDateTime으로 주면 됨)")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/all")
    public ResponseEntity<Page<UsersReserveInfoDto>> getUsersTrainingReservationList(@AuthUser User user,
                                                                                     @PageableDefault(sort="id", direction = Sort.Direction.DESC) Pageable pageable) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(userTrainingReservationService.getTrainingReservationList(user, pageable));
    }

    @Operation(summary = "회원이 남긴 트레이닝 예약 리뷰 전부 조회", description = "예약했던 모든 트레이닝 후기 조회",
            responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @GetMapping("/review/all")
    public ResponseEntity<List<UsersTrainingReviewDto>> getAllReviews(@AuthUser User user) {
        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(userTrainingReservationService.getAllReviews(user));
    }

    @Operation(summary = "예약 하나의 리뷰 조회", description = "예약하고 완료된 트레이닝에 대한 후기 조회", parameters = {
            @Parameter(name = "reserveId", description = "후기를 조회하려는 예약의 id")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 예약에 작성된 후기가 없음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @GetMapping("/review")
    public ResponseEntity<UsersTrainingReviewDto> getReviewForReservation(@AuthUser User user, @RequestParam Long reserveId) {
        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(userTrainingReservationService.getReviewForReservation(user, reserveId));
    }

    @Operation(summary = "회원의 예약한 트레이닝 리뷰 작성", description = "예약하고 완료된 트레이닝에 대한 후기 작성", parameters = {
            @Parameter(name = "dto", description = "예약 id, 리뷰 내용, 별점")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "작성 성공, 작성된 리뷰의 id 반환"),
            @ApiResponse(responseCode = "400", description = "예약한 트레이닝이 완료 상태가 아니라 작성 불가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "예약한 회원이 아니라 작성 권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/review")
    public ResponseEntity<Long> writeReviewOnCompletedReservation(@AuthUser User user, @RequestBody TrainingReviewReqDto dto) {
        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(userTrainingReservationService.writeReviewOnCompletedReservation(user, dto));
    }

    @Operation(summary = "회원의 트레이닝 리뷰 수정", description = "트레이닝 리뷰 수정", parameters = {
            @Parameter(name = "dto", description = "예약 id, 리뷰 내용, 별점"),
            @Parameter(name = "reviewId", description = "작성했던 리뷰의 id")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "수정 성공, 리뷰 id 반환 (파라미터로 넘긴 값과 동일)"),
            @ApiResponse(responseCode = "400", description = "예약한 트레이닝이 완료 상태가 아니라 작성 불가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "리뷰를 작성한 회원이 아니라 수정 권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PutMapping("/review")
    public ResponseEntity<String> updateReview(@AuthUser User user, @RequestParam Long reviewId, @RequestBody TrainingReviewReqDto dto) {
        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        userTrainingReservationService.updateReview(user, reviewId, dto);
        return ResponseEntity.ok().body("완료");
    }

    @Operation(summary = "회원의 트레이닝 리뷰 삭제", responses = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "400", description = "예약한 트레이닝이 완료 상태가 아니라 작성 불가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "리뷰를 작성한 회원이 아니라 삭제 권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @DeleteMapping("/review")
    public ResponseEntity<String> deleteReview(@AuthUser User user, @RequestParam Long reviewId) {
        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        userTrainingReservationService.deleteReview(user, reviewId);
        return ResponseEntity.ok().body("완료");
    }

}
