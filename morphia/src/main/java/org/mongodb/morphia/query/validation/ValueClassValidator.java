package org.mongodb.morphia.query.validation;

/**
 * Checks that the given value is of the required type.
 */
final class ValueClassValidator implements Validator {
    private ValueClassValidator() {
    }

    /**
     * @param value         a non-null value
     * @param requiredClass a non-null type to validate against
     */
    static boolean valueIsA(final Object value,
                            final Class requiredClass) {
        return (value.getClass().equals(requiredClass));
    }

}
