// src/main/java/com/Ristretto/API/validation/ChronologicalDates.java
package com.Ristretto.API.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ChronologicalDatesValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ChronologicalDates {
    String message() default "A data de término deve ser igual ou posterior à data de início";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String start();
    String end();
}
