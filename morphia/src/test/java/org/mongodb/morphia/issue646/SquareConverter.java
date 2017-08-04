package org.mongodb.morphia.issue646;

import org.jetbrains.annotations.NotNull;
import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

import java.util.Optional;

public class SquareConverter extends TypeConverter implements SimpleValueConverter {

    public SquareConverter() {
        super(Square.class);
    }

    @Override
    public Object decode(final Class targetClass, @NotNull final Object fromDBObject, final MappedField optionalExtraInfo) {
        if (fromDBObject instanceof String) {
            return new Square();
        }

        throw new RuntimeException("Did not expect " + fromDBObject.getClass().getName());
    }

    @Override
    public Optional<String> encode(@NotNull final Object value, final MappedField optionalExtraInfo) {
        if (!(value instanceof Square)) {
            throw new RuntimeException("Did not expect " + value.getClass().getName());
        }

        return Optional.of("Square");
    }
}
