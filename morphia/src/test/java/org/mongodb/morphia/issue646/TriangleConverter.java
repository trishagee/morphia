package org.mongodb.morphia.issue646;

import org.jetbrains.annotations.NotNull;
import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

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
    public Object encode(@NotNull final Object value, final MappedField optionalExtraInfo) {
        if (!(value instanceof Triangle)) {
            throw new RuntimeException("Did not expect " + value.getClass().getName());
        }

        return "Triangle";
    }
}
