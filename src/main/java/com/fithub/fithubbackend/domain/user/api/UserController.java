package com.fithub.fithubbackend.domain.user.api;

import com.fithub.fithubbackend.domain.user.application.UserService;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.dto.ProfileDto;
import com.fithub.fithubbackend.domain.user.dto.SignUpDto;
import com.fithub.fithubbackend.domain.user.dto.SignUpResponseDto;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

//    @Operation(summary = "프로필 수정", responses = {
//            @ApiResponse(responseCode = "200", description = "수정 성공"),
//            @ApiResponse(responseCode = "409", description = "이메일 중복", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
//            @ApiResponse(responseCode = "409", description = "닉네임 중복", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
//    }, parameters = {
//            @Parameter(name="image", description = "프로필 이미지 변경 시"),
//            @Parameter(name="profileDto", description = "변경하고 싶은 프로필내역")
//    })
//    @PutMapping("/profile/update")
//    public ResponseEntity<User> updateProfile(@RequestPart(value = "image",required = false) MultipartFile multipartFile, @RequestPart(value = "profileDto", required = false) ProfileDto profileDto, @AuthUser User user){
//        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
//        return ResponseEntity.ok(userService.updateProfile(multipartFile, profileDto, user));
//    }

    @Operation(summary = "프로필 수정 swagger에서 사용 불가능. postman으로 테스트 가능 (multipart/form-data)", responses = {
            @ApiResponse(responseCode = "200", description = "회원 생성"),
            @ApiResponse(responseCode = "409", description = "이메일 중복", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "닉네임 중복", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "아이디 중복", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "형식 에러 (이메일 형식 , 비밀번호 형식(8자이상 특수문자 포함), 닉네임 형식(특수문자 제외 한글, 영어, 숫자), 전화번호 형식 (xxx-xxx(xxxx)-xxxx)", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    }, parameters = {
            @Parameter(name="signUpDto", description = "=application/json 타입 지정 필요"),
            @Parameter(name="profileImg", description = "프로필 이미지. 없을 경우 기본 이미지로 저장됨")
    })
    @PutMapping(value = "/profile/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> updateProfile(@RequestPart(value = "profileDto") @Valid ProfileDto profileDto,
                                                    @RequestPart(value ="profileImg", required = false) MultipartFile profileImg,
                                                    @AuthUser User user) throws IOException {
        return ResponseEntity.ok(userService.updateProfile(profileImg, profileDto, user));
    }

}
