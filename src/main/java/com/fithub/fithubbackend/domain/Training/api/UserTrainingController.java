package com.fithub.fithubbackend.domain.Training.api;

import com.fithub.fithubbackend.domain.Training.application.UserTrainingService;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/training")
@RequiredArgsConstructor
public class UserTrainingController {
    private final UserTrainingService userTrainingService;

    @Operation(summary = "트레이닝 전체 조회, page 사용 (size = 9, sort=\"id\" desc 적용되어있음. swagger에서 보낼 때 따로 지정하는 거 없으면 parameter에 pageable은 다 지우고 보내도 됨)")
    @GetMapping("/all")
    public ResponseEntity<Page<TrainingOutlineDto>> searchAll(@PageableDefault(size = 9, sort = "id",  direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(userTrainingService.searchAll(pageable));
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
        return ResponseEntity.ok(userTrainingService.searchById(trainingId));
    }

    @Operation(summary = "트레이닝 상세 조회에서 트레이닝 리뷰 리스트 조회", parameters = {
            @Parameter(name = "trainingId", description = "조회할 트레이닝의 primary key(id)")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
    })
    @GetMapping("/reviews")
    public ResponseEntity<List<TrainingReviewDto>> getTrainingReviews (@RequestParam Long trainingId) {
        return ResponseEntity.ok(userTrainingService.getTrainingReviews(trainingId));
    }

    @Operation(summary = "트레이닝 찜 리스트 조회", responses = {
            @ApiResponse(responseCode = "200", description = "리스트 조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @GetMapping("/likes")
    public ResponseEntity<List<TrainingLikesInfoDto>> getUserTrainingLikesList(@AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(userTrainingService.getTrainingLikesList(user));
    }

    @Operation(summary = "트레이닝 찜 여부 조회", parameters = {
            @Parameter(name = "trainingId", description = "조회할 트레이닝의 primary key(id)")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "찜 여부를 조회할 트레이닝이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/check/likes")
    public ResponseEntity<Boolean> isLikesTraining(@RequestParam Long trainingId, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(userTrainingService.isLikesTraining(trainingId, user));
    }

    @Operation(summary = "트레이닝 찜 설정", parameters = {
            @Parameter(name = "trainingId", description = "조회할 트레이닝의 primary key(id)")
    }, responses = {
            @ApiResponse(responseCode = "400", description = "트레이너는 자신의 트레이닝 찜 불가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 트레이닝이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/likes")
    public ResponseEntity<String> likes(@RequestParam Long trainingId, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        userTrainingService.likesTraining(trainingId, user);
        return ResponseEntity.ok().body("완료");
    }

    @Operation(summary = "트레이닝 찜 취소", parameters = {
            @Parameter(name = "trainingId", description = "조회할 트레이닝의 primary key(id)")
    }, responses = {
            @ApiResponse(responseCode = "404", description = "해당 트레이닝이 존재하지 않음"),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "해당 트레이닝을 찜하지 않아 찜 취소 불가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @DeleteMapping("/likes")
    public ResponseEntity<String> cancelTrainingLikes(@RequestParam Long trainingId, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        userTrainingService.cancelTrainingLikes(trainingId, user);
        return ResponseEntity.ok().body("완료");
    }

    @Operation(summary = "회원의 트레이닝 예약 리스트", parameters = {
            @Parameter(name = "pageable", description = "조회할 목록의 page, size, sort(기본은 id desc(생성 순), 예약된 트레이닝 날짜 순은 reserveDateTime으로 주면 됨)")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/reservations")
    public ResponseEntity<Page<UsersReserveInfoDto>> getUsersTrainingReservationList(@AuthUser User user,
                                                                                     @PageableDefault(sort="id", direction = Sort.Direction.DESC) Pageable pageable) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(userTrainingService.getTrainingReservationList(user, pageable));
    }

    @Operation(summary = "회원이 남긴 트레이닝 예약 리뷰 전부 조회", description = "예약했던 모든 트레이닝 후기 조회",
            responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @GetMapping("/reservation/review/all")
    public ResponseEntity<List<UsersTrainingReviewDto>> getAllReviews(@AuthUser User user) {
        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(userTrainingService.getAllReviews(user));
    }

    @Operation(summary = "예약 하나의 리뷰 조회", description = "예약하고 완료된 트레이닝에 대한 후기 조회", parameters = {
            @Parameter(name = "reserveId", description = "후기를 조회하려는 예약의 id")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 예약에 작성된 후기가 없음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @GetMapping("/reservation/review")
    public ResponseEntity<UsersTrainingReviewDto> getReviewForReservation(@AuthUser User user, @RequestParam Long reserveId) {
        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(userTrainingService.getReviewForReservation(user, reserveId));
    }

    @Operation(summary = "회원의 예약한 트레이닝 리뷰 작성", description = "예약하고 완료된 트레이닝에 대한 후기 작성", parameters = {
            @Parameter(name = "dto", description = "예약 id, 리뷰 내용, 별점")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "작성 성공, 작성된 리뷰의 id 반환"),
            @ApiResponse(responseCode = "400", description = "예약한 트레이닝이 완료 상태가 아니라 작성 불가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "예약한 회원이 아니라 작성 권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/reservation/review")
    public ResponseEntity<Long> writeReviewOnCompletedReservation(@AuthUser User user, @RequestBody TrainingReviewReqDto dto) {
        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(userTrainingService.writeReviewOnCompletedReservation(user, dto));
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
    @PutMapping("/reservation/review")
    public ResponseEntity<String> updateReview(@AuthUser User user, @RequestParam Long reviewId, @RequestBody TrainingReviewReqDto dto) {
        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        userTrainingService.updateReview(user, reviewId, dto);
        return ResponseEntity.ok().body("완료");
    }

    @Operation(summary = "회원의 트레이닝 리뷰 삭제", responses = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "400", description = "예약한 트레이닝이 완료 상태가 아니라 작성 불가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "리뷰를 작성한 회원이 아니라 삭제 권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @DeleteMapping("/reservation/review")
    public ResponseEntity<String> deleteReview(@AuthUser User user, @RequestParam Long reviewId) {
        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        userTrainingService.deleteReview(user, reviewId);
        return ResponseEntity.ok().body("완료");
    }

}
