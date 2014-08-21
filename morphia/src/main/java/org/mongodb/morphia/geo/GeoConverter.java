package org.mongodb.morphia.geo;

import com.mongodb.BasicDBObject;
import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

import java.util.ArrayList;
import java.util.List;

public class GeoConverter extends TypeConverter implements SimpleValueConverter {
    private final GeoJsonType geoJsonType;
    private final List<GeometryFactory> factories;

    public GeoConverter(final GeoJsonType geoJsonType,
                        final List<GeometryFactory> factories) {
        this.geoJsonType = geoJsonType;
        this.factories = factories;
    }

    @Override
    public Object encode(final Object value, final MappedField optionalExtraInfo) {
        List<Object> encodedObjects = encodeObjects((List) value);
        return new BasicDBObject("type", geoJsonType.getType())
               .append("coordinates", encodedObjects);
    }

    private List<Object> encodeObjects(final List value) {
        List<Object> encodedObjects = new ArrayList<Object>();
        for (final Object object : (List) value) {
            if (object instanceof Point) {
                encodedObjects.add(CoordinateConverter.encode((Point) object));
            } else {
                encodedObjects.add(encodeObjects(((Geometry) object).getCoordinates()));
            }
        }
        return encodedObjects;
    }

    @Override
    public Object decode(final Class<?> targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
        return decodeObject(fromDBObject, factories);
    }

    @SuppressWarnings("unchecked") // always have unchecked casts when dealing with raw classes
    private Object decodeObject(final Object fromDBObject, final List<GeometryFactory> geometryFactories) {
        if (!geometryFactories.isEmpty()) {
            // we're expecting a list that can be turned into a geometry using one of these factories
            GeometryFactory factory = geometryFactories.get(0);
            if (fromDBObject instanceof List) {
                List<Object> decodedObjects = new ArrayList<Object>();
                for (final Object objectThatNeedsDecoding : (List) fromDBObject) {
                    decodedObjects.add(decodeObject(objectThatNeedsDecoding, geometryFactories.subList(1, geometryFactories.size())));
                }
                return factory.createGeometry(decodedObjects);
            }
        }
        return getMapper().getConverters().encode(fromDBObject);
    }
    
    interface GeometryFactory<T> {
        Geometry createGeometry(List<T> geometries);
    }
}
