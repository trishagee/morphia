package org.mongodb.morphia.query.validation;

public final class DefaultTypeValueValidator implements Validator {
    private DefaultTypeValueValidator() {
    }

    public static boolean validate(final Class<?> type, final Object value) {
        return value.getClass().isAssignableFrom(type)
               || value.getClass().getSimpleName().equalsIgnoreCase(type.getSimpleName());
    }
}
