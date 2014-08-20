package org.mongodb.morphia.geo;

import java.util.List;

public interface PointCollection {
    List<Point> getPoints();
    
    PointCollection createCollection(final List<Point> points1);

    List<List<Double>> getCoordinates();
}
