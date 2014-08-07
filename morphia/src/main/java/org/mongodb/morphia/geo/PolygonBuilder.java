package org.mongodb.morphia.geo;

import java.util.ArrayList;
import java.util.List;

/**
 * This builder allows you to define the properties of the GeoJSON Polygon to create.
 */
public class PolygonBuilder {
    private final List<LineString> interiorRings = new ArrayList<LineString>();
    private final LineString exteriorBoundary;

    /**
     * This builder will create a new Polygon representing a 
     * <a href="http://docs.mongodb.org/manual/apps/geospatial-indexes/#geojson-objects">GeoJSON</a>
     * Polygon type.  Supported by server versions 2.4 and above.
     *
     * @param points an ordered series of Points that make up the external boundary of the polygon.  The first and last points should be the
     *               same to close the polygon
     * @throws IllegalArgumentException if the start and end points are not the same
     */
    public PolygonBuilder(final Point... points) {
        ensurePolygonIsClosed(points);
        exteriorBoundary = new LineString(points);
    }

    /**
     * By adding a series of points here, you can define inner rings (i.e. holes) inside the polygon.
     *
     * @param points zero or more Points defining any interior rings (or holes) inside the boundary of the polygon
     * @return this PolygonBuilder
     * @throws IllegalArgumentException if the start and end points are not the same
     */
    public PolygonBuilder interiorRing(final Point... points) {
        ensurePolygonIsClosed(points);
        interiorRings.add(new LineString(points));
        return this;
    }

    /**
     * Return a GeoJSON Polygon with the specification from this builder.
     *
     * @return a Polygon instance representing an area (defined by the exterior boundary) and optionally containing holes (defined by the
     * interior rings)
     */
    public Polygon build() {
        return new Polygon(exteriorBoundary, interiorRings);
    }

    private static void ensurePolygonIsClosed(final Point[] points) {
        if (points.length > 0 && !points[0].equals(points[points.length - 1])) {
            throw new IllegalArgumentException("A polygon requires the starting point to be the same as the end to ensure a closed "
                                               + "area");
        }
    }
}
