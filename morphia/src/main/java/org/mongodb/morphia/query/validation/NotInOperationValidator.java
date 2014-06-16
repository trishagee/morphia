package org.mongodb.morphia.query.validation;

import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.query.FilterOperator;

import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.mongodb.morphia.query.FilterOperator.NOT_IN;

/**
 * Checks if the value can have the {@code FilterOperator.NOT_IN} operator applied to it.  Since this class does not need state, and the
 * methods can't be static because it implements an interface, it seems to be one of the few places where the Singleton pattern seems
 * appropriate.
 */
public enum NotInOperationValidator implements OperationValidator {
    INSTANCE;

    @Override
    public boolean apply(final MappedField mappedField, final FilterOperator operator, final Object value,
                         final List<ValidationFailure> validationFailures) {
        if (appliesTo(operator)) {
            validate(value, validationFailures);
            return true;
        }
        return false;
    }

    private static void validate(final Object value, final List<ValidationFailure> validationFailures) {
        if (value == null) {
            validationFailures.add(new ValidationFailure(format("For a $nin operation, value cannot be null.")));
        } else if (!(value.getClass().isArray()
                     || Iterable.class.isAssignableFrom(value.getClass())
                     || Map.class.isAssignableFrom(value.getClass()))) {
            validationFailures.add(new ValidationFailure(format("For a $nin operation, value '%s' should be a List or array. "
                                                                + "Instead it was a: %s",
                                                                value, value.getClass()
                                                               )));
        }
    }

    private static boolean appliesTo(final FilterOperator operator) {
        return operator.equals(NOT_IN);
    }
}
