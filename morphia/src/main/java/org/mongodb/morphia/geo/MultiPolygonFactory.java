package org.mongodb.morphia.geo;

import java.util.List;

class MultiPolygonFactory implements GeometryFactory<Polygon> {
    @Override
    public Geometry createGeometry(final List<Polygon> polygons) {
        return new MultiPolygon(polygons);
    }
}
