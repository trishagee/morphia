package org.mongodb.morphia.query.validation;

import java.util.regex.Pattern;

public final class PatternValueTypeValidator implements Validator {
    private PatternValueTypeValidator() { }

    public static boolean validate(final Class<?> type, final Object value) {
        return value instanceof Pattern && String.class.equals(type);
    }
}
