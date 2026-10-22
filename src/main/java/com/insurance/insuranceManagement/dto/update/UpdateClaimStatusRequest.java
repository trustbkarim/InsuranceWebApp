package com.insurance.insuranceManagement.dto.update;

import com.insurance.insuranceManagement.domain.enums.ClaimStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateClaimStatusRequest(@NotNull(message = "Status is required") ClaimStatus status,
                                       @DecimalMin(value = "0.00", message = "Approved amount must be positif or zero")  @Digits(integer = 13, fraction = 2) BigDecimal approvedAmount,
                                       @Size(max = 500, message = "he rejection reason cannot exceed 500 characters.") String rejectionReason) {


    public void withStatus(ClaimStatus claimStatus) {
        new UpdateClaimStatusRequest(claimStatus, this.approvedAmount, this.rejectionReason);
    }
}
