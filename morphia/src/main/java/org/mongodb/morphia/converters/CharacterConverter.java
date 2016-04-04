package org.mongodb.morphia.converters;


import org.jetbrains.annotations.NotNull;
import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.mapping.MappingException;

import java.util.Optional;


/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author scotthernandez
 */
public class CharacterConverter extends TypeConverter implements SimpleValueConverter {
    /**
     * Creates the Converter.
     */
    public CharacterConverter() {
        super(char.class, Character.class);
    }

    @Override
    public Object decode(final Class targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
        if (fromDBObject == null) {
            return null;
        }

        if (fromDBObject instanceof String) {
            final char[] chars = ((String) fromDBObject).toCharArray();
            if (chars.length == 1) {
                return chars[0];
            } else if (chars.length == 0) {
                return (char) 0;
            }
        }
        throw new MappingException("Trying to map multi-character data to a single character: " + fromDBObject);
    }

    @Override
    public Optional encode(@NotNull final Object value, final MappedField optionalExtraInfo) {
        return value.equals('\0') ? Optional.empty() : Optional.of(String.valueOf(value));
    }
}
