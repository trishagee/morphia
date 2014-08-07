package org.mongodb.morphia.geo;

import java.util.ArrayList;
import java.util.List;

public class LineString {
    private final String type = "LineString";
    private final List<List<Double>> coordinates = new ArrayList<List<Double>>();

    @SuppressWarnings("UnusedDeclaration") // used by Morphia
    LineString() {
    }

    LineString(final Point... points) {
        for (final Point point : points) {
            this.coordinates.add(point.getCoordinates());
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

    List<List<Double>> getCoordinates() {
        return coordinates;
    }
}
