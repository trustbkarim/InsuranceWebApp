package com.insurance.insuranceManagement.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

    private static final String PHONE_REGEX =
            "^(\\+212[0-9]{9}|0[0-9]{9})$";

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if (phone == null || phone.isBlank()) {
            return true;
        }
        return phone.matches(PHONE_REGEX);
    }
}
