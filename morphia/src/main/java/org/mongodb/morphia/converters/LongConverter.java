package org.mongodb.morphia.converters;


import org.mongodb.morphia.mapping.MappedField;

/**
 * Decodes <code>long</code>s and <code>Long</code>s.
 */
public class LongConverter extends TypeConverter<Long> implements SimpleValueConverter {

    /**
     * Registers the converter to decode long and its Object wrapper type.
     */
    public LongConverter() {
        super(long.class, Long.class);
    }

    @Override
    public Long decode(final Class targetClass, final Object val, final MappedField optionalExtraInfo) {
        if (val instanceof Long) {
            return (Long) val;
        }

        if (val instanceof Number) {
            return ((Number) val).longValue();
        }

        return Long.parseLong(val.toString());
    }

}
