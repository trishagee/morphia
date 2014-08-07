package org.mongodb.morphia.geo;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a series of points, which will saved into MongoDB as per the 
 * <a href="http://geojson.org/geojson-spec.html#id5">GeoJSON specification</a>
 */
public class MultiPoint {
    private final String type = "MultiPoint";
    private final List<List<Double>> coordinates = new ArrayList<List<Double>>();

    @SuppressWarnings("UnusedDeclaration") // used by Morphia
    private MultiPoint() {
    }

    MultiPoint(final Point... points) {
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

        MultiPoint that = (MultiPoint) o;

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
        return "MultiPoint{"
               + "type='" + type + '\''
               + ", coordinates=" + coordinates
               + '}';
    }
}
