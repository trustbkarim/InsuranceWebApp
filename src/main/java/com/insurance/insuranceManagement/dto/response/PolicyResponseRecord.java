package com.insurance.insuranceManagement.dto.response;

import com.insurance.insuranceManagement.domain.enums.PolicyStatus;
import com.insurance.insuranceManagement.domain.enums.PolicyType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PolicyResponseRecord(Long id, String policyNumber, PolicyType policyType, PolicyStatus status, LocalDate startDate,
                                   LocalDate endDate, BigDecimal premiumAmount, BigDecimal coverageAmount, String description) {
}
