package org.mongodb.morphia.geo;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents a series of lines, which will saved into MongoDB as per the 
 * <a href="http://geojson.org/geojson-spec.html#id6">GeoJSON specification</a>. Therefore this entity will never have its own ID or 
 * store the its Class name.
 * <p/>
 * The factory for creating a MultiLineString is the {@code GeoJson.multiLineString} method.
 *
 * @see org.mongodb.morphia.geo.GeoJson#multiLineString(LineString...) 
 */
@Embedded
@Entity(noClassnameStored = true)
public class MultiLineString implements Geometry {
    private final List<LineString> coordinates;

    @SuppressWarnings("UnusedDeclaration") // needed for Morphia
    private MultiLineString() {
        this.coordinates = new ArrayList<LineString>();
    }

    MultiLineString(final LineString... lineStrings) {
        coordinates = Arrays.asList(lineStrings);
    }

    MultiLineString(final List<LineString> coordinates) {
        this.coordinates = coordinates;
    }

    public List<LineString> getLineStrings() {
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

        MultiLineString that = (MultiLineString) o;

        if (coordinates != null ? !coordinates.equals(that.coordinates) : that.coordinates != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return coordinates != null ? coordinates.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "MultiLineString{"
               + "coordinates=" + coordinates
               + '}';
    }
}
