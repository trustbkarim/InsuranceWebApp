package com.insurance.insuranceManagement.domain.repository;

import com.insurance.insuranceManagement.domain.entity.PolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyRepository extends JpaRepository<PolicyEntity, Long> {
}
