package org.mongodb.morphia.geo;

import com.mongodb.BasicDBList;
import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

import java.util.ArrayList;
import java.util.List;

public class PointListConverter extends TypeConverter implements SimpleValueConverter {
    private final CoordinateConverter coordinateConverter = new CoordinateConverter();

    public PointListConverter() {
        super(PointCollection.class, LineString.class);
        coordinateConverter.setMapper(getMapper());
    }

    @Override
    public Object encode(final Object value, final MappedField optionalExtraInfo) {
        PointCollection pointCollection = (PointCollection) value;
        BasicDBList dbList = new BasicDBList();
        for (final Point point : pointCollection.getPoints()) {
            dbList.add(coordinateConverter.encode(point));
        }
        return dbList;
    }

    @Override
    @SuppressWarnings("unchecked") //always going to have unchecked casts when decoding
    public Object decode(final Class<?> targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
        List coordinates = (List) fromDBObject;
        List<Point> points = new ArrayList<Point>();
        for (final Object pointsAsArray : coordinates) {
            points.add((Point) coordinateConverter.decode(Point.class, pointsAsArray, optionalExtraInfo));
        }
        return points;
    }
}
