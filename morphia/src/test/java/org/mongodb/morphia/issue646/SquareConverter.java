package org.mongodb.morphia.issue646;

import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

import java.util.Optional;

public class SquareConverter extends TypeConverter implements SimpleValueConverter {

    public SquareConverter() {
        super(Square.class);
    }

    @Override
    public Object decode(final Class targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
        if (fromDBObject == null) {
            return null;
        }

        if (fromDBObject instanceof String) {
            return new Square();
        }

        throw new RuntimeException(
                                      "Did not expect " + fromDBObject.getClass().getName());
    }

    @Override
    public Object encode(final Optional value, final MappedField optionalExtraInfo) {
        if (!value.isPresent()) {
            return null;
        }

        if (!(value.get() instanceof Square)) {
            throw new RuntimeException(
                                          "Did not expect " + value.getClass().getName());
        }

        return "Square";
    }
}
