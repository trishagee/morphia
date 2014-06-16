package org.mongodb.morphia.query.validation;

import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.query.FilterOperator;

import java.util.List;

import static java.lang.String.format;
import static org.mongodb.morphia.query.FilterOperator.SIZE;

/**
 * Checks if the value can have the {@code FilterOperator.ALL} operator applied to it.  Since this class does not need state, and the
 * methods can't be static because it implements an interface, it seems to be one of the few places where the Singleton pattern seems
 * appropriate.
 */
public enum SizeOperationValidator implements OperationValidator {
    INSTANCE;

    @Override
    public boolean apply(final MappedField mappedField, final FilterOperator operator, final Object value,
                         final List<ValidationFailure> validationFailures) {
        if (appliesTo(operator)) {
            validate(mappedField, value, validationFailures);
            return true;
        }
        return false;
    }

    private static void validate(final MappedField mappedField, final Object value,
                                 final List<ValidationFailure> validationFailures) {
        if (!(value instanceof Integer)) {
            validationFailures.add(new ValidationFailure(format("For a $size operation, value '%s' should be an integer type.  "
                                                                + "Instead it was a: %s",
                                                                value, value == null ? "null" : value.getClass()
                                                               )));

        }
        if (!(List.class.isAssignableFrom(mappedField.getType()) || mappedField.getType().isArray())) {
            validationFailures.add(new ValidationFailure(format("For a $size operation, field '%s' should be a List or array.  "
                                                                + "Instead it was a: %s",
                                                                mappedField, mappedField.getType()
                                                               )));
        }
    }

    private static boolean appliesTo(final FilterOperator operator) {
        return operator.equals(SIZE);
    }
}
