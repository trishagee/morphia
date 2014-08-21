package org.mongodb.morphia.geo;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;

import java.util.Arrays;
import java.util.List;

/**
 * This class represents a set of polygons, which will saved into MongoDB as per the 
 * <a href="http://geojson.org/geojson-spec.html#id7">GeoJSON specification</a>. Therefore this entity will never have its own ID or store 
 * the its Class name.
 * <p/>
 * The factory for creating a MultiPolygon is the {@code GeoJson.multiPolygon} method.
 * 
 * @see org.mongodb.morphia.geo.GeoJson#multiPolygon(Polygon...) 
 */
@Embedded
@Entity(noClassnameStored = true)
public class MultiPolygon implements Geometry {
    private List<Polygon> coordinates;

    @SuppressWarnings("UnusedDeclaration") // used by Morphia
    private MultiPolygon() {
    }

    MultiPolygon(final Polygon... polygons) {
        coordinates = Arrays.asList(polygons);
    }

    MultiPolygon(final List<Polygon> polygons) {
        coordinates = polygons;
    }

    public List<Polygon> getPolygons() {
        return coordinates;
    }
    public List<Polygon> getCoordinates() {
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

        MultiPolygon that = (MultiPolygon) o;

        if (!coordinates.equals(that.coordinates)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return coordinates.hashCode();
    }

    @Override
    public String toString() {
        return "MultiPolygon{"
               + "coordinates=" + coordinates
               + '}';
    }
}
