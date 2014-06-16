package org.mongodb.morphia.query.validation;

import org.mongodb.morphia.mapping.MappedClass;
import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.mapping.Mapper;

public final class MappedFieldTypeValidator implements Validator {
    private MappedFieldTypeValidator() {
    }
    //TODO: Trisha - might only need to pass in the mapped class

    /**
     * Assumes the {@code type} is not null
     *
     * @param mapper
     * @param type
     * @param value
     * @return
     */
    public static boolean validate(final Mapper mapper, final Class<?> type, final Object value) {
        MappedClass mappedClassForType = mapper.getMappedClass(type);
        if (mappedClassForType.getMappedIdField() == null) {
            return false;
        }
        Class<?> classOfValue = value.getClass();
        Class classOfIdFieldForType = mappedClassForType.getMappedIdField().getConcreteType();
        return classOfValue.equals(classOfIdFieldForType);
    }

    static boolean isArrayOfNumbers(final MappedField mappedField) {
        Class subClass = mappedField.getSubClass();
        return mappedField.getType().isArray()
               && (subClass == int.class || subClass == long.class || subClass == double.class || subClass == float.class);
    }

    static boolean isIterableOfNumbers(final MappedField mappedField) {
        return Iterable.class.isAssignableFrom(mappedField.getType()) && Number.class.isAssignableFrom(mappedField.getSubClass());
    }
}
