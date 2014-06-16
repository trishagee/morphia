package org.mongodb.morphia.query.validation;

import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.query.FilterOperator;

import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.mongodb.morphia.query.FilterOperator.ALL;

public enum AllOperationValidator implements OperationValidator {
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
            validationFailures.add(new ValidationFailure(format("For an $all operation, value cannot be null.")));
        } else if (!(value.getClass().isArray()
                     || Iterable.class.isAssignableFrom(value.getClass())
                     || Map.class.isAssignableFrom(value.getClass()))) {
            validationFailures.add(new ValidationFailure(format("For an $all operation, value '%s' should be an array, "
                                                                + "an Iterable, or a Map.  Instead it was a: %s",
                                                                value, value.getClass()
                                                               )));
        }
    }

    private static boolean appliesTo(final FilterOperator operator) {
        return operator.equals(ALL);
    }
}
