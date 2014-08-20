package org.mongodb.morphia.geo;

import com.mongodb.DBObject;
import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

import java.util.List;

public class PolygonConverter extends TypeConverter implements SimpleValueConverter {
    private final PointListConverter pointListConverter = new PointListConverter();
    private final IteratingConverter iteratingConverter = new IteratingConverter(pointListConverter, 
                                                                                 GeoJsonType.POLYGON, 
                                                                                 LineString.class);

    public PolygonConverter() {
        super(Polygon.class);
        pointListConverter.setMapper(getMapper());
    }

    @Override
    public Object encode(final Object value, final MappedField optionalExtraInfo) {
        return iteratingConverter.encode(((Polygon) value).getAllBoundaries(), optionalExtraInfo);
    }

    @Override
    @SuppressWarnings("unchecked") //always going to have unchecked warnings casting from the raw objects
    public Object decode(final Class<?> targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
        DBObject dbObject = (DBObject) fromDBObject;
        return new Polygon(iteratingConverter.decode((List<List>) dbObject.get("coordinates"), optionalExtraInfo));
    }
}
