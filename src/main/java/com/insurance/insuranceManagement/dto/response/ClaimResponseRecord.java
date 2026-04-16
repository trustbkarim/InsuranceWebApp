package com.insurance.insuranceManagement.dto.response;

import com.insurance.insuranceManagement.domain.enums.ClaimStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ClaimResponseRecord(Long id, String claimNumber, ClaimStatus status, LocalDateTime incidentDate, String description,
                                  BigDecimal claimedAmount, BigDecimal approvedAmount, String rejectionReason) {
}
