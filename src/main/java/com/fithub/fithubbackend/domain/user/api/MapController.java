package com.fithub.fithubbackend.domain.user.api;

import com.fithub.fithubbackend.domain.user.application.MapService;
import com.fithub.fithubbackend.domain.user.dto.constants.MapDocumentDto;
import com.fithub.fithubbackend.domain.user.dto.constants.MapDto;
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
    @GetMapping("/map")
    public ResponseEntity<MapDto> getFitness(@RequestParam(value = "page",defaultValue = "1") int page, @RequestParam double x, @RequestParam double y) {
        return ResponseEntity.ok(mapService.getLocationByFitness(page,x,y));
    }
}
