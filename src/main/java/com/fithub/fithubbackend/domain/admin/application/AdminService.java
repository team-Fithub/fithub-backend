package com.fithub.fithubbackend.domain.admin.application;

import com.fithub.fithubbackend.domain.admin.dto.CertRejectDto;

public interface AdminService {
    void acceptTrainerCertificateRequest(Long requestId);
    void rejectTrainerCertificateRequest(Long requestId, CertRejectDto dto);
}
