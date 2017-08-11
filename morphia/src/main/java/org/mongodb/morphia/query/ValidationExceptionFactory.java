package org.mongodb.morphia.query;

import org.jetbrains.annotations.NotNull;

import static java.lang.String.format;

class ValidationExceptionFactory {
    private final String propertyPath;

    ValidationExceptionFactory(@NotNull String propertyPath) {
        this.propertyPath = propertyPath;
    }

    ValidationException fieldNotFoundException(String className, String fieldName) {
        return new ValidationException(format(
                "The field '%s' could not be found in '%s' while validating - %s; if you wish " +
                "to continue please disable validation.", fieldName, className, propertyPath));
    }

    ValidationException queryingReferenceFieldsException(String className, String fieldName) {
        return new ValidationException(format("Cannot use dot-notation past '%s' in '%s'; found " +
                                           "while validating - %s", fieldName, className, propertyPath));

    }
}
