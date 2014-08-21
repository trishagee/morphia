package org.mongodb.morphia.geo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeoConverter extends TypeConverter implements SimpleValueConverter {
    private final GeoJsonType geoJsonType;
    private final List<GeometryFactory> factories;

    public GeoConverter(final Class<? extends Geometry> geometryClass, final GeoJsonType geoJsonType,
                        final List<GeometryFactory> factories) {
        super(geometryClass);
        this.geoJsonType = geoJsonType;
        this.factories = factories;
    }

    @Override
    public Object encode(final Object value, final MappedField optionalExtraInfo) {
        Object encodedObjects = encodeObjects(((Geometry) value).getCoordinates());
        return new BasicDBObject("type", geoJsonType.getType())
               .append("coordinates", encodedObjects);
    }

    private Object encodeObjects(final List value) {
        List<Object> encodedObjects = new ArrayList<Object>();
        for (final Object object : (List) value) {
            if (object instanceof Geometry) {
                //iterate through the list of geometry objects recursively until you find the lowest-level
                encodedObjects.add(encodeObjects(((Geometry) object).getCoordinates()));
            } else {
                encodedObjects.add(getMapper().getConverters().encode(object));
            }
        }
        return encodedObjects;
    }

    @Override
    public Object decode(final Class<?> targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
        return decodeObject(((DBObject) fromDBObject).get("coordinates"), factories);
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

    public static class MultiPolygonConverter extends GeoConverter {
        public MultiPolygonConverter() {
            super(MultiPolygon.class, GeoJsonType.MULTI_POLYGON, Arrays.<GeometryFactory>asList(new MultiPolygonFactory(),
                                                                                                new PolygonFactory(),
                                                                                                new LineStringFactory(),
                                                                                                new PointFactory()));
        }
    }

    public static class PolygonConverter extends GeoConverter {
        public PolygonConverter() {
            super(Polygon.class, GeoJsonType.POLYGON, Arrays.<GeometryFactory>asList(new PolygonFactory(),
                                                                                     new LineStringFactory(),
                                                                                     new PointFactory()));
        }
    }

    public static class MultiLineStringConverter extends GeoConverter {
        public MultiLineStringConverter() {
            super(MultiLineString.class, GeoJsonType.MULTI_LINE_STRING, Arrays.<GeometryFactory>asList(new MultiLineStringFactory(),
                                                                                                       new LineStringFactory(),
                                                                                                       new PointFactory()));
        }
    }

    public static class MultiPointConverter extends GeoConverter {
        public MultiPointConverter() {
            super(MultiPoint.class, GeoJsonType.MULTI_POINT, Arrays.<GeometryFactory>asList(new MultiPointFactory(), new PointFactory()));
        }
    }

    public static class LineStringConverter extends GeoConverter {
        public LineStringConverter() {
            super(LineString.class, GeoJsonType.LINE_STRING, Arrays.<GeometryFactory>asList(new LineStringFactory(), new PointFactory()));
        }
    }

    /**
     * Converts the Point class from and to MongoDB-shaped DBObjects. This means the Point class can be a prettier, 
     * more usable object since all the serialisation logic is in the converter.
     */
    public static class PointConverter extends GeoConverter {
        /**
         * Create a new converter.  Registers itself to convert Point classes.
         */
        public PointConverter() {
            super(Point.class, GeoJsonType.POINT, Arrays.<GeometryFactory>asList(new PointFactory()));
        }
    }
}
