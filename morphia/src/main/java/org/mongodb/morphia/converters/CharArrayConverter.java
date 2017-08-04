package org.mongodb.morphia.converters;


import org.jetbrains.annotations.NotNull;
import org.mongodb.morphia.mapping.MappedField;

import java.lang.reflect.Array;
import java.util.Optional;


/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author scotthernandez
 */
public class CharArrayConverter extends TypeConverter implements SimpleValueConverter {
    /**
     * Creates the Converter.
     */
    public CharArrayConverter() {
        super(char[].class, Character[].class);
    }

    @Override
    public Object decode(final Class targetClass, @NotNull final Object val, final MappedField optionalExtraInfo) {
        final char[] chars = val.toString().toCharArray();
        if (targetClass.isArray() && targetClass.equals(Character[].class)) {
            return convertToWrapperArray(chars);
        }
        return chars;
    }

    @Override
    public Optional<?> encode(@NotNull final Object value, final MappedField optionalExtraInfo) {
        if (value instanceof char[]) {
            return Optional.of(new String((char[]) value));
        } else {
            final StringBuilder builder = new StringBuilder();
            final Character[] array = (Character[]) value;
            for (final Character character : array) {
                builder.append(character);
            }
            return Optional.of(builder.toString());
        }
    }

    Object convertToWrapperArray(final char[] values) {
        final int length = values.length;
        final Object array = Array.newInstance(Character.class, length);
        for (int i = 0; i < length; i++) {
            Array.set(array, i, values[i]);
        }
        return array;
    }
}
