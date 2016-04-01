package org.mongodb.morphia.converters;


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
    public Object decode(final Class targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
        final String className = fromDBObject.toString();
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new MappingException("Cannot create class from Name '" + className + "'", e);
        }
    }

    @Override
    public Object encode(final Optional value, final MappedField optionalExtraInfo) {
        if (!value.isPresent()) {
            return null;
        } else {
            return ((Class) value.get()).getName();
        }
    }
}
