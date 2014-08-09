package org.mongodb.morphia.geo;

import com.mongodb.DBObject;
import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

import java.util.List;

public class GeometryConverter extends TypeConverter implements SimpleValueConverter {
    public GeometryConverter() {
        super(Geometry.class);
    }

    @Override
    @SuppressWarnings("unchecked") // always going to have to cast getting raw objects from the database
    public Object decode(final Class<?> targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
        DBObject dbObject = (DBObject) fromDBObject;
        String type = (String) dbObject.get("type");
        if (type.equals("Point")) {
            List<Double> coordinates = (List<Double>) dbObject.get("coordinates");
            return new Point(coordinates);
        } else if (type.equals("LineString")) {
            List<List<Double>> coordinates = (List<List<Double>>) dbObject.get("coordinates");
            return new LineString(coordinates);
        }
        return null;
    }
}
