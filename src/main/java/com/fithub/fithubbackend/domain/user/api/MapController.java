package com.fithub.fithubbackend.domain.user.api;

import com.fithub.fithubbackend.domain.user.application.MapService;
import com.fithub.fithubbackend.domain.user.dto.constants.MapDocumentDto;
import com.fithub.fithubbackend.domain.user.dto.constants.MapDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class MapController {
    private final MapService mapService;

    @Operation(summary = "헬스장 조회", responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "409", description = "JSONParsing 에러")
    })
    @GetMapping("/map")
    public ResponseEntity<MapDto> getFitness(@RequestParam(value = "page",defaultValue = "1") int page, @RequestParam double x, @RequestParam double y) {
        return ResponseEntity.ok(mapService.getLocationByFitness(page,x,y));
    }
}
