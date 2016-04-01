package org.mongodb.morphia.converters;


import org.mongodb.morphia.mapping.MappedField;


/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author scotthernandez
 */
public class BooleanConverter extends TypeConverter<Boolean> implements SimpleValueConverter {

    /**
     * Creates the Converter.
     */
    public BooleanConverter() {
        super(boolean.class, Boolean.class);
    }

    @Override
    public Boolean decode(final Class targetClass, final Object val, final MappedField optionalExtraInfo) {
        if (val instanceof Boolean) {
            return (Boolean) val;
        }

        //handle the case for things like the ok field
        if (val instanceof Number) {
            return ((Number) val).intValue() != 0;
        }

        return Boolean.parseBoolean(val.toString());
    }
}
