package org.mongodb.morphia.geo;

import java.util.ArrayList;
import java.util.List;

public class GeometryCollectionBuilder {
    private List<GeoJsonType> geometries = new ArrayList<GeoJsonType>();

    public GeometryCollectionBuilder add(final GeoJsonType geometry) {
        geometries.add(geometry);
        return this;
    }

    public GeometryCollection build() {
        return new GeometryCollection(geometries);
    }
}
