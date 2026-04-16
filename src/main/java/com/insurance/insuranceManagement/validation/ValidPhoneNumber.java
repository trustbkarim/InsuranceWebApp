package com.insurance.insuranceManagement.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhoneNumber {

    String message() default "Phone number is invalid (accepted formats : +212XXXXXXXXX or 0XXXXXXXXX)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
