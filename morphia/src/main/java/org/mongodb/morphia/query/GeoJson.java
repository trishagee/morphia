package org.mongodb.morphia.query;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.ArrayList;
import java.util.List;

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
     * Create a new Polygon representing a <a href="http://docs.mongodb.org/manual/apps/geospatial-indexes/#geojson-objects">GeoJSON</a>
     * Polygon type.  Supported by server versions 2.4 an above.
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
     * This builder allows you to define the properties of the GeoJSON Polygon to create.
     */
    public static class PolygonBuilder {
        private final List<LineString> interiorRings = new ArrayList<LineString>();
        private final LineString exteriorBoundary;

        /**
         * This builder will create a new Polygon representing a 
         * <a href="http://docs.mongodb.org/manual/apps/geospatial-indexes/#geojson-objects">GeoJSON</a>
         * Polygon type.  Supported by server versions 2.4 and above.
         *
         * @param points an ordered series of Points that make up the external boundary of the polygon.  The first and last points should be
         *               the same to close the polygon
         * @throws java.lang.IllegalArgumentException if the start and end points are not the same
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
         * @throws java.lang.IllegalArgumentException if the start and end points are not the same
         */
        public PolygonBuilder interiorRing(final Point... points) {
            ensurePolygonIsClosed(points);
            interiorRings.add(new LineString(points));
            return this;
        }
        
        private static void ensurePolygonIsClosed(final Point[] points) {
            if (points.length > 0 && !points[0].equals(points[points.length - 1])) {
                throw new IllegalArgumentException("A polygon requires the starting point to be the same as the end to ensure a closed " 
                                                   + "area");
            }
        }

        /**
         * Return a GeoJSON Polygon with the specification from this builder.
         *
         * @return a Polygon instance representing an area (defined by the exterior boundary) and optionally containing holes (defined by 
         * the interior rings)
         */
        public Polygon build() {
            return new Polygon(exteriorBoundary, interiorRings);
        }
    }

    public static class Point {
        private final String type = "Point";
        private final List<Double> coordinates = new ArrayList<Double>(2);

        @SuppressWarnings("unused") //needed for Morphia serialisation
        private Point() {
        }

        Point(final double latitude, final double longitude) {
            this.coordinates.add(longitude);
            this.coordinates.add(latitude);
        }

        DBObject asDBObject() {
            return new BasicDBObject("type", type).append("coordinates", coordinates);
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Point point = (Point) o;

            if (!coordinates.equals(point.coordinates)) {
                return false;
            }
            if (!type.equals(point.type)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = type.hashCode();
            result = 31 * result + coordinates.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "Point{"
                   + "type='" + type + '\''
                   + ", coordinates=" + coordinates
                   + '}';
        }
    }

    public static class LineString {
        private final String type = "LineString";
        private final List<List<Double>> coordinates = new ArrayList<List<Double>>();

        @SuppressWarnings("UnusedDeclaration") // used by Morphia
        private LineString() {
        }

        LineString(final Point... points) {
            for (final Point point : points) {
                this.coordinates.add(point.coordinates);
            }
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            LineString that = (LineString) o;

            if (!coordinates.equals(that.coordinates)) {
                return false;
            }
            if (!type.equals(that.type)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = type.hashCode();
            result = 31 * result + coordinates.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "LineString{"
                   + "type='" + type + '\''
                   + ", coordinates=" + coordinates
                   + '}';
        }
    }

    /**
     * This class represents a more complex polygon that contains both an exterior boundary and zero or more interior boundaries (holes)
     * within it.  MultiRingPolygon relies on SimplePolygon to represent the outer and inner boundaries
     */
    public static class Polygon {
        private final String type = "Polygon";
        private final List<List<List<Double>>> coordinates = new ArrayList<List<List<Double>>>();

        @SuppressWarnings("UnusedDeclaration") // used by Morphia
        private Polygon() {
        }

        Polygon(final LineString exteriorBoundary, final List<LineString> interiorBoundaries) {
            this.coordinates.add(exteriorBoundary.coordinates);
            for (final LineString interiorBoundary : interiorBoundaries) {
                this.coordinates.add(interiorBoundary.coordinates);
            }
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Polygon that = (Polygon) o;

            if (!coordinates.equals(that.coordinates)) {
                return false;
            }
            if (!type.equals(that.type)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = type.hashCode();
            result = 31 * result + coordinates.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "MultiRingPolygon{"
                   + "type='" + type + '\''
                   + ", coordinates=" + coordinates
                   + '}';
        }
    }
}
