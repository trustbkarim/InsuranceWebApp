package com.insurance.insuranceManagement.service.impl;

import com.insurance.insuranceManagement.domain.entity.CustomerEntity;
import com.insurance.insuranceManagement.domain.repository.CustomerRepository;
import com.insurance.insuranceManagement.dto.request.CustomerRequestRecord;
import com.insurance.insuranceManagement.dto.response.CustomerResponseRecord;
import com.insurance.insuranceManagement.dto.response.PagedResponse;
import com.insurance.insuranceManagement.exception.DuplicateResourceException;
import com.insurance.insuranceManagement.exception.ResourceNotFoundException;
import com.insurance.insuranceManagement.mapper.CustomerMapper;
import com.insurance.insuranceManagement.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private CustomerMapper customerMapper;

    @Override
    @Transactional
    public CustomerResponseRecord createCustomer(CustomerRequestRecord customerRequest) {

        log.info("Creation of client with e-mail {} : ", customerRequest.email());

        if(customerRepository.existsByEmail(customerRequest.email())) {

            throw new DuplicateResourceException("Client", "Email", customerRequest.email());
        }

        CustomerEntity customerEntity = customerMapper.toEntity(customerRequest);
        CustomerEntity saved = customerRepository.save(customerEntity);

        log.info("Client created successfully, id = {}", saved.getId());
        return customerMapper.toResponse(saved);
    }

    @Override
    public CustomerResponseRecord findCustomerById(Long id) {

        return customerRepository.findById(id)
                .map(customerMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
    }

    @Override
    public CustomerResponseRecord findCustomerByEmail(String email) {

        return customerRepository.findByEmail(email)
                .map(customerMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "email", email));
    }

    @Override
    public PagedResponse<CustomerResponseRecord> findAllCustomers(Pageable pageable) {

        return PagedResponse.from(customerRepository.findAll(pageable).map(customerMapper::toResponse));
    }

    @Override
    public PagedResponse<CustomerResponseRecord> searchCustomer(String keyword, Pageable pageable) {

        log.debug("Search for client with = '{}'", keyword);
        return PagedResponse.from(customerRepository.searchCustomers(keyword, pageable).map(customerMapper::toResponse));
    }

    @Override
    @Transactional
    public CustomerResponseRecord updateCustomer(Long id, CustomerRequestRecord customerRequest) {

        log.debug("Update client ID = {}", id);

        CustomerEntity customerEntity = customerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));

        customerMapper.updateEntityFromRequest(customerRequest, customerEntity);

        CustomerEntity updated = customerRepository.save(customerEntity);

        log.debug("Client updated successfully ID = {}", id);

        return customerMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {

        log.info("Delete client id {} ", id);

        if(customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client", "id", id);
        }

        customerRepository.deleteById(id);

        log.info("Successfully deleted client ID {}", id);
    }
}
