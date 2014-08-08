package org.mongodb.morphia.geo;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a GeoJSON Point type.  Will be persisted into the database according to <a href="http://geojson.org/geojson-spec.html#id2">the
 * specification</a>. Therefore because of this, this entity will never have its own ID or store the its Class name.
 * <p/>
 * The factory for creating a Point is the {@code GeoJson.point} method.
 *
 * @see org.mongodb.morphia.geo.GeoJson
 */
@Embedded
@Entity(noClassnameStored = true)
public class Point implements GeoJsonType {
    private final String type = "Point";
    private final List<Double> coordinates = new ArrayList<Double>(2);

    @SuppressWarnings("unused") //needed for Morphia serialisation
    private Point() {
    }

    Point(final double latitude, final double longitude) {
        this.coordinates.add(longitude);
        this.coordinates.add(latitude);
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

    /**
     * Returns the two co-ordinates for this point as a List with two Double values
     *
     * @return a List with two Double values, the first is longitude the second latitude
     */
    List<Double> getCoordinates() {
        return coordinates;
    }

    String getType() {
        return type;
    }
}
