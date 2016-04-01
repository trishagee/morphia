package org.mongodb.morphia.converters;


import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.utils.ReflectionUtils;

import java.util.List;

/**
 * Decodes arrays of the primitives (and wrappers) of boolean, int, double and long.  Will not work for floating
 * point numbers, bytes or chars.
 */
public class ArrayConverter extends TypeConverter implements SimpleValueConverter {

    /**
     * Initialises the converter to decode primitive array types.
     */
    public ArrayConverter() {
        super(boolean[].class, Boolean[].class, int[].class, Integer[].class, double[].class, Double[].class,
              long[].class, Long[].class);
    }

    @Override
    public Object decode(final Class targetClass, final Object val, final MappedField optionalExtraInfo) {
        final Class<?> type = targetClass.isArray() ? targetClass.getComponentType() : targetClass;
        return ReflectionUtils.convertToArray(type, (List<?>) val);
    }
}
