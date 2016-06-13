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
    public Object decode(final Class targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
        if (fromDBObject == null) {
            return null;
        }

        if (fromDBObject instanceof String) {
            return new Triangle();
        }

        throw new RuntimeException(
                                      "Did not expect " + fromDBObject.getClass().getName());
    }

    @Override
    public Object encode(final Optional value, final MappedField optionalExtraInfo) {
        if (!value.isPresent()) {
            return null;
        }

        if (!(value.get() instanceof Triangle)) {
            throw new RuntimeException(
                                          "Did not expect " + value.getClass().getName());
        }

        return "Triangle";
    }
}
