package com.Ristretto.API.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidTimezoneValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTimezone {
    String message() default "Timezone inválida";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
