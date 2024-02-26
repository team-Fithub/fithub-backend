package com.fithub.fithubbackend.domain.trainer.api;

import com.fithub.fithubbackend.domain.trainer.application.TrainerService;
import com.fithub.fithubbackend.domain.trainer.dto.TrainerSpecDto;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.domain.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trainers")
public class TrainerController {

    private final TrainerService trainerService;

    @GetMapping("/spec")
    public ResponseEntity<TrainerSpecDto> getTrainersSpec(@AuthUser User user) {
        return ResponseEntity.ok(trainerService.getTrainersSpec(user.getId()));
    }

//    @PostMapping("/careers")
//    public ResponseEntity<>
}
