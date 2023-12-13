package com.fithub.fithubbackend.domain.trainer.api;

import com.fithub.fithubbackend.domain.trainer.application.TrainerAuthService;
import com.fithub.fithubbackend.domain.trainer.dto.TrainerCertificationRequestDto;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/trainer/certificate")
public class TrainerAuthController {

    private final TrainerAuthService trainerAuthService;

    @Operation(summary = "트레이너 인증 요청, swagger에서는 careerList를 application/json으로 못 보내서 사용 X, postman에서는 Body->form-data로 요청 가능",
            description = "postman 요청 시 licenseFileList[0], careerList[0].company 이런 식으로 보내야됨",
            responses = {
                    @ApiResponse(responseCode = "200", description = "트레이너 인증 요청 완료"),
                    @ApiResponse(responseCode = "409", description = "이미 트레이너인 회원의 요청 / 완료되지 않은 요청이 있음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
                    @ApiResponse(responseCode = "500", description = "s3에 파일 업로드 실패", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
            }, parameters = {
            @Parameter(name="requestDto", description = "트레이너 인증 요청 dto(자격증 이미지 파일 리스트, 자격증 이름, 경력사항 리스트)")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> certificationRequest(@Valid TrainerCertificationRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.badRequest().body("로그인한 사용자만 가능합니다.");
        }
        trainerAuthService.saveTrainerCertificateRequest(requestDto, userDetails.getUsername());
        return ResponseEntity.ok().body("완료");
    }
}
