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
        Point point = (Point) value;
        return new double[]{point.getLongitude(), point.getLatitude()};
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object decode(final Class<?> targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
        List<Double> coordinateList = (List<Double>) fromDBObject;
        return new Point(coordinateList.get(1), coordinateList.get(0));
    }
}
