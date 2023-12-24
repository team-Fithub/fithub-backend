package com.fithub.fithubbackend.domain.admin.api;

import com.fithub.fithubbackend.domain.admin.application.AdminService;
import com.fithub.fithubbackend.domain.admin.dto.CertRejectDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "트레이너 인증 요청 승인", responses = {
            @ApiResponse(responseCode = "200", description = "트레이너 인증 요청 승인 및 트레이너 생성 완료"),
    })
    @PutMapping("/trainer/certificate/accept")
    private ResponseEntity<String> acceptTrainerCertificateRequest(@RequestParam Long requestId) {
        adminService.acceptTrainerCertificateRequest(requestId);
        return ResponseEntity.ok().body("완료");
    }

    @Operation(summary = "트레이너 인증 요청 반려", responses = {
            @ApiResponse(responseCode = "200", description = "트레이너 인증 요청 반려"),
    })
    @PutMapping("/trainer/certificate/reject")
    private ResponseEntity<String> rejectTrainerCertificateRequest(@RequestParam Long requestId, @RequestBody CertRejectDto dto) {
        adminService.rejectTrainerCertificateRequest(requestId, dto);
        return ResponseEntity.ok().body("완료");
    }
}
