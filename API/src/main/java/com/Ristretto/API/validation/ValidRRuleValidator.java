package com.Ristretto.API.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import net.fortuna.ical4j.model.property.RRule;

public class ValidRRuleValidator implements ConstraintValidator<ValidRRule, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true;
        try {
            new RRule(value).validate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
