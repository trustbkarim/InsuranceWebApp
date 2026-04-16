package com.insurance.insuranceManagement.domain.entity;

import com.insurance.insuranceManagement.domain.enums.PolicyStatus;
import com.insurance.insuranceManagement.domain.enums.PolicyType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "insurance_policies")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PolicyEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "policy_number", nullable = false, unique = true, length = 100)
    private String policyNumber;

    @Column(name = "policy_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PolicyType policyType;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PolicyStatus status = PolicyStatus.PENDING;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "premium_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal premiumAmount;

    @Column(name = "coverage_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal coverageAmount;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ClaimEntity> claims = new ArrayList<>();

    public boolean isActive() {

        return PolicyStatus.ACTIVE.equals(this.status) && !LocalDate.now().isAfter(this.endDate) && !LocalDate.now().isBefore(this.startDate);
    }

    public boolean isExpired() {

        return LocalDate.now().isAfter(this.endDate);
    }
}
