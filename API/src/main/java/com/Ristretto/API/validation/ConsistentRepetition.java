package com.Ristretto.API.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ConsistentRepetitionValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ConsistentRepetition {
    String message() default "Repetition type and rule are required when repetitive is true";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}