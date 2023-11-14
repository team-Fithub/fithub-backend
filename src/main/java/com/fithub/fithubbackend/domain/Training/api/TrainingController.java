package com.fithub.fithubbackend.domain.Training.api;

import com.fithub.fithubbackend.domain.Training.application.TrainingService;
import com.fithub.fithubbackend.domain.Training.dto.TrainingCreateDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/training")
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingService trainingService;

    @PostMapping
    public ResponseEntity<Long> createTraining(@RequestBody @Valid TrainingCreateDto dto, @RequestParam Long trainerId) {
        return ResponseEntity.ok(trainingService.createTraining(dto, trainerId));
    }

    @PutMapping
    public ResponseEntity<Long> updateTraining(@RequestBody @Valid TrainingCreateDto dto, @RequestParam Long trainerId) {
        // TODO: 예약 관련 기능 끝난 후 수정 필요
        return ResponseEntity.ok(trainingService.updateTraining(dto, trainerId));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteTraining(@RequestParam Long trainingId) {
        // TODO: delete 작업 필요
        trainingService.deleteTraining(trainingId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/close")
    public ResponseEntity<Void> updateTrainingClosed(@RequestParam Long trainingId, @RequestParam boolean closed) {
        trainingService.updateClosed(trainingId, closed);
        return ResponseEntity.ok().build();
    }
}
