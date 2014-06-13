package org.mongodb.morphia.query.validation;

import static java.util.Arrays.asList;

@SuppressWarnings("unchecked")
public final class IntegerValueTypeValidator implements Validator {
    private IntegerValueTypeValidator() {
    }

    public static boolean validate(final Class<?> type, final Object value) {
        return value instanceof Integer
               && (asList(int.class, long.class, Long.class, Integer.class).contains(type));
    }
}
