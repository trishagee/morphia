package org.mongodb.morphia.geo;

import java.util.ArrayList;
import java.util.List;

public class GeometryCollectionBuilder {
    private List<Geometry> geometries = new ArrayList<Geometry>();

    public GeometryCollectionBuilder add(final Geometry geometry) {
        geometries.add(geometry);
        return this;
    }

    public GeometryCollection build() {
        return new GeometryCollection(geometries);
    }
}
