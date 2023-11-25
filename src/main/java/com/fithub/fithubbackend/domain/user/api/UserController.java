package com.fithub.fithubbackend.domain.user.api;

import com.fithub.fithubbackend.domain.user.application.UserService;
import com.fithub.fithubbackend.domain.user.dto.SignInDto;
import com.fithub.fithubbackend.domain.user.dto.SignOutDto;
import com.fithub.fithubbackend.global.auth.TokenInfoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "로그인", responses = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원"),
            @ApiResponse(responseCode = "403", description = "로그인 실패 - 비밀번호 불일치")
    })
    @PostMapping("/auth/sign-in")
    public ResponseEntity<TokenInfoDto> signIn(@RequestBody SignInDto signInDto, HttpServletResponse response) {
        return ResponseEntity.ok(userService.signIn(signInDto, response));
    }

    @Operation(summary = "로그아웃", responses = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "검증되지 않는 토큰이거나 만료된 토큰"),
            @ApiResponse(responseCode = "403", description = "로그아웃 실패")
    })
    @DeleteMapping("/auth/sign-out")
    public ResponseEntity signOut(@CookieValue(name = "refreshToken") String cookieRefreshToken,
                                  @CookieValue(name = "accessToken") String cookieAccessToken,
                                  @AuthenticationPrincipal UserDetails userDetails, HttpServletResponse response, HttpServletRequest request){
        SignOutDto signOutDto = SignOutDto.builder()
                .accessToken(cookieAccessToken)
                .refreshToken(cookieRefreshToken).build();
        userService.signOut(signOutDto, userDetails, response, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "로그인 테스트 용")
    @GetMapping("/auth/test")
    public String test(@AuthenticationPrincipal UserDetails user) {
        return "인증 후 진입 가능";
    }

    @Operation(summary = "토큰 재발급", responses = {
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
            @ApiResponse(responseCode = "400", description = "검증되지 않는 토큰"),
            @ApiResponse(responseCode = "403", description = "토큰 재발급 실패")
    })
    @PatchMapping("/auth/reissue")
    public  ResponseEntity<TokenInfoDto> reissue(@CookieValue(name = "refreshToken") String cookieRefreshToken,
                                                      @AuthenticationPrincipal UserDetails userDetails,
                                                      HttpServletRequest request,HttpServletResponse response) {
        return ResponseEntity.ok(userService.reissue(cookieRefreshToken, request, response));
    }


}
