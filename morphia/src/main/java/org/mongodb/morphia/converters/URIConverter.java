package org.mongodb.morphia.converters;


import org.jetbrains.annotations.NotNull;
import org.mongodb.morphia.mapping.MappedField;

import java.net.URI;
import java.util.Optional;


/**
 * @author scotthernandez
 */
public class URIConverter extends TypeConverter implements SimpleValueConverter {

    /**
     * Creates the Converter.
     */
    public URIConverter() {
        this(URI.class);
    }

    protected URIConverter(final Class clazz) {
        super(clazz);
    }

    @Override
    public Object decode(final Class targetClass, @NotNull final Object val, final MappedField optionalExtraInfo) {
        return URI.create(val.toString().replace("%46", "."));
    }

    @Override
    public Optional<?> encode(@NotNull final Object uri, final MappedField optionalExtraInfo) {
        return Optional.of(uri.toString().replace(".", "%46"));
    }
}
