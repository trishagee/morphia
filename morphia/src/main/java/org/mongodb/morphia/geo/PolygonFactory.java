package org.mongodb.morphia.geo;

import java.util.List;

class PolygonFactory implements GeometryFactory<LineString> {
    @Override
    public Geometry createGeometry(final List<LineString> boundaries) {
        return new Polygon(boundaries);
    }
}
