package org.mongodb.morphia.converters;


import org.jetbrains.annotations.NotNull;
import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.utils.ReflectionUtils;

import java.util.List;


/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author scotthernandez
 */
public class LongConverter extends TypeConverter implements SimpleValueConverter {

    /**
     * Creates the Converter.
     */
    public LongConverter() {
        super(long.class, Long.class, long[].class, Long[].class);
    }

    @Override
    public Object decode(final Class targetClass, @NotNull final Object val, final MappedField optionalExtraInfo) {
        if (val instanceof Long) {
            return val;
        }

        if (val instanceof Number) {
            return ((Number) val).longValue();
        }

        if (val instanceof List) {
            final Class<?> type = targetClass.isArray() ? targetClass.getComponentType() : targetClass;
            return ReflectionUtils.convertToArray(type, (List<?>) val);
        }

        return Long.parseLong(val.toString());
    }

}
