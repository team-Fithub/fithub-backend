package com.fithub.fithubbackend.domain.admin.application;

import com.fithub.fithubbackend.domain.admin.dto.CertRejectDto;
import com.fithub.fithubbackend.domain.admin.dto.CertRejectedRequestDto;
import com.fithub.fithubbackend.domain.admin.dto.CertRequestDetailDto;
import com.fithub.fithubbackend.domain.admin.dto.CertRequestOutlineDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    Page<CertRequestOutlineDto> getAllAuthenticationRequest(Pageable pageable);
    Page<CertRejectedRequestDto> getAllAuthenticationRejectedRequest(Pageable pageable);
    CertRequestDetailDto getAuthenticationRequestById(Long requestId);

    void acceptTrainerCertificateRequest(Long requestId);
    void rejectTrainerCertificateRequest(Long requestId, CertRejectDto dto);
}
