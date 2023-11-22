package com.fithub.fithubbackend.domain.user.api;

import com.fithub.fithubbackend.domain.user.application.UserService;
import com.fithub.fithubbackend.domain.user.dto.UserDto;
import com.fithub.fithubbackend.global.auth.TokenInfoDto;
import com.fithub.fithubbackend.global.exception.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "로그인", responses = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원"),
            @ApiResponse(responseCode = "403", description = "로그인 실패")
    })
    @PostMapping("/auth/sign-in")
    public ResponseEntity<TokenInfoDto> signIn(@RequestBody UserDto.SignInDto signInDto) {
        return ResponseEntity.ok(userService.signIn(signInDto));
    }

    @Operation(summary = "로그아웃 ( accessToken과 email(아이디)는 필수 값 / refreshToken은 \"\"(null)으로 설정 )", responses = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "검증되지 않는 토큰이거나 만료된 토큰"),
            @ApiResponse(responseCode = "403", description = "로그아웃 실패")
    })
    @PostMapping("/auth/sign-out")
    public ResponseEntity signOut(@RequestBody UserDto.SignOutDto signOutDto){
        userService.signOut(signOutDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
