package org.mongodb.morphia.issue646;

import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

import java.util.Optional;

public class TriangleConverter extends TypeConverter implements SimpleValueConverter {

    public TriangleConverter() {
        super(Triangle.class);
    }

    @Override
    public Optional<Triangle> decode(final Class targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
        if (fromDBObject == null) {
            return Optional.empty();
        }

        if (fromDBObject instanceof String) {
            return Optional.of(new Triangle());
        }

        throw new RuntimeException("Did not expect " + fromDBObject.getClass().getName());
    }

    @Override
    public Object encode(final Object value, final MappedField optionalExtraInfo) {
        if (value == null) {
            return null;
        }

        if (!(value instanceof Triangle)) {
            throw new RuntimeException(
                                          "Did not expect " + value.getClass().getName());
        }

        return "Triangle";
    }
}
