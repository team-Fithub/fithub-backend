package com.fithub.fithubbackend.domain.user.api;

import com.fithub.fithubbackend.domain.user.application.UserService;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.dto.ProfileDto;
import com.fithub.fithubbackend.global.domain.AuthUser;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
