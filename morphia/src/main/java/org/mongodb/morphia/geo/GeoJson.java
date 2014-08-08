package org.mongodb.morphia.geo;

/**
 * Factory class for creating GeoJSON types.  See 
 * <a href="http://docs.mongodb.org/manual/applications/geospatial-indexes/#geojson-objects">the
 * documentation</a> for all the types.
 */
public final class GeoJson {
    private GeoJson() {
    }

    /**
     * Create a new Point representing a <a href="http://docs.mongodb.org/manual/apps/geospatial-indexes/#geojson-objects">GeoJSON</a> point
     * type.  Supported by server versions 2.4 and above.
     *
     * @param latitude  the point's latitude coordinate
     * @param longitude the point's longitude coordinate
     * @return a Point instance representing a single location point defined by the given latitude and longitude
     */
    public static Point point(final double latitude, final double longitude) {
        return new Point(latitude, longitude);
    }

    /**
     * Create a new LineString representing a <a href="http://docs.mongodb.org/manual/apps/geospatial-indexes/#geojson-objects">GeoJSON</a>
     * LineString type.  Supported by server versions 2.4 an above.
     *
     * @param points an ordered series of Points that make up the line
     * @return a LineString instance representing a series of ordered points that make up a line
     */
    public static LineString lineString(final Point... points) {
        return new LineString(points);
    }

    /**
     * Create a new PolygonBuilder that will let you create a Polygon representing a 
     * <a href="http://docs.mongodb.org/manual/apps/geospatial-indexes/#geojson-objects">GeoJSON</a>
     * Polygon type.  Supported by server versions 2.4 and above.
     *
     * @param points an ordered series of Points that make up the polygon.  The first and last points should be the same to close the
     *               polygon
     * @return a PolygonBuilder to be used to build up the required Polygon
     * @throws java.lang.IllegalArgumentException if the start and end points are not the same
     */
    public static PolygonBuilder polygon(final Point... points) {
        return new PolygonBuilder(points);
    }

    /**
     * Create a new MultiPoint representing a <a href="http://docs.mongodb.org/manual/apps/geospatial-indexes/#geojson-objects">GeoJSON</a>
     * MultiPoint type.  Supported by server versions 2.6 and above.
     *
     * @param points a set of points that make up the MultiPoint object
     * @return a MultiPoint object containing all the given points
     */
    public static MultiPoint multiPoint(final Point... points) {
        return new MultiPoint(points);
    }

    /**
     * Create a new MultiLineString representing a 
     * <a href="http://docs.mongodb.org/manual/apps/geospatial-indexes/#geojson-objects">GeoJSON</a>
     * MultiLineString type.  Supported by server versions 2.6 and above.
     *
     * @param lines a set of lines that make up the MultiLineString object
     * @return a MultiLineString object containing all the given lines
     */
    public static MultiLineString multiLineString(final LineString... lines) {
        return new MultiLineString(lines);
    }

    /**
     * Create a new MultiPolygon representing a 
     * <a href="http://docs.mongodb.org/manual/apps/geospatial-indexes/#geojson-objects">GeoJSON</a>
     * MultiPolygon type.  Supported by server versions 2.6 and above.
     *
     * @param polygons a series of polygons (which may contain inner rings)
     * @return a MultiPolygon object containing all the given polygons
     */
    public static MultiPolygon multiPolygon(final Polygon... polygons) {
        return new MultiPolygon(polygons);
    }

    /**
     * Return a GeometryCollectionBuilder that will let you create a GeometryCollection 
     * <a href="http://docs.mongodb.org/manual/apps/geospatial-indexes/#geojson-objects">GeoJSON</a>
     * GeometryCollection.  Supported by server version 2.6
     *
     * @return new GeometryCollectionBuilder that will allow you to build up a GeometryCollection
     */
    public static GeometryCollectionBuilder geometryCollection() {
        return new GeometryCollectionBuilder();
    }
}
