package com.unipapers.backend.Common.Validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MakerereEmailValidator.class)
@Documented
public @interface MakerereEmail {
    String message() default "This is not a valid Makerere email";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

