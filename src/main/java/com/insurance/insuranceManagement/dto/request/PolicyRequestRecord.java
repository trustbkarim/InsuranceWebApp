package com.insurance.insuranceManagement.dto.request;

import com.insurance.insuranceManagement.domain.enums.PolicyType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PolicyRequestRecord(@NotNull(message = "The policy number is required") String policyNumber,
                                  @NotNull(message = "The policy type is required") PolicyType policyType,
                                  @NotNull(message = "The start date is required") @FutureOrPresent(message = "Start date must be today or in the future") LocalDate startDate,
                                  @NotNull(message = "The end date is required") @Future(message = "End date must be today or in the future") LocalDate endDate,
                                  @NotNull(message = "The premium amount is required") @DecimalMin(value = "0.01", message = "Premium amount must be greater than 0") @Digits(integer = 13, fraction = 2, message = "Invalid amount format") BigDecimal premiumAmount,
                                  @NotNull(message = "The coverage amount is required") @DecimalMin(value = "0.01", message = "Coverage amount must be greater than 0") @Digits(integer = 13, fraction = 2) BigDecimal coverageAmount,
                                  @Size(message = "The description cannot exceed 1000 characters") String description) {
}
