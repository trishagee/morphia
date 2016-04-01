package org.mongodb.morphia.converters;


import org.mongodb.morphia.mapping.MappedField;


/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author scotthernandez
 */
public class IntegerConverter extends TypeConverter<Integer> implements SimpleValueConverter {
    /**
     * Creates the Converter.
     */
    public IntegerConverter() {
        super(int.class, Integer.class);
    }

    @Override
    public Integer decode(final Class<Integer> targetClass, final Object val, final MappedField optionalExtraInfo) {
        if (val instanceof Integer) {
            return (Integer) val;
        }

        if (val instanceof Number) {
            return ((Number) val).intValue();
        }

        return Integer.parseInt(val.toString());
    }
}
