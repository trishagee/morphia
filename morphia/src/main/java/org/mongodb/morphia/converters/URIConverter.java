package org.mongodb.morphia.converters;


import org.mongodb.morphia.mapping.MappedField;

import java.net.URI;
import java.util.Optional;


/**
 * @author scotthernandez
 */
public class URIConverter extends TypeConverter<URI> implements SimpleValueConverter {

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
    public URI decode(final Class targetClass, final Object val, final MappedField optionalExtraInfo) {
        if (val == null) {
            return null;
        }

        return URI.create(val.toString().replace("%46", "."));
    }

    @Override
    public String encode(final Optional<URI> uri, final MappedField optionalExtraInfo) {
        if (!uri.isPresent()) {
            return null;
        }

        return uri.get().toString().replace(".", "%46");
    }
}
