package org.mongodb.morphia.converters;


import org.jetbrains.annotations.NotNull;
import org.mongodb.morphia.mapping.MappedField;

import java.util.UUID;


/**
 * provided by http://code.google.com/p/morphia/issues/detail?id=320
 *
 * @author stummb
 * @author scotthernandez
 */
public class UUIDConverter extends TypeConverter implements SimpleValueConverter {

    /**
     * Creates the Converter.
     */
    public UUIDConverter() {
        super(UUID.class);
    }

    @Override
    public Object decode(final Class targetClass, @NotNull final Object fromDBObject, final MappedField optionalExtraInfo) {
        return UUID.fromString((String) fromDBObject);
    }

    @Override
    public Object encode(@NotNull final Object value, final MappedField optionalExtraInfo) {
        return value.toString();
    }
}
