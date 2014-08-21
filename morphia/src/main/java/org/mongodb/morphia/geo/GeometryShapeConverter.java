package org.mongodb.morphia.geo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Converter that understands most Geometry instances are effectively just lists of either other geometry objects or double coordinates.
 * Recursively encodes and decodes Geometry objects, but needs to be instantiated with a List of GeometryFactory instances that represented
 * the hierarchy of Geometries that make up the required Geometry object.
 * <p/>
 * Overridden by subclasses to define exact behaviour for specific Geometry concrete classes.
 */
public class GeometryShapeConverter extends TypeConverter implements SimpleValueConverter {
    private final GeoJsonType geoJsonType;
    private final List<GeometryFactory> factories;

    GeometryShapeConverter(final Class<? extends Geometry> geometryClass, final GeoJsonType geoJsonType,
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
        for (final Object object : value) {
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

    /**
     * Extends and therefore configures GeometryShapeConverter to provide the specific configuration for converting MultiPolygon objects to
     * and from <a href="http://geojson.org/geojson-spec.html#id7">MongoDB representations</a> of the GeoJson.
     */
    public static class MultiPolygonConverter extends GeometryShapeConverter {
        /**
         * Creates a new MultiPolygonConverter.
         */
        public MultiPolygonConverter() {
            super(MultiPolygon.class, GeoJsonType.MULTI_POLYGON, Arrays.<GeometryFactory>asList(new MultiPolygonFactory(),
                                                                                                new PolygonFactory(),
                                                                                                new LineStringFactory(),
                                                                                                new PointFactory()));
        }
    }

    public static class PolygonConverter extends GeometryShapeConverter {
        /**
         * Creates a new PolygonConverter.  This extends and therefore configures GeometryShapeConverter to provide the specific
         * configuration for converting Polygon objects to and from <a href="http://geojson.org/geojson-spec.html#id4">MongoDB
         * representations</a> of the GeoJson.
         */
        public PolygonConverter() {
            super(Polygon.class, GeoJsonType.POLYGON, Arrays.<GeometryFactory>asList(new PolygonFactory(),
                                                                                     new LineStringFactory(),
                                                                                     new PointFactory()));
        }
    }

    public static class MultiLineStringConverter extends GeometryShapeConverter {
        /**
         * Creates a new MultiLineStringConverter.  This extends and therefore configures GeometryShapeConverter to provide the specific
         * configuration for converting MultiLineString objects to and from <a href="http://geojson.org/geojson-spec.html#id6">MongoDB
         * representations</a> of the GeoJson.
         */
        public MultiLineStringConverter() {
            super(MultiLineString.class, GeoJsonType.MULTI_LINE_STRING, Arrays.<GeometryFactory>asList(new MultiLineStringFactory(),
                                                                                                       new LineStringFactory(),
                                                                                                       new PointFactory()));
        }
    }

    public static class MultiPointConverter extends GeometryShapeConverter {
        /**
         * Creates a new MultiPointConverter. This extends and therefore configures GeometryShapeConverter to provide the specific 
         * configuration for converting MultiPoint objects
         * to and from <a href="http://geojson.org/geojson-spec.html#id5">MongoDB representations</a> of the GeoJson.
         */
        public MultiPointConverter() {
            super(MultiPoint.class, GeoJsonType.MULTI_POINT, Arrays.<GeometryFactory>asList(new MultiPointFactory(), new PointFactory()));
        }
    }

    public static class LineStringConverter extends GeometryShapeConverter {
        /**
         * Creates a new LineStringConverter. This extends and therefore configures GeometryShapeConverter to provide the specific 
         * configuration for converting LineString objects
         * to and from <a href="http://geojson.org/geojson-spec.html#id3">MongoDB representations</a> of the GeoJson.
         */
        public LineStringConverter() {
            super(LineString.class, GeoJsonType.LINE_STRING, Arrays.<GeometryFactory>asList(new LineStringFactory(), new PointFactory()));
        }
    }

    public static class PointConverter extends GeometryShapeConverter {
        /**
         * Creates a new PointConverter. This extends and therefore configures GeometryShapeConverter to provide the specific 
         * configuration for converting Point objects
         * to and from <a href="http://geojson.org/geojson-spec.html#id2">MongoDB representations</a> of the GeoJson.
         */
        public PointConverter() {
            super(Point.class, GeoJsonType.POINT, Arrays.<GeometryFactory>asList(new PointFactory()));
        }
    }
}
