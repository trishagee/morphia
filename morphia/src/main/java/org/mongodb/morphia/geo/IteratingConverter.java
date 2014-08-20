package org.mongodb.morphia.geo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.DefaultCreator;
import org.mongodb.morphia.mapping.MappedField;

import java.util.ArrayList;
import java.util.List;

public class IteratingConverter {
    private final TypeConverter converter;
    private final GeoJsonType geoJsonType;
    private final Class<?> targetClass;

    public IteratingConverter(final TypeConverter converter, final GeoJsonType geoJsonType,
                              final Class<?> targetClass) {
        this.converter = converter;
        this.geoJsonType = geoJsonType;
        this.targetClass = targetClass;
    }

    public DBObject encode(final List value, final MappedField optionalExtraInfo) {
        List<Object> encodedObjects = new ArrayList<Object>();
        for (final Object object : value) {
            encodedObjects.add(converter.encode(object, optionalExtraInfo));
        }
        return new BasicDBObject("type", geoJsonType.getType())
               .append("coordinates", encodedObjects);
    }

    @SuppressWarnings("unchecked") // always have unchecked casts when dealing with raw classes
    public List<PointCollection> decode(final List<List> coordinates, final MappedField optionalExtraInfo) {
        List<PointCollection> pointCollections = new ArrayList();
        for (final List coordinate : coordinates) {
            Object listOfPoints = converter.decode(targetClass, coordinate, optionalExtraInfo);
            pointCollections.add(((PointCollection) DefaultCreator.createInst(targetClass)).createCollection((List<Point>) listOfPoints));
        }
        return pointCollections;
    }
}
