package org.mongodb.morphia.geo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

import java.util.ArrayList;
import java.util.List;

import static org.mongodb.morphia.geo.Polygon.PolygonBoundary;

public class PolygonConverter extends TypeConverter implements SimpleValueConverter {
    private final PointListConverter pointListConverter = new PointListConverter();

    public PolygonConverter() {
        super(Polygon.class);
        pointListConverter.setMapper(getMapper());
    }

    @Override
    public Object encode(final Object value, final MappedField optionalExtraInfo) {
        Polygon polygon = (Polygon) value;
        List<PolygonBoundary> allBoundaries = polygon.getAllBoundaries();
        List encodedBoundaries = new ArrayList();
        for (PolygonBoundary boundary : allBoundaries) {
            encodedBoundaries.add(pointListConverter.encode(boundary, optionalExtraInfo));
        }
        return new BasicDBObject("type", GeoJsonType.POLYGON.getType())
               .append("coordinates", encodedBoundaries);
    }

    @Override
    @SuppressWarnings("unchecked") //always going to have unchecked warnings casting from the raw objects
    public Object decode(final Class<?> targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
        DBObject dbObject = (DBObject) fromDBObject;
        List<PolygonBoundary> boundaries = new ArrayList();
        List<List> coordinates = (List<List>) dbObject.get("coordinates");
        for (final List coordinate : coordinates) {
            Object listOfPoints = pointListConverter.decode(targetClass, coordinate, optionalExtraInfo);
            boundaries.add(new PolygonBoundary((List<Point>) listOfPoints));
        }
        return new Polygon(boundaries);
    }
}
