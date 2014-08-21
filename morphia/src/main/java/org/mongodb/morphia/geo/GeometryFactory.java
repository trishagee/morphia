package org.mongodb.morphia.geo;

import java.util.List;

interface GeometryFactory<T> {
    Geometry createGeometry(List<T> geometries);
}
