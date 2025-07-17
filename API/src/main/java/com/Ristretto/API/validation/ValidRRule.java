package com.Ristretto.API.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidRRuleValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRRule {
    String message() default "Regra de repetição inválida segundo RFC 5545";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
