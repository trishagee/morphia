package org.mongodb.morphia.geo;

import org.mongodb.morphia.annotations.Converters;
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
 * The factory for creating a Polygon is {@code PolygonBuilder}, which is accessible via the {@code GeoJson.polygonBuilder} method.
 * Alternatively, simple polygons without inner rings can be created via the {@code GeoJson.polygon} factory method.
 *
 * @see org.mongodb.morphia.geo.GeoJson#polygonBuilder(Point...)
 * @see org.mongodb.morphia.geo.GeoJson#polygon(Point...)
 */
@Embedded
@Entity(noClassnameStored = true)
@Converters(PolygonConverter.class)
@SuppressWarnings("unchecked") //always going to have unchecked casts when decoding
public class Polygon implements Geometry {
    private List<List<Point>> coordinates;

    @SuppressWarnings("UnusedDeclaration") // used by Morphia
    Polygon() {
        this.coordinates = new ArrayList<List<Point>>();
    }

    Polygon(final LineString exteriorBoundary, final List<LineString> interiorBoundaries) {
        this();
        this.coordinates.add(exteriorBoundary.getPoints());
        for (final LineString interiorBoundary : interiorBoundaries) {
            this.coordinates.add(interiorBoundary.getPoints());
        }
    }

//    Polygon(final List<List<List<Double>>> coordinates) {
//        this.coordinates = coordinates;
//    }

    public Polygon(final List<List<Point>> points) {
        this.coordinates = points;
    }

    /*
     * Not for public consumption, used by package methods for creating more complex types that contain Polygons.
     */
    List<List<List<Double>>> getCoordinates() {
        List<List<List<Double>>> list = new ArrayList<List<List<Double>>>();
        for (List<Point> coordinate : coordinates) {
            ArrayList arrayList = new ArrayList();
            list.add(arrayList);
            for (Point point : coordinate) {
                arrayList.add(point.getCoordinates());
            }
        }
        return list;
    }

    List<List<Point>> getPoints() {
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

        Polygon polygon = (Polygon) o;

        if (!coordinates.equals(polygon.coordinates)) {
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
        return "Polygon{"
               + "coordinates=" + coordinates
               + '}';
    }
}
