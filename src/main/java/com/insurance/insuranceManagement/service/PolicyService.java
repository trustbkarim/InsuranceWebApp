package com.insurance.insuranceManagement.service;

import com.insurance.insuranceManagement.domain.enums.PolicyStatus;
import com.insurance.insuranceManagement.domain.enums.PolicyType;
import com.insurance.insuranceManagement.dto.request.PolicyRequestRecord;
import com.insurance.insuranceManagement.dto.response.PagedResponse;
import com.insurance.insuranceManagement.dto.response.PolicyResponseRecord;
import org.springframework.data.domain.Pageable;

public interface PolicyService {

    PolicyResponseRecord create(PolicyRequestRecord request);

    PolicyResponseRecord findById(Long id);

    PolicyResponseRecord findByPolicyNumber(String policyNumber);

    PagedResponse<PolicyResponseRecord> findAll(Pageable pageable);

    PagedResponse<PolicyResponseRecord> findByCustomer(Long customerId, Pageable pageable);

    PagedResponse<PolicyResponseRecord> findByStatus(PolicyStatus status, Pageable pageable);

    PagedResponse<PolicyResponseRecord> findByType(PolicyType type, Pageable pageable);

    PolicyResponseRecord activate(Long id);

    PolicyResponseRecord cancel(Long id);

    void delete(Long id);
}
