package org.mongodb.morphia.query.validation;

import org.mongodb.morphia.query.FilterOperator;

/**
 * Checks if the value can have the {@code FilterOperator.EXISTS} operator applied to it.
 */
public final class ExistsOperationValidator implements Validator {

    private ExistsOperationValidator() { }

    /**
     * Validate whether this operator can work on the given value.
     *
     * @param operator the query operator
     * @param value    the value to apply the operator to
     * @return true if the operator is an exists operator and the value can have that applied to it
     */
    public static boolean validate(final FilterOperator operator, final Object value) {
        return operator.equals(FilterOperator.EXISTS)
               && (value instanceof Boolean);
    }

    // what you want is an "apply" method that can check whether it should validate, and then do so if it should

}
