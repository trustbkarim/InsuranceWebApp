package com.insurance.insuranceManagement.domain.repository;

import com.insurance.insuranceManagement.domain.entity.CustomerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

    Optional<CustomerEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    Page<CustomerEntity> findByCity(String city, Pageable pageable);
    @Query("SELECT c FROM CustomerEntity c WHERE " +
            "LOWER(c.firstname) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.lastname)  LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.email)     LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<CustomerEntity> searchCustomers(@Param("search") String search, Pageable pageable);
}
