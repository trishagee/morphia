package org.mongodb.morphia.query.validation;

import org.mongodb.morphia.Key;
import org.mongodb.morphia.annotations.Entity;

public final class EntityAnnotatedValueValidator implements Validator {
    private EntityAnnotatedValueValidator() {
    }

    public static boolean validate(final Class<?> type, final Object value) {
        return value.getClass().getAnnotation(Entity.class) != null && Key.class.equals(type);
    }
}
