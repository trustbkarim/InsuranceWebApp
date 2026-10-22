package com.insurance.insuranceManagement.domain.repository;

import com.insurance.insuranceManagement.domain.entity.PolicyEntity;
import com.insurance.insuranceManagement.domain.enums.PolicyStatus;
import com.insurance.insuranceManagement.domain.enums.PolicyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyRepository extends JpaRepository<PolicyEntity, Long> {

    Optional<PolicyEntity> findByPolicyNumber(String policyNumber);

    boolean existsByPolicyNumber(String policyNumber);

    Page<PolicyEntity> findByCustomerId(Long customerId, Pageable pageable);

    Page<PolicyEntity> findByStatus(PolicyStatus status, Pageable pageable);

    Page<PolicyEntity> findByPolicyType(PolicyType policyType, Pageable pageable);

    List<PolicyEntity> findByEndDateBeforeAndStatus(LocalDate date, PolicyStatus status);

    @Query("SELECT p FROM PolicyEntity p WHERE p.customer.id = :customerId AND p.status = :status")
    List<PolicyEntity> findActiveByCustomerId(
            @Param("customerId") Long customerId,
            @Param("status") PolicyStatus status);

    @Query("SELECT COUNT(p) FROM PolicyEntity p WHERE p.customer.id = :customerId AND p.status = 'ACTIVE'")
    long countActiveByCustomerId(@Param("customerId") Long customerId);
}
