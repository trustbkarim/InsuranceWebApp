package com.insurance.insuranceManagement.dto.response;

import java.time.LocalDate;

public record CustomerResponseRecord(Long id, String firstName, String lastname, String email, String phone,
                                     LocalDate birthDate, String address, String city, String country) {
}
