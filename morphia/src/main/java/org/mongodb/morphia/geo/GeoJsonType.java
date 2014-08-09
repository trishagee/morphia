package org.mongodb.morphia.geo;

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

    public String getType() {
        return type;
    }
}
