package org.mongodb.morphia.query.validation;

import org.mongodb.morphia.query.FilterOperator;

import java.util.Map;

public final class InOperationValidator implements OperationValidator {
    private InOperationValidator() {
    }

    public static boolean validate(final FilterOperator operator, final Object value) {
        return operator.equals(FilterOperator.IN)
               && (value.getClass().isArray()
                   || Iterable.class.isAssignableFrom(value.getClass())
                   || Map.class.isAssignableFrom(value.getClass()));
    }
}
