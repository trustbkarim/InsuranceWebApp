package com.insurance.insuranceManagement.service;

import com.insurance.insuranceManagement.domain.enums.ClaimStatus;
import com.insurance.insuranceManagement.dto.request.ClaimRequestRecord;
import com.insurance.insuranceManagement.dto.response.ClaimResponseRecord;
import com.insurance.insuranceManagement.dto.response.PagedResponse;
import com.insurance.insuranceManagement.dto.update.UpdateClaimStatusRequest;
import org.springframework.data.domain.Pageable;

public interface ClaimService {

    ClaimResponseRecord createClaim(ClaimRequestRecord claimRequest);
    ClaimResponseRecord findById(Long id);
    ClaimResponseRecord findByClaimNumber(String claimNumber);
    PagedResponse<ClaimResponseRecord> findAll(Pageable pagedResponse);
    PagedResponse<ClaimResponseRecord> findByPolicy(Long id, Pageable pageable);
    PagedResponse<ClaimResponseRecord> findByStatus(ClaimStatus claimStatus, Pageable pageable);
    ClaimResponseRecord updateClaimStatus(Long id, UpdateClaimStatusRequest updateClaimStatus);
    void deleteClaim(Long id);
}
