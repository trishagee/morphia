package org.mongodb.morphia.geo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

import java.util.List;

public class LineStringConverter extends TypeConverter implements SimpleValueConverter {
    private final PointListConverter pointListConverter = new PointListConverter();

    public LineStringConverter() {
        super(LineString.class);
        pointListConverter.setMapper(getMapper());
    }

    @Override
    public Object encode(final Object value, final MappedField optionalExtraInfo) {
        List coordinates = (List) pointListConverter.encode(value, optionalExtraInfo);
        return new BasicDBObject("type", GeoJsonType.LINE_STRING.getType())
                                 .append("coordinates", coordinates);
    }

    @Override
    @SuppressWarnings("unchecked") //always going to have unchecked casts when decoding
    public Object decode(final Class<?> targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
        DBObject dbObject = (DBObject) fromDBObject;
        List coordinates = (List) dbObject.get("coordinates");
        return new LineString((List<Point>) pointListConverter.decode(targetClass, coordinates, optionalExtraInfo));
    }
}
