package org.mongodb.morphia.query.validation;

import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.query.FilterOperator;

import java.util.List;

public final class SizeOperationValidator implements OperationValidator {
    private SizeOperationValidator() { }

    public static boolean validate(final MappedField mf, final FilterOperator operator, final Object value) {
        return operator.equals(FilterOperator.SIZE)
               && (List.class.isAssignableFrom(mf.getType()) || mf.getType().isArray()) 
               && value instanceof Integer;
    }
}
