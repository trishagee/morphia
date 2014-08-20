package org.mongodb.morphia.geo;

import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

import java.util.List;

/**
 * Converts just the coordinates from the Point class without the GeoJson type.
 */
public class CoordinateConverter extends TypeConverter implements SimpleValueConverter {
    public CoordinateConverter() {
        super(Point.class);
    }

    @Override
    public Object encode(final Object value, final MappedField optionalExtraInfo) {
        return encode((Point) value);
    }

    static double[] encode(final Point point) {
        return new double[]{point.getLongitude(), point.getLatitude()};
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object decode(final Class<?> targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
        return decode((List<Double>) fromDBObject);
    }

    static Point decode(final List<Double> coordinates) {
        return new Point(coordinates.get(1), coordinates.get(0));
    }
}
