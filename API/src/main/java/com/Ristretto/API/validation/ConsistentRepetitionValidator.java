package com.Ristretto.API.validation;

import com.Ristretto.API.domain.calendarEvent.CalendarEventRegistrationData;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ConsistentRepetitionValidator implements ConstraintValidator<ConsistentRepetition, CalendarEventRegistrationData> {

    @Override
    public boolean isValid(CalendarEventRegistrationData data, ConstraintValidatorContext context) {
        if (data == null) {
            return true; // deixa outras validações tratarem nulos
        }

        if (Boolean.TRUE.equals(data.repetitive())) {
            // Se repetitivo = true, repetitionType e repetitionRule não podem ser nulos
            return data.repetitionType() != null && data.repetitionRule() != null && !data.repetitionRule().isBlank();
        }

        return true; // se não é repetitivo, não precisa validar
    }
}