package org.mongodb.morphia.geo;

import com.mongodb.DBObject;
import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

import java.util.List;

public class GeoJsonTypeConverter extends TypeConverter implements SimpleValueConverter {
    public GeoJsonTypeConverter() {
        super(GeoJsonType.class);
    }

    @Override
    public Object decode(final Class<?> targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
        DBObject dbObject = (DBObject) fromDBObject;
        String type = (String) dbObject.get("type");
        if (type.equals("Point")) {
            return decodePoint(dbObject);
        }
        return null;
    }

    private Object decodePoint(final DBObject dbObject) {
        List coordinates = (List) dbObject.get("coordinates");
        return GeoJson.point((Double) coordinates.get(1), (Double) coordinates.get(0));
    }
}
