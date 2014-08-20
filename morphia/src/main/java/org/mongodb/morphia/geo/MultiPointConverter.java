package org.mongodb.morphia.geo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

import java.util.List;

public class MultiPointConverter extends TypeConverter implements SimpleValueConverter {
    private final PointListConverter pointListConverter = new PointListConverter();

    public MultiPointConverter() {
        super(MultiPoint.class);
        pointListConverter.setMapper(getMapper());
    }

    @Override
    public Object encode(final Object value, final MappedField optionalExtraInfo) {
        List coordinates = (List) pointListConverter.encode(value, optionalExtraInfo);
        return new BasicDBObject("type", GeoJsonType.MULTI_POINT.getType())
               .append("coordinates", coordinates);
    }

    @Override
    @SuppressWarnings("unchecked") //always going to have unchecked warnings casting from the raw objects
    public Object decode(final Class<?> targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
        DBObject dbObject = (DBObject) fromDBObject;
        List coordinates = (List) dbObject.get("coordinates");
        return new MultiPoint((List<Point>) pointListConverter.decode(targetClass, coordinates, optionalExtraInfo));
    }
}
