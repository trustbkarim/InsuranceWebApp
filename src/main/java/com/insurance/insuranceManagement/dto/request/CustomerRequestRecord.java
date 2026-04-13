package com.insurance.insuranceManagement.dto.request;

import java.time.LocalDate;

public record CustomerRequestRecord(String firstName, String lastname, String email, String phone, LocalDate birthDate, String address, String city, String country) {
}
