package com.insurance.insuranceManagement.domain.repository;

import com.insurance.insuranceManagement.domain.entity.ClaimEntity;
import com.insurance.insuranceManagement.domain.enums.ClaimStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ClaimRepository extends JpaRepository<ClaimEntity, Long> {

    Optional<ClaimEntity> findByClaimNumber(String claimNumber);
    boolean existsByClaimNumber(String claimNumber);
    Page<ClaimEntity> findByPolicyCustomerId(Long policyCustomerId, Pageable pageable);
    Page<ClaimEntity> findByStatus(ClaimStatus claimStatus, Pageable pageable);

    @Query("SELECT COALESCE(SUM(c.approvedAmount), 0) FROM ClaimEntity c WHERE c.policy.id = :policyId AND c.status = 'APPROVED'")
    BigDecimal sumApprovedAmountByPolicyId(@Param("policyId") Long policyId);
}
