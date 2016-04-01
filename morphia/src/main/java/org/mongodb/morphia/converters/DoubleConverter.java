package org.mongodb.morphia.converters;


import org.mongodb.morphia.mapping.MappedField;


/**
 * Decodes <code>double</code>s and <code>Double</code>s.
 */
public class DoubleConverter extends TypeConverter<Double> implements SimpleValueConverter {

    /**
     * Registers the converter to decode double and its Object wrapper type.
     */
    public DoubleConverter() {
        super(double.class, Double.class);
    }

    @Override
    public Double decode(final Class targetClass, final Object val, final MappedField optionalExtraInfo) {
        if (val instanceof Double) {
            return (Double) val;
        }

        if (val instanceof Number) {
            return ((Number) val).doubleValue();
        }

        return Double.parseDouble(val.toString());
    }
}
