package org.mongodb.morphia.query.validation;

import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.query.FilterOperator;

import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.mongodb.morphia.query.FilterOperator.IN;

/**
 * Checks if the value can have the {@code FilterOperator.IN} operator applied to it.
 */
public final class InOperationValidator extends OperationValidator {
    private static final InOperationValidator INSTANCE = new InOperationValidator();

    private InOperationValidator() {
    }

    @Override
    protected void validate(final MappedField mappedField, final Object value, final List<ValidationFailure> validationFailures) {
        if (value == null) {
            validationFailures.add(new ValidationFailure(format("For an $in operation, value cannot be null.")));
            //TODO: Trish - extract this check for array types out as it's used in multiple places
        } else if (!(value.getClass().isArray()
                     || Iterable.class.isAssignableFrom(value.getClass())
                     || Map.class.isAssignableFrom(value.getClass()))) {
            validationFailures.add(new ValidationFailure(format("For a $in operation, value '%s' should be a List or array.  "
                                                                + "Instead it was a: %s",
                                                                value, value.getClass()
                                                               )));
        }
    }

    @Override
    protected FilterOperator getOperator() {
        return IN;
    }

    /**
     * Get the instance.
     *
     * @return the Singleton instance of this validator
     */
    public static InOperationValidator getInstance() {
        return INSTANCE;
    }
}
