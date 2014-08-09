package org.mongodb.morphia.geo;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a GeoJSON LineString type.  Will be persisted into the database according to 
 * <a href="http://geojson.org/geojson-spec.html#id3">the specification</a>. Therefore this entity will never have its own ID or store 
 * the its Class name.
 * <p/>
 * The factory for creating a LineString is the {@code GeoJson.lineString} method.
 *
 * @see org.mongodb.morphia.geo.GeoJson#lineString(Point...) 
 */
@Embedded
@Entity(noClassnameStored = true)
public class LineString implements Geometry {
    private final String type = GeoJsonType.LINE_STRING.getType();
    private final List<List<Double>> coordinates;

    @SuppressWarnings("UnusedDeclaration") // used by Morphia
    private LineString() {
        coordinates = new ArrayList<List<Double>>();
    }

    LineString(final Point... points) {
        this();
        for (final Point point : points) {
            this.coordinates.add(point.getCoordinates());
        }
    }

    LineString(final List<List<Double>> coordinates) {
        this.coordinates = coordinates;
    }

    /*
     * Not for public consumption, used by package methods for creating more complex types that contain LinePoints.
     */
    List<List<Double>> getCoordinates() {
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
