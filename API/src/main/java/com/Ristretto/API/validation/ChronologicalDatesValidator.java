
package com.Ristretto.API.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ValidationException;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

public class ChronologicalDatesValidator implements ConstraintValidator<ChronologicalDates, Object> {

    private String startField;
    private String endField;

    @Override
    public void initialize(ChronologicalDates constraintAnnotation) {
        this.startField = constraintAnnotation.start();
        this.endField = constraintAnnotation.end();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            Method getStart = value.getClass().getMethod(startField);
            Method getEnd = value.getClass().getMethod(endField);

            LocalDateTime start = (LocalDateTime) getStart.invoke(value);
            LocalDateTime end = (LocalDateTime) getEnd.invoke(value);

            if (start == null || end == null) return true;

            return !end.isBefore(start);

        } catch (Exception e) {
            return false;
        }
    }
}
