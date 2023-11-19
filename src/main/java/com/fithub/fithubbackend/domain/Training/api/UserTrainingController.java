package com.fithub.fithubbackend.domain.Training.api;

import com.fithub.fithubbackend.domain.Training.application.UserTrainingService;
import com.fithub.fithubbackend.domain.Training.dto.TrainingInfoDto;
import com.fithub.fithubbackend.domain.Training.dto.TrainingOutlineDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/training")
@RequiredArgsConstructor
public class UserTrainingController {
    private final UserTrainingService userTrainingService;

    @Operation(summary = "트레이닝 전체 조회, page 사용 (size = 9, sort=\"id\" desc 적용되어있음. swagger에서 보낼 때 따로 지정하는 거 없으면 parameter에 pageable은 다 지우고 보내도 됨)")
    @GetMapping("/all")
    // TODO: Security permitAll, filter X 추가
    public ResponseEntity<Page<TrainingOutlineDto>> searchAll(@PageableDefault(size = 9, sort = "id",  direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(userTrainingService.searchAll(pageable));
    }

    @Operation(summary = "트레이닝 하나 상세 조회", parameters = {
            @Parameter(name = "trainingId", description = "조회할 트레이닝의 primary key(id)")
    })
    @GetMapping
    public ResponseEntity<TrainingInfoDto> searchById(@RequestParam Long trainingId) {
        return ResponseEntity.ok(userTrainingService.searchById(trainingId));
    }
}
