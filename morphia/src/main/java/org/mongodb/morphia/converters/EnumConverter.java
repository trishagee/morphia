package org.mongodb.morphia.converters;


import org.mongodb.morphia.mapping.MappedField;

import java.util.Optional;


/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author scotthernandez
 */
public class EnumConverter extends TypeConverter<Enum> implements SimpleValueConverter {

    @Override
    @SuppressWarnings("unchecked")
    public Enum decode(final Class targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
        return Enum.valueOf(targetClass, fromDBObject.toString());
    }

    @Override
    public Object encode(final Optional<Enum> value, final MappedField optionalExtraInfo) {
        return value.map(Enum::name)
                    .orElse(null);
    }

    @Override
    protected boolean isSupported(final Class c, final MappedField optionalExtraInfo) {
        return c.isEnum();
    }

}
