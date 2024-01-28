package com.fithub.fithubbackend.domain.Training.api;

import com.fithub.fithubbackend.domain.Training.application.UserTrainingLikeService;
import com.fithub.fithubbackend.domain.Training.dto.TrainingLikesInfoDto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "user's training like (회원의 트레이닝 찜)", description = "회원이 사용하는 트레이닝 찜 관련 api")
@RestController
@RequestMapping("/users/training/like")
@RequiredArgsConstructor
public class UserTrainingLikesController {
    private final UserTrainingLikeService userTrainingLikeService;

    @Operation(summary = "트레이닝 찜 여부 조회", parameters = {
            @Parameter(name = "trainingId", description = "조회할 트레이닝의 primary key(id)")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "찜 여부를 조회할 트레이닝이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/check")
    public ResponseEntity<Boolean> isLikesTraining(@RequestParam Long trainingId, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(userTrainingLikeService.isLikesTraining(trainingId, user));
    }

    @Operation(summary = "트레이닝 찜 설정", parameters = {
            @Parameter(name = "trainingId", description = "조회할 트레이닝의 primary key(id)")
    }, responses = {
            @ApiResponse(responseCode = "400", description = "트레이너는 자신의 트레이닝 찜 불가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 트레이닝이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping
    public ResponseEntity<String> likes(@RequestParam Long trainingId, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        userTrainingLikeService.likesTraining(trainingId, user);
        return ResponseEntity.ok().body("완료");
    }

    @Operation(summary = "트레이닝 찜 취소", parameters = {
            @Parameter(name = "trainingId", description = "조회할 트레이닝의 primary key(id)")
    }, responses = {
            @ApiResponse(responseCode = "404", description = "해당 트레이닝이 존재하지 않음"),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "해당 트레이닝을 찜하지 않아 찜 취소 불가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @DeleteMapping
    public ResponseEntity<String> cancelTrainingLikes(@RequestParam Long trainingId, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        userTrainingLikeService.cancelTrainingLikes(trainingId, user);
        return ResponseEntity.ok().body("완료");
    }

    @Operation(summary = "회원의 트레이닝 찜 리스트 조회", responses = {
            @ApiResponse(responseCode = "200", description = "리스트 조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @GetMapping("/all")
    public ResponseEntity<List<TrainingLikesInfoDto>> getUserTrainingLikesList(@AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(userTrainingLikeService.getTrainingLikesList(user));
    }
}
