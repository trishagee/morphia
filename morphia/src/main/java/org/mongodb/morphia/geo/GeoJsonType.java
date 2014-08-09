package org.mongodb.morphia.geo;

/**
 * Enumerates all the GeoJson types that are currently supported by Morphia.
 */
public enum GeoJsonType {
    POINT("Point"),
    LINE_STRING("LineString"),
    POLYGON("Polygon"),
    MULTI_POINT("MultiPoint"),
    MULTI_LINE_STRING("MultiLineString"),
    MULTI_POLYGON("MultiPolygon");

    private final String type;

    private GeoJsonType(final String type) {
        this.type = type;
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
}
