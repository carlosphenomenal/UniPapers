package com.unipapers.backend.Common.Validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MakerereEmailValidator implements ConstraintValidator<MakerereEmail, String> {

    private static final String MAKERERE_EMAIL_DOMAIN = ".mak.ac.ug";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Allow null values (use @NotNull for required validation)
        if (value == null) {
            return true;
        }

        // Check if email ends with Makerere domain
        return value.toLowerCase().endsWith(MAKERERE_EMAIL_DOMAIN);
    }
}


