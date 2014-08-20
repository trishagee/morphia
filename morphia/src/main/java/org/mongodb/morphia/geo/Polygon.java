package org.mongodb.morphia.geo;

import org.mongodb.morphia.annotations.Converters;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents either a simple polygon enclosing an area, or a more complex polygon that contains both an exterior boundary and
 * interior boundaries (holes) within it.  It will be persisted into the database according to <a
 * href="http://geojson.org/geojson-spec.html#id4">the specification</a>. Therefore this entity will never have its own ID or store the its
 * Class name.
 * <p/>
 * The factory for creating a Polygon is {@code PolygonBuilder}, which is accessible via the {@code GeoJson.polygonBuilder} method.
 * Alternatively, simple polygons without inner rings can be created via the {@code GeoJson.polygon} factory method.
 *
 * @see org.mongodb.morphia.geo.GeoJson#polygonBuilder(Point...)
 * @see org.mongodb.morphia.geo.GeoJson#polygon(Point...)
 */
@Embedded
@Entity(noClassnameStored = true)
@Converters({PolygonConverter.class})
@SuppressWarnings("unchecked") //always going to have unchecked casts when decoding
public class Polygon implements Geometry {
    private PolygonBoundary exteriorBoundary;
    private final List<PolygonBoundary> interiorBoundaries;

    @SuppressWarnings("UnusedDeclaration") // used by Morphia
    private Polygon() {
        interiorBoundaries = new ArrayList<PolygonBoundary>();
    }

    Polygon(final PolygonBoundary exteriorBoundary, final List<PolygonBoundary> interiorBoundaries) {
        this.exteriorBoundary = exteriorBoundary;
        this.interiorBoundaries = interiorBoundaries;
    }

    //    Polygon(final List<List<List<Double>>> coordinates) {
    //        this.coordinates = coordinates;
    //    }

    public Polygon(final List<PolygonBoundary> points) {
        exteriorBoundary = points.get(0);
        if (points.size() > 1) {
            interiorBoundaries = points.subList(1, points.size());
        } else {
            interiorBoundaries = new ArrayList<PolygonBoundary>();
        }
    }

    /*
     * Not for public consumption, used by package methods for creating more complex types that contain Polygons.
     */
    List<List<List<Double>>> getCoordinates() {
        List<List<List<Double>>> list = new ArrayList<List<List<Double>>>();

        list.add(exteriorBoundary.getCoordinates());
        for (final PolygonBoundary interiorBoundary : interiorBoundaries) {
            list.add(interiorBoundary.getCoordinates());
        }
        return list;
    }

    public PolygonBoundary getExteriorBoundary() {
        return exteriorBoundary;
    }

    public List<PolygonBoundary> getInteriorBoundaries() {
        //TODO this should be immutable or a copy
        return interiorBoundaries;
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

        Polygon polygon = (Polygon) o;

        if (exteriorBoundary != null ? !exteriorBoundary.equals(polygon.exteriorBoundary) : polygon.exteriorBoundary != null) {
            return false;
        }
        if (interiorBoundaries != null ? !interiorBoundaries.equals(polygon.interiorBoundaries) : polygon.interiorBoundaries != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = exteriorBoundary != null ? exteriorBoundary.hashCode() : 0;
        result = 31 * result + (interiorBoundaries != null ? interiorBoundaries.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Polygon{"
               + "exteriorBoundary=" + exteriorBoundary
               + ", interiorBoundaries=" + interiorBoundaries
               + '}';
    }

    public static class PolygonBoundary {
        private final List<Point> points;

        public PolygonBoundary(final Point... points) {
            this.points = Arrays.asList(points);
        }

        public PolygonBoundary(final List<Point> points) {
            this.points = points;
        }

        public List<Point> getPoints() {
            return points;
        }

        List<List<Double>> getCoordinates() {
            //TODO: this method needs removing once all the converters are done
            List<List<Double>> list = new ArrayList<List<Double>>();
            for (final Point coordinate : points) {
                list.add(coordinate.getCoordinates());
            }
            return list;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            PolygonBoundary that = (PolygonBoundary) o;

            if (!points.equals(that.points)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            return points.hashCode();
        }

        @Override
        public String toString() {
            return "PolygonBoundary{"
                   + "points=" + points
                   + '}';
        }
    }
}
