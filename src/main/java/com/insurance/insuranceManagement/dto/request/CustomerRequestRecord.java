package com.insurance.insuranceManagement.dto.request;

import com.insurance.insuranceManagement.validation.ValidPhoneNumber;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CustomerRequestRecord(@NotBlank(message = "First name must not be blank") @Size(min = 2, max = 100, message = "First name must contain between 2 and 100 characters") String firstName,
                                    @NotBlank(message = "Last name must not be blank") @Size(min = 2, max = 100, message = "Last name must contain between 2 and 100 characters") String lastname,
                                    @NotNull(message = "E-mail is required") @Email(message = "E-mail format is invalid") String email,
                                    @ValidPhoneNumber @NotBlank String phone,
                                    @Past(message = "Birth date must be in the past") LocalDate birthDate,
                                    @Size(max = 255, message = "Address must not be greater than 255 characters") String address,
                                    @Size(max = 100) String city,
                                    @Size(max = 100) String country) {
}
