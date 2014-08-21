package org.mongodb.morphia.geo;

import java.util.List;

class LineStringFactory implements GeometryFactory<Point> {
    @Override
    public Geometry createGeometry(final List<Point> objects) {
        return new LineString(objects);
    }
}
