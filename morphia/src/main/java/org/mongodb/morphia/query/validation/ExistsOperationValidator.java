package org.mongodb.morphia.query.validation;

import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.query.FilterOperator;

import java.util.List;

import static java.lang.String.format;
import static org.mongodb.morphia.query.FilterOperator.EXISTS;

/**
 * Checks if the value can have the {@code FilterOperator.EXISTS} operator applied to it.  Since this class does not need state, and the
 * methods can't be static because it implements an interface, it seems to be one of the few places where the Singleton pattern seems
 * appropriate.
 */
public enum ExistsOperationValidator implements OperationValidator {
    INSTANCE;

    /**
     * Apply validation if the operator is an $exists operator.  If so, check the value is a valid value for an $exists query.  This
     * validator assumes only boolean values are valid, this even excludes the use of 1 or 0 to represent true or false.  If validation
     * fails a ValidationFailure will be added to the validationFailures list.
     *
     * @param mappedField
     * @param operator           any FilterOperator for a query
     * @param value              the value to apply the operator to
     * @param validationFailures the list to add any failures to. If validation passes or the operator is not an $exists operator, this list
     *                           will not change.
     * @return true if validation was applied, false if this validation doesn't apply to this operator.
     */
    public boolean apply(final MappedField mappedField, final FilterOperator operator, final Object value,
                         final List<ValidationFailure> validationFailures) {
        if (appliesTo(operator)) {
            validate(value, validationFailures);
            return true;
        }
        return false;
    }

    private static void validate(final Object value, final List<ValidationFailure> validationFailures) {
        if (value instanceof Boolean) {
            return;
        }
        validationFailures.add(new ValidationFailure(format("For an $exists operation, value '%s' should be a boolean type.  "
                                                            + "Instead it was a: %s",
                                                            value, value == null ? "null" : value.getClass()
                                                           )));
    }

    private static boolean appliesTo(final FilterOperator operator) {
        return operator.equals(EXISTS);
    }
}
