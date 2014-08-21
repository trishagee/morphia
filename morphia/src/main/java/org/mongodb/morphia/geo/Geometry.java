package org.mongodb.morphia.geo;

import org.mongodb.morphia.annotations.Converters;

import java.util.List;

/**
 * Marker interface to denote which entities are classes that will serialise into a MongoDB GeoJson object. As this is an interface, not a
 * concrete class, and as the implementations do not store the class names, this requires a TypeConverter to define how to decode each
 * implementation from the database.
 */
@Converters(GeometryConverter.class)
public interface Geometry {
    List<?> getCoordinates();
}
