package org.mongodb.morphia.geo;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for creating GeometryCollections.  Currently simply adds each Geometry to a List, but this would be the correct place to put
 * validation, or do any manipulation before creating the GeometryCollection.
 */
public class GeometryCollectionBuilder {
    private final List<Geometry> geometries = new ArrayList<Geometry>();

    /**
     * Record a Geometry that will need to be added to the final GeometryCollection.
     *
     * @param geometry the Geometry to add to the collection
     * @return this GeometryCollectionBuilder
     */
    public GeometryCollectionBuilder add(final Geometry geometry) {
        geometries.add(geometry);
        return this;
    }

    /**
     * Create an instance of GeometryCollection
     *
     * @return a GeometryCollection with all the details initialised in this builder.
     */
    public GeometryCollection build() {
        return new GeometryCollection(geometries);
    }
}
