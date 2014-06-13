package org.mongodb.morphia.query.validation;

import org.mongodb.morphia.query.FilterOperator;

import java.util.List;

public final class SizeOperationValidator implements Validator {
    private SizeOperationValidator() { }

    public static boolean validate(final Class<?> type, final FilterOperator operator, final Object value) {
        return operator.equals(FilterOperator.SIZE)
               && (type.isAssignableFrom(List.class) && value instanceof Integer);
        //TODO: Trish - tidy the logic
    }
}
