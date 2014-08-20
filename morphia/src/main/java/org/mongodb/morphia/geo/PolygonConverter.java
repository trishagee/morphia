package org.mongodb.morphia.geo;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked") //TODO this needs fixing
public class PolygonConverter extends TypeConverter implements SimpleValueConverter {

    public PolygonConverter() {
        super(Polygon.class);
    }

    @Override
    public Object encode(final Object value, final MappedField optionalExtraInfo) {
        Polygon polygon = (Polygon) value;
        BasicDBObject dbObject = new BasicDBObject("type", GeoJsonType.POLYGON.getType());
        BasicDBList dbList = new BasicDBList();
        for (final List<Point> points : polygon.getPoints()) {
            ArrayList polygonList = new ArrayList();
            dbList.add(polygonList);
            for (Point point : points) {
                polygonList.add(PointConverter.getEncodablePointCoordinates(point));
            }
        }
        dbObject.append("coordinates", dbList);
        return dbObject;

    }

    @Override
    public Object decode(final Class<?> targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
        DBObject dbObject = (DBObject) fromDBObject;
        List<List> coordinates = (List<List>) dbObject.get("coordinates");
        List<List<Point>> points = new ArrayList<List<Point>>();
        for (List coordinate : coordinates) {
            List polygon = new ArrayList();
            points.add(polygon);
            for (Object o : coordinate) {
                polygon.add(new Point((List) o));
            }
        }
        return new Polygon(points);
    }
}
