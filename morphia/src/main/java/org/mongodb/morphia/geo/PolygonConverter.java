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

    public PolygonConverter() {
        super(Polygon.class);
    }

    @Override
    public Object encode(final Object value, final MappedField optionalExtraInfo) {
        Polygon polygon = (Polygon) value;
        BasicDBObject dbObject = new BasicDBObject("type", GeoJsonType.POLYGON.getType())
                                 .append("coordinates", getMapper().getConverters().encode(polygon.getAllBoundaries()));
        return dbObject;
    }

    @Override
    @SuppressWarnings("unchecked") //always going to have unchecked warnings casting from the raw objects
    public Object decode(final Class<?> targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
        DBObject dbObject = (DBObject) fromDBObject;
        List<PolygonBoundary> boundaries = new ArrayList();
        List<List> coordinates = (List<List>) dbObject.get("coordinates");
        for (final List coordinate : coordinates) {
            boundaries.add((PolygonBoundary) getMapper().getConverters().decode(PolygonBoundary.class, coordinate, optionalExtraInfo));
        }
        return new Polygon(boundaries);
    }
}
