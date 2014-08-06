package org.mongodb.morphia.query;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.Arrays;

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
     * @return a Polygon instance representing an area bounded by the given points.
     * @throws java.lang.IllegalArgumentException if the start and end points are not the same
     */
    public static Polygon polygon(final Point... points) {
        if (points.length > 0 && !points[0].equals(points[points.length - 1])) {
            throw new IllegalArgumentException("A polygon requires the starting point to be the same as the end to ensure a closed area");
        }
        return new Polygon(points);
    }

    public static class Point {
        private final String type = "Point";
        private double[] coordinates;

        //needed for Morphia serialisation
        @SuppressWarnings("unused")
        private Point() {
        }

        Point(final double latitude, final double longitude) {
            this.coordinates = new double[]{longitude, latitude};
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

            if (!Arrays.equals(coordinates, point.coordinates)) {
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
            result = 31 * result + Arrays.hashCode(coordinates);
            return result;
        }

        @Override
        public String toString() {
            return "Point{"
                   + "type='" + type + '\''
                   + ", coordinates=" + Arrays.toString(coordinates)
                   + '}';
        }
    }

    public static class LineString {
        private final String type = "LineString";
        private final double[][] coordinates;

        LineString(final Point... points) {
            this.coordinates = new double[points.length][2];
            for (int i = 0; i < points.length; i++) {
                this.coordinates[i] = points[i].coordinates;
            }
        }
    }

    public static class Polygon {
        private final String type = "Polygon";
        private final double[][] coordinates;

        public Polygon(final Point... points) {
            this.coordinates = new double[points.length][2];
            for (int i = 0; i < points.length; i++) {
                this.coordinates[i] = points[i].coordinates;
            }
        }
    }
}
