package org.mongodb.morphia.converters;

import org.mongodb.morphia.mapping.MappedField;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author scotthernandez
 */
public class EnumSetConverter extends TypeConverter<EnumSet> implements SimpleValueConverter {

    private final EnumConverter ec = new EnumConverter();

    /**
     * Creates the Converter.
     */
    public EnumSetConverter() {
        super(EnumSet.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public EnumSet decode(final Class targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
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
    public Object encode(final Optional<EnumSet> value, final MappedField optionalExtraInfo) {
        return value.map(this::getValues)
                    .orElse(null);
    }

    @SuppressWarnings("unchecked")
    private List getValues(EnumSet enumSet) {
        final List values = new ArrayList();
        final Object[] array = enumSet.toArray();
        for (final Object anArray : array) {
            values.add(ec.encode(Optional.ofNullable((Enum)anArray)));
        }
        return values;
    }
}
