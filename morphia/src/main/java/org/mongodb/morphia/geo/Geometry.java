package org.mongodb.morphia.geo;

import org.mongodb.morphia.annotations.Converters;
import org.mongodb.morphia.annotations.Embedded;

/**
 * Marker interface to denote which entities are classes that will serialise into a MongoDB GeoJson object. 
 */
@Embedded
@Converters(GeometryConverter.class)
public interface Geometry {
}
