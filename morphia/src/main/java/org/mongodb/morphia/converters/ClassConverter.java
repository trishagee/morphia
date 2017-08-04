package org.mongodb.morphia.converters;


import org.jetbrains.annotations.NotNull;
import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.mapping.MappingException;

import java.util.Optional;


/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author scotthernandez
 */
public class ClassConverter extends TypeConverter implements SimpleValueConverter {

    /**
     * Creates the Converter.
     */
    public ClassConverter() {
        super(Class.class);
    }

    @Override
    public Object decode(final Class targetClass, @NotNull final Object fromDBObject, final MappedField optionalExtraInfo) {
        final String l = fromDBObject.toString();
        try {
            return Class.forName(l);
        } catch (ClassNotFoundException e) {
            throw new MappingException("Cannot create class from Name '" + l + "'", e);
        }
    }

    @Override
    public Optional<?> encode(@NotNull final Object value, final MappedField optionalExtraInfo) {
        return Optional.ofNullable(((Class) value).getName());
    }
}
