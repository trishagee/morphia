package org.mongodb.morphia.converters;


import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.mapping.MappingException;

import java.util.Optional;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author scotthernandez
 */
public class CharacterConverter extends TypeConverter<Character> implements SimpleValueConverter {
    /**
     * Creates the Converter.
     */
    public CharacterConverter() {
        super(char.class, Character.class);
    }

    @Override
    public Character decode(final Class targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
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
    public Object encode(final Optional<Character> value, final MappedField optionalExtraInfo) {
        return value.filter(character -> !character.equals('\0'))
                    .map(String::valueOf)
                    .orElse(null);
    }
}
