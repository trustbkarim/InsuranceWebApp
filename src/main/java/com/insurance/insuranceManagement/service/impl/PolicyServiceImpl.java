package com.insurance.insuranceManagement.service.impl;

import com.insurance.insuranceManagement.domain.entity.CustomerEntity;
import com.insurance.insuranceManagement.domain.entity.PolicyEntity;
import com.insurance.insuranceManagement.domain.enums.PolicyStatus;
import com.insurance.insuranceManagement.domain.enums.PolicyType;
import com.insurance.insuranceManagement.domain.repository.CustomerRepository;
import com.insurance.insuranceManagement.domain.repository.PolicyRepository;
import com.insurance.insuranceManagement.dto.request.PolicyRequestRecord;
import com.insurance.insuranceManagement.dto.response.PagedResponse;
import com.insurance.insuranceManagement.dto.response.PolicyResponseRecord;
import com.insurance.insuranceManagement.exception.BusinessException;
import com.insurance.insuranceManagement.exception.ResourceNotFoundException;
import com.insurance.insuranceManagement.mapper.PolicyMapper;
import com.insurance.insuranceManagement.service.PolicyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PolicyServiceImpl implements PolicyService {

    private static final int MAX_ACTIVE_POLICIES = 5;
    private static final AtomicLong sequence = new AtomicLong(1000);

    private final PolicyRepository policyRepository;
    private final CustomerRepository customerRepository;
    private PolicyMapper policyMapper;


    @Override
    public PolicyResponseRecord create(PolicyRequestRecord request) {

        log.info("Create policy for policy type {}", request.policyType());

        // if customer exists
        CustomerEntity customerEntity = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.customerId()));

        // business rule
        if(!request.endDate().isAfter(request.startDate())) {
            throw new BusinessException("INVALID_DATE", "The end date must be later than the start date.");
        }

        // Business rule : MAX_ACTIVE_POLICIES activated policies
        long activeCount = policyRepository.countActiveByCustomerId(request.customerId());

        if (activeCount >= MAX_ACTIVE_POLICIES) {
            throw new BusinessException("MAX_POLICIES_REACHED",
                    String.format("Client cannot have more than %d active policies", MAX_ACTIVE_POLICIES));
        }

        PolicyEntity policyEntity = policyMapper.toEntity(request);
        policyEntity.setCustomer(customerEntity);
        policyEntity.setPolicyNumber(generatePolicyNumber(request.policyType()));
        policyEntity.setStatus(PolicyStatus.PENDING);

        PolicyEntity created = policyRepository.save(policyEntity);

        log.info("Police created : {} (id={})", created.getPolicyNumber(), created.getId());
        return policyMapper.toResponse(created);
    }

    @Override
    public PolicyResponseRecord findById(Long id) {

        return policyRepository.findById(id)
                .map(policyMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Policy", "id", id));
    }

    @Override
    public PolicyResponseRecord findByPolicyNumber(String policyNumber) {

        return policyRepository.findByPolicyNumber(policyNumber)
                .map(policyMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Policy", "Policy number", policyNumber));
    }

    @Override
    public PagedResponse<PolicyResponseRecord> findAll(Pageable pageable) {

        return PagedResponse.from(policyRepository.findAll(pageable)
                .map(policyMapper::toResponse));
    }

    @Override
    public PagedResponse<PolicyResponseRecord> findByCustomer(Long customerId, Pageable pageable) {

        if(!policyRepository.existsById(customerId)) {

            throw new ResourceNotFoundException("Client", "id", customerId);
        }
        return  PagedResponse.from(policyRepository.findByCustomerId(customerId, pageable).map(policyMapper::toResponse));
    }

    @Override
    public PagedResponse<PolicyResponseRecord> findByStatus(PolicyStatus status, Pageable pageable) {
        return PagedResponse.from(policyRepository.findByStatus(status, pageable).map(policyMapper::toResponse));
    }

    @Override
    public PagedResponse<PolicyResponseRecord> findByType(PolicyType type, Pageable pageable) {
        return PagedResponse.from(policyRepository.findByPolicyType(type, pageable).map(policyMapper::toResponse));
    }

    @Override
    public PolicyResponseRecord activate(Long id) {

        log.info("Activating policy id {}", id);

        PolicyEntity policyEntity = policyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Policy", "id", id));

        if(PolicyStatus.ACTIVE.equals(policyEntity.getStatus())) {
            throw new BusinessException("ALREADY_ACTIVATED", "Policy already activated");
        }

        if(PolicyStatus.CANCELLED.equals(policyEntity.getStatus())) {
            throw new BusinessException("POLICY_CANCELLED", "Cancelled policy cannot be activated");
        }

        if(policyEntity.isExpired()) {
            throw new BusinessException("ALREADY_EXPIRED", "Policy already expired");
        }

        policyEntity.setStatus(PolicyStatus.ACTIVE);
        PolicyEntity activated = policyRepository.save(policyEntity);

        log.info("Policy {} activated", activated.getPolicyNumber());
        return policyMapper.toResponse(activated);
    }

    @Override
    @Transactional
    public PolicyResponseRecord cancel(Long id) {

        PolicyEntity policyEntity = policyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Policy", "id", id));

        if(PolicyStatus.CANCELLED.equals(policyEntity.getStatus())) {
            throw new BusinessException("ALREADY_CANCELLED", "Policy already cancelled");
        }

        policyEntity.setStatus(PolicyStatus.CANCELLED);

        PolicyEntity updated = policyRepository.save(policyEntity);

        log.info("Policy number {} successfully deleleted.", updated.getPolicyNumber());
        return policyMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {

        PolicyEntity policyEntity = policyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Policy", "id", id));

        if(PolicyStatus.ACTIVE.equals(policyEntity.getStatus())) {
            throw new BusinessException("CANNOT_DELETE_ACTIVE", "Unable to delete an active policy. Cancel it first.");
        }

        policyRepository.deleteById(id);
        log.info("Policy with id {} has been successfully deleted", id);
    }

    /**
     * Generate unique policy number :
     * Format : POL-{PREFEXI-TYPE}-{YEAR}-{SEQUENCE}
     * Example : POL-AUT-2024-1001
     */
    private String generatePolicyNumber(PolicyType type) {
        String year   = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
        String prefix = type.name().substring(0, Math.min(3, type.name().length())).toUpperCase();
        long   seq    = sequence.getAndIncrement();

        String number;
        do {
            number = String.format("POL-%s-%s-%d", prefix, year, seq);
        } while (policyRepository.existsByPolicyNumber(number));

        return number;
    }
}
