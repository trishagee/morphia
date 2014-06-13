package org.mongodb.morphia.query.validation;

import org.mongodb.morphia.Key;

public final class KeyValueTypeValidator implements Validator {
    private KeyValueTypeValidator() {
    }

    public static boolean validate(final Class<?> type, final Object value) {
        return value.getClass().isAssignableFrom(Key.class) && type.equals(((Key) value).getKindClass());
    }
}
