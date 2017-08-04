package org.mongodb.morphia.converters;


import org.jetbrains.annotations.NotNull;
import org.mongodb.morphia.mapping.MappedField;

import java.util.Optional;


/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author scotthernandez
 */
public class EnumConverter extends TypeConverter implements SimpleValueConverter {

    @Override
    @SuppressWarnings("unchecked")
    public Object decode(final Class targetClass, @NotNull final Object fromDBObject, final MappedField optionalExtraInfo) {
        return Enum.valueOf(targetClass, fromDBObject.toString());
    }

    @Override
    public Optional<?> encode(@NotNull final Object value, final MappedField optionalExtraInfo) {
        return Optional.ofNullable(((Enum) value).name());
    }

    @Override
    protected boolean isSupported(final Class c, final MappedField optionalExtraInfo) {
        return c.isEnum();
    }

}
