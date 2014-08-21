package org.mongodb.morphia.geo;

/**
 * Enumerates all the GeoJson types that are currently supported by Morphia.
 */
public enum GeoJsonType {
    POINT("Point", Point.class),
    LINE_STRING("LineString", LineString.class),
    POLYGON("Polygon", Polygon.class),
    MULTI_POINT("MultiPoint", MultiPoint.class),
    MULTI_LINE_STRING("MultiLineString", MultiLineString.class),
    MULTI_POLYGON("MultiPolygon", MultiPolygon.class);

    private final String type;
    private final Class<? extends Geometry> typeClass;

    GeoJsonType(final String type, final Class<? extends Geometry> typeClass) {
        this.type = type;
        this.typeClass = typeClass;
    }

    /**
     * Returns the value that needs to be stored with the GeoJson values in the database to declare which GeoJson type the coordinates
     * represent. See <a href="http://docs.mongodb.org/manual/applications/geospatial-indexes/#geojson-objects">the documentation</a> for a
     * list of the GeoJson objects supported by MongoDB.
     *
     * @return a String of the GeoJson type.
     */
    public String getType() {
        return type;
    }

    /**
     * Returns a concrete class that implements Geometry, the class that represents this GeoJsonType.
     *
     * @return the Geometry class for this GeoJsonType
     */
    public Class<? extends Geometry> getTypeClass() {
        return typeClass;
    }

    /**
     * Allows you to turn String values of types into the Enum that corresponds to this type.
     *
     * @param type a String, one of the values from 
     *             <a href="http://docs.mongodb.org/manual/applications/geospatial-indexes/#geojson-objects">this list</a> of supported 
     *             types
     * @return the GeoJsonType that corresponds to this type String
     */
    public static GeoJsonType fromString(final String type) {
        if (type != null) {
            for (final GeoJsonType geoJsonType : values()) {
                if (type.equalsIgnoreCase(geoJsonType.getType())) {
                    return geoJsonType;
                }
            }
        }
        throw new IllegalArgumentException(String.format("Cannot decode type into GeoJsonType. Type= '%s'", type));
    }
}
