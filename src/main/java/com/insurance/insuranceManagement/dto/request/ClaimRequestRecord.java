package com.insurance.insuranceManagement.dto.request;

import com.insurance.insuranceManagement.domain.enums.ClaimStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public record ClaimRequestRecord(@NotNull(message = "Claim number should not be null") String claimNumber, ClaimStatus status,
                                 @NotNull(message = "Incident date is required") @PastOrPresent(message = "Incident date cannot be in the future") LocalDateTime incidentDate,
                                 @NotBlank(message = "Description should not be blank") @Size(min = 20, max = 2000, message = "Description must contain between 20 and 2000 characters") String description,
                                 @NotNull(message = "Claimed amount is required") @DecimalMin(value = "0.01", message = "Claimed amount must be greater than 0") @Digits(integer = 13, fraction = 2, message = "Invalid format") BigDecimal claimedAmount)
{ }
