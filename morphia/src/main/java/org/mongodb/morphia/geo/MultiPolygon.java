package org.mongodb.morphia.geo;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a set of polygons, which will saved into MongoDB as per the 
 * <a href="http://geojson.org/geojson-spec.html#id7">GeoJSON specification</a>
 */
public class MultiPolygon {
    private final String type = "MultiPolygon";
    // Sigh. But that's the representation that converts easiest to the MongoDB shape
    private final List<List<List<List<Double>>>> coordinates = new ArrayList<List<List<List<Double>>>>();

    @SuppressWarnings("UnusedDeclaration") // used by Morphia
    private MultiPolygon() {
    }

    MultiPolygon(final Polygon... polygons) {
        for (final Polygon polygon : polygons) {
            coordinates.add(polygon.getCoordinates());
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

        MultiPolygon that = (MultiPolygon) o;

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
        return "MultiPolygon{"
               + "type='" + type + '\''
               + ", coordinates=" + coordinates
               + '}';
    }
}
