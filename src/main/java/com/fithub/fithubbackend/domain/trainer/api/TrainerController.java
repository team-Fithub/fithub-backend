package com.fithub.fithubbackend.domain.trainer.api;

import com.fithub.fithubbackend.domain.trainer.application.TrainerService;
import com.fithub.fithubbackend.domain.trainer.dto.TrainerCareerDto;
import com.fithub.fithubbackend.domain.trainer.dto.TrainerCareerRequestDto;
import com.fithub.fithubbackend.domain.trainer.dto.TrainerLicenseDto;
import com.fithub.fithubbackend.domain.trainer.dto.TrainerSpecDto;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.domain.AuthUser;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trainers")
public class TrainerController {

    private final TrainerService trainerService;

    @GetMapping("/spec")
    public ResponseEntity<TrainerSpecDto> getTrainersSpec(@AuthUser User user) {
        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(trainerService.getTrainersSpec(user.getId()));
    }

    @GetMapping("/careers")
    public ResponseEntity<TrainerCareerDto> getTrainerCareer(@AuthUser User user, @RequestParam Long careerId) {
        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(trainerService.getTrainerCareer(careerId));
    }

    @GetMapping("/licenses")
    public ResponseEntity<TrainerLicenseDto> getTrainerLicenseImg(@AuthUser User user, @RequestParam Long licenseId) {
        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(trainerService.getTrainerLicenseImg(licenseId));
    }

    @PostMapping("/careers")
    public ResponseEntity<Long> createTrainerCareer(@AuthUser User user, @RequestBody TrainerCareerRequestDto dto) {
        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(trainerService.createTrainerCareer(user.getId(), dto));
    }
}