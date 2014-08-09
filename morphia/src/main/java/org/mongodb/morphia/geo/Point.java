package org.mongodb.morphia.geo;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a GeoJSON Point type.  Will be persisted into the database according to <a href="http://geojson.org/geojson-spec.html#id2">the
 * specification</a>. Therefore because of this, this entity will never have its own ID or store the its Class name.
 * <p/>
 * The builder for creating a Point is the {@code GeoJson.pointBuilder} method, or the helper {@code GeoJson.point} factory method.
 *
 * @see org.mongodb.morphia.geo.GeoJson#point(double, double)
 * @see GeoJson#pointBuilder()
 */
@Embedded
@Entity(noClassnameStored = true)
public class Point implements Geometry {
    private final String type = GeoJsonType.POINT.getType();
    private final List<Double> coordinates;

    @SuppressWarnings("unused") //needed for Morphia serialisation
    private Point() {
        coordinates = new ArrayList<Double>(2);
    }

    Point(final double latitude, final double longitude) {
        this();
        this.coordinates.add(longitude);
        this.coordinates.add(latitude);
    }

    Point(final List<Double> coordinates) {
        this.coordinates = coordinates;
    }

    /*
     * Not for public consumption, used by package methods for creating more complex types that contain Points.
     */
    List<Double> getCoordinates() {
        return coordinates;
    }

    /* equals, hashCode and toString. Useful primarily for testing and debugging. Don't forget to re-create when changing this class */
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
