package org.mongodb.morphia.converters;


import org.mongodb.morphia.mapping.MappedField;

import java.util.Optional;


/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author scotthernandez
 */
public class EnumConverter extends TypeConverter implements SimpleValueConverter {

    @Override
    @SuppressWarnings("unchecked")
    public Object decode(final Class targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
        return Enum.valueOf(targetClass, fromDBObject.toString());
    }

    @Override
    public Object encode(final Optional value, final MappedField optionalExtraInfo) {
        if (!value.isPresent()) {
            return null;
        }

        return getName((Enum) value.get());
    }

    @Override
    protected boolean isSupported(final Class c, final MappedField optionalExtraInfo) {
        return c.isEnum();
    }

    private <T extends Enum> String getName(final T value) {
        return value.name();
    }
}
