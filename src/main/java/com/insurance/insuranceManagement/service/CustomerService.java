package com.insurance.insuranceManagement.service;

import com.insurance.insuranceManagement.dto.request.CustomerRequestRecord;
import com.insurance.insuranceManagement.dto.response.CustomerResponseRecord;
import com.insurance.insuranceManagement.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface CustomerService {

    CustomerResponseRecord createCustomer(CustomerRequestRecord customerRequest);
    CustomerResponseRecord findCustomerById(Long id);
    CustomerResponseRecord findCustomerByEmail(String email);
    PagedResponse<CustomerResponseRecord> findAllCustomers(Pageable pageable);
    PagedResponse<CustomerResponseRecord> searchCustomer(String keyword, Pageable pageable);
    CustomerResponseRecord updateCustomer(Long id, CustomerRequestRecord customerRequest);
    void deleteCustomer(Long id);

}
