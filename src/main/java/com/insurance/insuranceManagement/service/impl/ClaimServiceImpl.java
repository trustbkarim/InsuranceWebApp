package com.insurance.insuranceManagement.service.impl;

import com.insurance.insuranceManagement.domain.entity.ClaimEntity;
import com.insurance.insuranceManagement.domain.entity.PolicyEntity;
import com.insurance.insuranceManagement.domain.enums.ClaimStatus;
import com.insurance.insuranceManagement.domain.enums.PolicyStatus;
import com.insurance.insuranceManagement.domain.repository.ClaimRepository;
import com.insurance.insuranceManagement.domain.repository.PolicyRepository;
import com.insurance.insuranceManagement.dto.request.ClaimRequestRecord;
import com.insurance.insuranceManagement.dto.response.ClaimResponseRecord;
import com.insurance.insuranceManagement.dto.response.PagedResponse;
import com.insurance.insuranceManagement.dto.update.UpdateClaimStatusRequest;
import com.insurance.insuranceManagement.exception.BusinessException;
import com.insurance.insuranceManagement.exception.ResourceNotFoundException;
import com.insurance.insuranceManagement.mapper.ClaimMapper;
import com.insurance.insuranceManagement.service.ClaimService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ClaimServiceImpl implements ClaimService {

    private static final AtomicLong sequence = new AtomicLong(1000);

    private final ClaimRepository claimRepository;
    private final PolicyRepository policyRepository;

    private ClaimMapper claimMapper;

    @Transactional
    @Override
    public ClaimResponseRecord createClaim(ClaimRequestRecord claimRequest) {

        // policy should exists
        PolicyEntity policy = policyRepository.findById(claimRequest.policyId()).orElseThrow(() -> new ResourceNotFoundException("Policy", "id", claimRequest.policyId()));

        // policy should be actif
        if(!PolicyStatus.ACTIVE.equals(policy.getStatus())) {
            throw new BusinessException("POLICY_NOT_ACTIVE", "A claim can only be made on an active policy (current status : " + policy.getStatus() + ")");
        }

        // the incident date must be within the coverage period
        LocalDate incident = claimRequest.incidentDate();

        if(incident.isBefore(policy.getStartDate()) || incident.isAfter(policy.getEndDate())) {

            throw new BusinessException("INCIDENT_OUT_OF_COVERAGE", String.format("Incident date %s is out of coverage period [%s - %s]"));
        }

        // The claimed amount must not exceed the remaining coverage.
        BigDecimal alreadyApproved = claimRepository.sumApprovedAmountByPolicyId(policy.getId());
        BigDecimal remaining       = policy.getCoverageAmount().subtract(alreadyApproved);

        if(claimRequest.claimedAmount().compareTo(remaining) > 0) {
            throw new BusinessException("EXCEEDS_COVERAGE", String.format("The claimed amount (%.2f) exceeds the remaining coverage (%.2f).", claimRequest.claimedAmount(), remaining));
        }

        ClaimEntity claimEntity = claimMapper.toEntity(claimRequest);
        claimEntity.setPolicy(policy);
        claimEntity.setClaimNumber(generateClaimNumber());
        claimEntity.setStatus(ClaimStatus.SUBMITTED);

        ClaimEntity claimSaved = claimRepository.save(claimEntity);

        log.info("Claim {} declared (id={})", claimSaved.getClaimNumber(), claimSaved.getId());

        return claimMapper.toResponse(claimSaved);
    }

    @Override
    public ClaimResponseRecord findById(Long id) {
        return claimRepository.findById(id).map(claimMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Claim", "id", id));
    }

    @Override
    public ClaimResponseRecord findByClaimNumber(String claimNumber) {
        return claimRepository.findByClaimNumber(claimNumber).map(claimMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Claim", "number", claimNumber));
    }

    @Override
    public PagedResponse<ClaimResponseRecord> findAll(Pageable pageable) {
        return PagedResponse.from(claimRepository.findAll(pageable).map(claimMapper::toResponse));
    }

    @Override
    public PagedResponse<ClaimResponseRecord> findByPolicy(Long policyId, Pageable pageable) {

        if(!policyRepository.existsById(policyId)) {
            throw new ResourceNotFoundException("Police", "id", policyId);
        }

        return PagedResponse.from(claimRepository.findByPolicyCustomerId(policyId, pageable).map(claimMapper::toResponse));
    }

    @Override
    public PagedResponse<ClaimResponseRecord> findByStatus(ClaimStatus claimStatus, Pageable pageable) {

        return PagedResponse.from(claimRepository.findByStatus(claimStatus, pageable).map(claimMapper::toResponse));
    }

    @Override
    public ClaimResponseRecord updateClaimStatus(Long id, UpdateClaimStatusRequest updateClaimStatus) {

        log.info("Update claim status id = {} -> {}", id, updateClaimStatus.status());

        ClaimEntity claim = claimRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Claim", "id", id));

        // Update status
        switch (updateClaimStatus.status()) {

            case APPROVED -> {

                if (updateClaimStatus.approvedAmount() == null) {
                    throw new BusinessException("MISSING_APPROVED_AMOUNT", "Approved amount is required for approval !");
                }
                if(updateClaimStatus.approvedAmount().compareTo(claim.getClaimedAmount()) > 0) {
                  throw new BusinessException("APPROVED_EXCEEDS_CLAIMED", "Approved amount must not be greater than claimed amount");
                }
                claim.approve(updateClaimStatus.approvedAmount());
            }
            case REJECTED -> {
                if(updateClaimStatus.rejectionReason() == null || updateClaimStatus.rejectionReason().isBlank()) {

                    throw new BusinessException("MISSING REJECTION REASON", "Rejection reason is required");
                }
                claim.reject(updateClaimStatus.rejectionReason());
            }

            case UNDER_REVIEW -> updateClaimStatus.withStatus(ClaimStatus.UNDER_REVIEW);

            case CLOSED -> updateClaimStatus.withStatus(ClaimStatus.CLOSED);

            default -> throw new BusinessException("INVALID STATUS", "Not supported status : " + updateClaimStatus.status());

        }

        ClaimEntity updatedClaim = claimRepository.save(claim);
        log.info("Claim {} updated -> {}", updatedClaim.getClaimNumber(), updatedClaim.getStatus());

        return claimMapper.toResponse(updatedClaim);
    }

    @Override
    public void deleteClaim(Long id) {

        ClaimEntity claim = claimRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Claim", "id", id));

        if(!ClaimStatus.SUBMITTED.equals(claim.getStatus())) {
            throw new BusinessException("CANNOT_DELETE_CLAIM", "Only claim with status SUBMITTED can be deleted !");
        }

        claimRepository.deleteById(id);
        log.info("Claim deleted successfully !");
    }

    /**
     * Generate claim number unique.
     * Format : CLM-{YEAR}-{SEQUENCE}
     * Example : CLM-2024-1001
     */
    private String generateClaimNumber() {
        String year = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
        long   seq  = sequence.getAndIncrement();

        String number;
        do {
            number = String.format("CLM-%s-%d", year, seq);
        } while (claimRepository.existsByClaimNumber(number));

        return number;
    }
}
