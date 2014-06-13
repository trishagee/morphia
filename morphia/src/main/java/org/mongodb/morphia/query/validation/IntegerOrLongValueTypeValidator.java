package org.mongodb.morphia.query.validation;

import static java.util.Arrays.asList;

//TODO: Trish - naming this class according to original validation, but it needs to be turned on its head
@SuppressWarnings("unchecked")
public final class IntegerOrLongValueTypeValidator {
    private IntegerOrLongValueTypeValidator() {
    }

    public static boolean validate(final Class<?> type, final Object value) {
        return (value instanceof Integer || value instanceof Long)
               && (asList(double.class, Double.class).contains(type));
    }
}
