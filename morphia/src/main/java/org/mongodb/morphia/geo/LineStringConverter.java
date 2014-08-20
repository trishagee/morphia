package org.mongodb.morphia.geo;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

import java.util.ArrayList;
import java.util.List;

public class LineStringConverter extends TypeConverter implements SimpleValueConverter {
    public LineStringConverter() {
        super(LineString.class);
    }

    @Override
    public Object encode(final Object value, final MappedField optionalExtraInfo) {
        LineString lineString = (LineString) value;
        BasicDBObject dbObject = new BasicDBObject("type", GeoJsonType.LINE_STRING.getType());
        BasicDBList dbList = new BasicDBList();
        for (final Point point : lineString.getPoints()) {
            dbList.add(PointConverter.getEncodablePointCoordinates(point));
        }
        dbObject.append("coordinates", dbList);
        return dbObject;
    }

    @Override
    @SuppressWarnings("unchecked") //always going to have unchecked casts when decoding
    public Object decode(final Class<?> targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
        DBObject dbObject = (DBObject) fromDBObject;
        List coordinates = (List) dbObject.get("coordinates");
        List<Point> points = new ArrayList<Point>();
        for (final Object coordinate : coordinates) {
            points.add(new Point((List) coordinate));
        }
        return new LineString(points);
    }
}
