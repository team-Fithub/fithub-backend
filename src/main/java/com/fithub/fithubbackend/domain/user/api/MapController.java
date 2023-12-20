package com.fithub.fithubbackend.domain.user.api;

import com.fithub.fithubbackend.domain.user.application.MapService;
import com.fithub.fithubbackend.domain.user.dto.MapDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class MapController {
    private final MapService mapService;

    @Operation(summary = "헬스장 조회", parameters = {
            @Parameter(name = "page", description = "한 페이지에 15개씩 결과 출력 (최대 3페이지), default = 1"),
            @Parameter(name = "x", description = "사용자 현재 위치의 x 좌표"),
            @Parameter(name = "y", description = "사용자 현재 위치의 y 좌표")
            }, responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "409", description = "JSONParsing 에러")
    })
    @GetMapping("/map")
    public ResponseEntity<MapDto> getFitness(@RequestParam(value = "page",defaultValue = "1") int page, @RequestParam double x, @RequestParam double y) {
        return ResponseEntity.ok(mapService.getLocationByFitness(page,x,y));
    }
}
