package org.mongodb.morphia;


import java.util.stream.IntStream;

/**
 * Defines how the output of the map reduce job is handled.
 */
public enum MapreduceType {
    REPLACE,
    MERGE,
    REDUCE,
    INLINE;

    /**
     * Finds the type represented by the value given
     *
     * @param value the value to look up
     * @return the type represented by the value given
     */
    public static MapreduceType fromString(final String value) {
        return IntStream.range(0, values().length)
                        .mapToObj(i -> values()[i])
                        .filter(fo -> fo.name()
                                        .equals(value))
                        .findFirst()
                        .orElse(null);
    }

}
