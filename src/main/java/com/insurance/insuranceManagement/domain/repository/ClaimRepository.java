package com.insurance.insuranceManagement.domain.repository;

import com.insurance.insuranceManagement.domain.entity.ClaimEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimRepository extends JpaRepository<ClaimEntity, Long> {
}
