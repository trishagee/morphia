package org.mongodb.morphia.geo;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a series of lines, which will saved into MongoDB as per the 
 * <a href="http://geojson.org/geojson-spec.html#id6">GeoJSON specification</a>
 */
public class MultiLineString {
    private final String type = "MultiLineString";
    private final List<List<List<Double>>> coordinates = new ArrayList<List<List<Double>>>();

    @SuppressWarnings("UnusedDeclaration") // needed for Morphia
    private MultiLineString() {
    }

    MultiLineString(final LineString... lineStrings) {
        for (final LineString lineString : lineStrings) {
            this.coordinates.add(lineString.getCoordinates());
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

        MultiLineString that = (MultiLineString) o;

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
        return "MultiLineString{"
               + "type='" + type + '\''
               + ", coordinates=" + coordinates
               + '}';
    }
}
