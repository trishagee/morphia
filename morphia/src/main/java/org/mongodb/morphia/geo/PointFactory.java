package org.mongodb.morphia.geo;

import java.util.List;

class PointFactory implements GeometryFactory<Double> {
    @Override
    public Geometry createGeometry(final List<Double> coordinates) {
        return new Point(coordinates);
    }
}
