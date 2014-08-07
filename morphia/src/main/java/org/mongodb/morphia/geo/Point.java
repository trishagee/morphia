package org.mongodb.morphia.geo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.ArrayList;
import java.util.List;

public class Point {
    private final String type = "Point";
    private final List<Double> coordinates = new ArrayList<Double>(2);

    @SuppressWarnings("unused") //needed for Morphia serialisation
    private Point() {
    }

    Point(final double latitude, final double longitude) {
        this.coordinates.add(longitude);
        this.coordinates.add(latitude);
    }

    //TODO: this needs to not be public
    public DBObject asDBObject() {
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

    /**
     * Returns the two co-ordinates for this point as a List with two Double values
     * @return a List with two Double values, the first is longitude the second latitude
     */
    List<Double> getCoordinates() {
        return coordinates;
    }
}
