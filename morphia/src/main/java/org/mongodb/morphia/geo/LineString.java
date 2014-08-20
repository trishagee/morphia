package org.mongodb.morphia.geo;

import org.mongodb.morphia.annotations.Converters;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;

import java.util.ArrayList;
import java.util.Arrays;
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
@Converters(LineStringConverter.class)
public class LineString implements Geometry, PointCollection {
    private List<Point> coordinates;

    @SuppressWarnings("UnusedDeclaration") // used by Morphia
    private LineString() {
    }

    LineString(final Point... points) {
        this();
        this.coordinates = Arrays.asList(points);
    }

    LineString(final List<Point> points) {
        coordinates = points;
    }

    /*
     * Not for public consumption, used by package methods for creating more complex types that contain LinePoints.
     */
    @Override
    public List<List<Double>> getCoordinates() {
        //TODO: this method needs removing once all the converters are done
        List<List<Double>> list = new ArrayList<List<Double>>();
        for (final Point coordinate : coordinates) {
            list.add(coordinate.getCoordinates());
        }
        return list;
    }

    public List<Point> getPoints() {
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

        return true;
    }

    @Override
    public int hashCode() {
        return coordinates.hashCode();
    }

    @Override
    public String toString() {
        return "LineString{"
               + "coordinates=" + coordinates
               + '}';
    }
}
