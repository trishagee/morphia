package org.mongodb.morphia.query;

import static java.lang.String.format;

class ValidationExceptionFactory {
    private final String fieldName;
    private final String className;
    private final String queryPath;

    ValidationExceptionFactory(String fieldName, String className, String queryPath) {
        this.fieldName = fieldName;
        this.className = className;
        this.queryPath = queryPath;
    }

    ValidationException fieldNotFoundException() {
        return new ValidationException(format(
                "The field '%s' could not be found in '%s' while validating - %s; if you wish " +
                "to continue please disable validation.", fieldName, className, queryPath));
    }

    ValidationException queryingReferenceFieldsException() {
        return new ValidationException(format("Cannot use dot-notation past '%s' in '%s'; found " +
                                           "while validating - %s", fieldName, className, queryPath));

    }
}
