package com.fithub.fithubbackend.domain.user.api;

import com.fithub.fithubbackend.domain.user.application.UserService;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.dto.CloseAccountReasonDto;
import com.fithub.fithubbackend.domain.user.dto.InterestUpdateDto;
import com.fithub.fithubbackend.domain.user.dto.ProfileDto;
import com.fithub.fithubbackend.domain.user.dto.ProfileUpdateDto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "프로필 조회", responses = {
            @ApiResponse(responseCode = "200", description = "프로필 조회")})
    @GetMapping("/profile")
    public ResponseEntity<ProfileDto> myProfile(@AuthUser User user){
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(userService.myProfile(user));
    }

    @Operation(summary = "프로필 수정", responses = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
    }, parameters = {
            @Parameter(name="profileUpdateDto", description = "프로필내역")
    })
    @PutMapping("/profile/update")
    public ResponseEntity<String> updateProfile(@RequestBody ProfileUpdateDto profileUpdateDto, @AuthUser User user){
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        userService.updateProfile(profileUpdateDto, user);
        return ResponseEntity.ok().body("완료");
    }


    @Operation(summary = "이미지 수정", responses = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
    }, parameters = {
            @Parameter(name="image", description = "프로필 이미지 변경 시")
    })
    @PutMapping("/image/update")
    public ResponseEntity<String> updateImage(@RequestPart(value = "image") MultipartFile multipartFile, @AuthUser User user){
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        userService.updateImage(multipartFile, user);
        return ResponseEntity.ok().body("완료");
    }

    @Operation(summary = "관심사 수정", responses = {
            @ApiResponse(responseCode = "200", description = "관심사 수정 성공"),
    })
    @PutMapping("/interest")
    public ResponseEntity<String> updateInterest(@Valid @RequestBody InterestUpdateDto interestUpdateDto, @AuthUser User user){
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        userService.updateInterest(interestUpdateDto, user);
        return ResponseEntity.ok().body("관심사 수정 완료");
    }

    @Operation(summary = "회원 탈퇴", responses = {
            @ApiResponse(responseCode = "200", description = "탈퇴 성공"),
            @ApiResponse(responseCode = "400", description = "예약 또는 진행 중인 트레이닝이 존재해 탈퇴 작업 불가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    }, parameters = {
            @Parameter(name="CloseAccountReasonDto", description = "탈퇴 사유 dto. reason 이 other(기타)가 아니라면 customReason 를 안 보내거나 null 로 보내주기.")
    })
    @PutMapping
    public ResponseEntity<String> closeAccount(@AuthUser User user, @RequestBody CloseAccountReasonDto reason) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        userService.closeAccount(user, reason);
        return ResponseEntity.ok().body("회원 탈퇴 완료");
    }



}
