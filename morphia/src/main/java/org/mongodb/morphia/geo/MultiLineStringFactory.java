package org.mongodb.morphia.geo;

import java.util.List;

public class MultiLineStringFactory implements GeometryFactory<LineString> {
    @Override
    public Geometry createGeometry(final List<LineString> lineStrings) {
        return new MultiLineString(lineStrings);
    }
}
