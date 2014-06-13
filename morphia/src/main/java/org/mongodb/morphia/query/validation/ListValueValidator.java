package org.mongodb.morphia.query.validation;

import java.util.List;

public final class ListValueValidator implements Validator {
    private ListValueValidator() {
    }

    public static boolean validator(final Object value) {
        return value instanceof List;
    }
}
