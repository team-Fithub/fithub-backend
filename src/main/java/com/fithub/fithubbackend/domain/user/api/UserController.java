package com.fithub.fithubbackend.domain.user.api;

import com.fithub.fithubbackend.domain.Training.dto.TrainingCancelOrRefundDto;
import com.fithub.fithubbackend.domain.user.application.UserService;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.dto.ProfileDto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "프로필 조회", responses = {
            @ApiResponse(responseCode = "200", description = "프로필 조회")})
    @GetMapping("/profile")
    public ResponseEntity<ProfileDto> myProfile(@AuthUser User user){
        if(user == null) throw new CustomException(ErrorCode.UNKNOWN_ERROR, "로그인한 사용자만 가능합니다.");

        return ResponseEntity.ok(userService.myProfile(user));
    }

    @Operation(summary = "프로필 수정", responses = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "409", description = "이메일 중복", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "닉네임 중복", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    }, parameters = {
            @Parameter(name="image", description = "프로필 이미지 변경 시"),
            @Parameter(name="profileDto", description = "변경하고 싶은 프로필내역")
    })
    @PatchMapping("/profile/update")
    public ResponseEntity<User> updateProfile(@RequestPart(value = "image",required = false) MultipartFile multipartFile, @RequestPart(value = "profileDto", required = false) ProfileDto profileDto, @AuthUser User user){
        if(user == null) throw new CustomException(ErrorCode.UNKNOWN_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(userService.updateProfile(multipartFile, profileDto, user));
    }

    @Operation(summary = "취소/환불 내역 조회", responses = {
            @ApiResponse(responseCode = "200", description = "취소/환불 내역 조회"),
    })
    @GetMapping("/profile/cancelOrRefund")
    public ResponseEntity<List<TrainingCancelOrRefundDto>> cancelOrRefund(@AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.UNKNOWN_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(userService.trainingCancelOrRefundHistory(user));
    }

}
