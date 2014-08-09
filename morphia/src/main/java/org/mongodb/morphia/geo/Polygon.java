package org.mongodb.morphia.geo;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents either a simple polygon enclosing an area, or a more complex polygon that contains both an exterior boundary and
 * interior boundaries (holes) within it.  It will be persisted into the database according to <a
 * href="http://geojson.org/geojson-spec.html#id4">the specification</a>. Therefore this entity will never have its own ID or store the its
 * Class name.
 * <p/>
 * The factory for creating a Polygon is {@code PolygonBuilder}, which is accessible via the {@code GeoJson.polygon} method.
 *
 * @see org.mongodb.morphia.geo.GeoJson
 * @see org.mongodb.morphia.geo.PolygonBuilder
 */
@Embedded
@Entity(noClassnameStored = true)
public class Polygon implements Geometry {
    private final String type = GeoJsonType.POLYGON.getType();
    private final List<List<List<Double>>> coordinates;

    @SuppressWarnings("UnusedDeclaration") // used by Morphia
    Polygon() {
        this.coordinates = new ArrayList<List<List<Double>>>();
    }

    Polygon(final LineString exteriorBoundary, final List<LineString> interiorBoundaries) {
        this();
        this.coordinates.add(exteriorBoundary.getCoordinates());
        for (final LineString interiorBoundary : interiorBoundaries) {
            this.coordinates.add(interiorBoundary.getCoordinates());
        }
    }

    Polygon(final List<List<List<Double>>> coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Polygon that = (Polygon) o;

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
        return "Polygon{"
               + "type='" + type + '\''
               + ", coordinates=" + coordinates
               + '}';
    }

    List<List<List<Double>>> getCoordinates() {
        return coordinates;
    }
}
