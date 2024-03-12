package com.fithub.fithubbackend.domain.admin.api;

import com.fithub.fithubbackend.domain.admin.application.AdminService;
import com.fithub.fithubbackend.domain.admin.dto.CertRejectDto;
import com.fithub.fithubbackend.domain.admin.dto.CertRejectedRequestDto;
import com.fithub.fithubbackend.domain.admin.dto.CertRequestDetailDto;
import com.fithub.fithubbackend.domain.admin.dto.CertRequestOutlineDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/trainers/authentications/all")
    private ResponseEntity<Page<CertRequestOutlineDto>> getAllAuthenticationRequest(
            @PageableDefault(sort = "id") Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllAuthenticationRequest(pageable));
    }

    @GetMapping("/trainers/authentications/rejected/all")
    private ResponseEntity<Page<CertRejectedRequestDto>> getAllAuthenticationRejectedRequest(
            @PageableDefault(sort = "id") Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllAuthenticationRejectedRequest(pageable));
    }

    @GetMapping("/trainers/authentications")
    private ResponseEntity<CertRequestDetailDto> getAllAuthenticationRequest(@RequestParam Long requestId) {
        return ResponseEntity.ok(adminService.getAuthenticationRequestById(requestId));
    }

    @Operation(summary = "트레이너 인증 요청 승인", responses = {
            @ApiResponse(responseCode = "200", description = "트레이너 인증 요청 승인 및 트레이너 생성 완료"),
    })
    @PutMapping("/trainers/authentications/accept")
    private ResponseEntity<String> acceptTrainerCertificateRequest(@RequestParam Long requestId) {
        adminService.acceptTrainerCertificateRequest(requestId);
        return ResponseEntity.ok().body("완료");
    }

    @Operation(summary = "트레이너 인증 요청 반려", responses = {
            @ApiResponse(responseCode = "200", description = "트레이너 인증 요청 반려"),
    })
    @PutMapping("/trainers/authentications/reject")
    private ResponseEntity<String> rejectTrainerCertificateRequest(@RequestParam Long requestId, @RequestBody CertRejectDto dto) {
        adminService.rejectTrainerCertificateRequest(requestId, dto);
        return ResponseEntity.ok().body("완료");
    }
}
