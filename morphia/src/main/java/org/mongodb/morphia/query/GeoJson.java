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
}
