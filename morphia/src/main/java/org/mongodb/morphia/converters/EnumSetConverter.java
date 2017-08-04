package org.mongodb.morphia.converters;

import org.jetbrains.annotations.NotNull;
import org.mongodb.morphia.mapping.MappedField;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author scotthernandez
 */
public class EnumSetConverter extends TypeConverter implements SimpleValueConverter {

    private final EnumConverter ec = new EnumConverter();

    /**
     * Creates the Converter.
     */
    public EnumSetConverter() {
        super(EnumSet.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object decode(final Class targetClass, @NotNull final Object fromDBObject, final MappedField optionalExtraInfo) {
        final Class enumType = optionalExtraInfo.getSubClass();

        final List l = (List) fromDBObject;
        if (l.isEmpty()) {
            return EnumSet.noneOf(enumType);
        }

        final List enums = new ArrayList();
        for (final Object object : l) {
            enums.add(ec.decode(enumType, object));
        }
        return EnumSet.copyOf(enums);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<?> encode(@NotNull final Object value, final MappedField optionalExtraInfo) {
        final List values = new ArrayList();

        final EnumSet s = (EnumSet) value;
        final Object[] array = s.toArray();
        for (final Object anArray : array) {
            values.add(ec.encode(anArray).orElse(null));
        }

        return Optional.of(values);
    }
}
