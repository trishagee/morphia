package org.mongodb.morphia.geo;

import java.util.List;

public class MultiPointFactory implements GeometryFactory<Point> {
    @Override
    public Geometry createGeometry(final List<Point> points) {
        return new MultiPoint(points);
    }
}
