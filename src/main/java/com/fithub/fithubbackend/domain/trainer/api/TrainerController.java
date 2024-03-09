package com.fithub.fithubbackend.domain.trainer.api;

import com.fithub.fithubbackend.domain.trainer.application.TrainerService;
import com.fithub.fithubbackend.domain.trainer.dto.*;
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
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "trainers (트레이너 경력, 자격증 작업)", description = "트레이너의 트레이닝 외의 작업")
@RestController
@RequiredArgsConstructor
@RequestMapping("/trainers")
public class TrainerController {

    private final TrainerService trainerService;

    @Operation(summary = "트레이너의 경력, 자격증 이미지 조회", responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/spec")
    public ResponseEntity<TrainerSpecDto> getTrainersSpec(@AuthUser User user) {
        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(trainerService.getTrainersSpec(user.getId()));
    }

    @Operation(summary = "트레이너 경력 하나 조회", responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    }, parameters = {
            @Parameter(name="careerId", description = "/spec 조회로 얻은 careerList에 있는 careerId")
    })
    @GetMapping("/careers")
    public ResponseEntity<TrainerCareerDetailDto> getTrainerCareer(@AuthUser User user, @RequestParam Long careerId) {
        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(trainerService.getTrainerCareer(careerId));
    }

    @Operation(summary = "트레이너 자격증 하나 조회", responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/licenses")
    public ResponseEntity<TrainerLicenseDto> getTrainerLicenseImg(@AuthUser User user, @RequestParam Long licenseId) {
        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(trainerService.getTrainerLicenseImg(licenseId));
    }

    @Operation(summary = "트레이너 경력 추가", responses = {
            @ApiResponse(responseCode = "200", description = "추가 완료"),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/careers")
    public ResponseEntity<Long> createTrainerCareer(@AuthUser User user, @RequestBody TrainerCareerRequestDto dto) {
        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(trainerService.createTrainerCareer(user.getId(), dto));
    }

    @Operation(summary = "트레이너 자격증 추가", responses = {
            @ApiResponse(responseCode = "200", description = "추가 완료"),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "s3에 파일 업로드 실패", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/licenses")
    public ResponseEntity<Long> createTrainerLicenseImg(@AuthUser User user, @RequestPart MultipartFile file) {
        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(trainerService.createTrainerLicenseImg(user.getId(), file));
    }

    @Operation(summary = "트레이너 경력 하나 수정", responses = {
            @ApiResponse(responseCode = "200", description = "수정 완료"),
            @ApiResponse(responseCode = "400", description = "진행중인 트레이닝 모집이 있어 근무지가 필요합니다. 해당 주소로 진행중인 트레이닝 삭제 후 근무지 수정을 진행해야됩니다.", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "경력을 수정할 권한이 없음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "좌표 파싱 에러", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PutMapping("/careers")
    public ResponseEntity<String> updateTrainerCareer(@AuthUser User user, @RequestParam Long careerId, @RequestBody TrainerCareerRequestDto dto) {
        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        trainerService.updateTrainerCareer(user.getId(), careerId, dto);
        return ResponseEntity.ok().body("완료");
    }

    @Operation(summary = "트레이너 경력 하나 삭제", responses = {
            @ApiResponse(responseCode = "200", description = "삭제 완료"),
            @ApiResponse(responseCode = "400", description = "진행중인 트레이닝 모집이 있어 근무지가 필요합니다. 해당 주소로 진행중인 트레이닝 삭제 후 근무지 삭제를 진행해야됩니다.", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @DeleteMapping("/careers")
    public ResponseEntity<String> deleteTrainerCareer(@AuthUser User user, @RequestParam Long careerId) {
        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        trainerService.deleteTrainerCareer(user.getId(), careerId);
        return ResponseEntity.ok().body("완료");
    }

    @Operation(summary = "트레이너 자격증 하나 삭제", responses = {
            @ApiResponse(responseCode = "200", description = "삭제 완료"),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @DeleteMapping("/licenses")
    public ResponseEntity<String> deleteTrainerLicenseImg(@AuthUser User user, @RequestParam Long licenseId) {
        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        trainerService.deleteTrainerLicenseImg(user.getId(), licenseId);
        return ResponseEntity.ok().body("완료");
    }
}
