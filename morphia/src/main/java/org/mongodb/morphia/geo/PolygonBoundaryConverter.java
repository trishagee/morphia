package org.mongodb.morphia.geo;

import com.mongodb.BasicDBList;
import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

import java.util.ArrayList;
import java.util.List;

public class PolygonBoundaryConverter extends TypeConverter implements SimpleValueConverter {
    private final CoordinateConverter coordinateConverter = new CoordinateConverter();

    public PolygonBoundaryConverter() {
        super(Polygon.PolygonBoundary.class);
        coordinateConverter.setMapper(getMapper());
    }

    @Override
    public Object encode(final Object value, final MappedField optionalExtraInfo) {
        Polygon.PolygonBoundary polygonBoundary = (Polygon.PolygonBoundary) value;
        BasicDBList dbList = new BasicDBList();
        for (final Point point : polygonBoundary.getPoints()) {
            dbList.add(PointConverter.getEncodablePointCoordinates(point));
        }
        return dbList;
    }

    @Override
    @SuppressWarnings("unchecked") //always going to have unchecked casts when decoding
    public Object decode(final Class<?> targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
        List list = (List) fromDBObject;
        List<Point> points = new ArrayList<Point>();
        for (final Object pointsAsArray : list) {
            points.add((Point) coordinateConverter.decode(Point.class, pointsAsArray, optionalExtraInfo));
        }
        return new Polygon.PolygonBoundary(points);
    }
}
