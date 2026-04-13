package com.insurance.insuranceManagement.domain.entity;

import com.insurance.insuranceManagement.domain.enums.ClaimStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "insurance_claims")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClaimEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "claim_number", nullable = false, unique = true)
    private String claimNumber;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ClaimStatus status = ClaimStatus.SUBMITTED;

    @Column(name = "incident_date")
    private LocalDateTime incidentDate;;

    @Column(name = "description")
    private String description;

    @Column(name = "claimed_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal claimedAmount;

    @Column(name = "approved_amount", precision = 15, scale = 2)
    private BigDecimal approvedAmount;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private PolicyEntity policy;

    public void approve(BigDecimal approvedAmount) {

        this.status = ClaimStatus.APPROVED;
        this.approvedAmount = approvedAmount;
        this.rejectionReason = null;
    }

    public void reject(String reason) {

        this.status = ClaimStatus.REJECTED;
        this.rejectionReason = reason;
        this.approvedAmount = BigDecimal.ZERO;
    }
}
