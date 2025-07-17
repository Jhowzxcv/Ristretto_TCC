package com.Ristretto.API.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.ZoneId;
import java.time.zone.ZoneRulesException;

public class ValidTimezoneValidator implements ConstraintValidator<ValidTimezone, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true;

        try {
            ZoneId.of(value);
            return true;
        } catch (ZoneRulesException e) {
            return false;
        }
    }
}
