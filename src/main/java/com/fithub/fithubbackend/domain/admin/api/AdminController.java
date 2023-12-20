package com.fithub.fithubbackend.domain.admin.api;

import com.fithub.fithubbackend.domain.admin.application.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @PutMapping("/trainer/certificate/accept")
    private ResponseEntity<String> acceptTrainerCertificateRequest(@RequestParam Long requestId) {
        adminService.acceptTrainerCertificateRequest(requestId);
        return ResponseEntity.ok().body("완료");
    }
}
