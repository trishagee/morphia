package org.mongodb.morphia.utils;


import org.bson.BSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


/**
 * Helper to allow for optimizations for different types of Map/Collections
 *
 * @param <K> The key type of the map
 * @param <V> The value type of the map/collection
 * @author Scott Hernandez
 */
public final class IterHelper<K, V> {
    /**
     * Process a Map
     *
     * @param map        the object to process
     * @param callback the callback
     */
    @SuppressWarnings("unchecked")
    public void loopMap(final Map<K, V> map, final MapIterCallback<K, V> callback) {
        if (map == null) {
            return;
        }
        for (final Entry<K, V> entry : map.entrySet()) {
            callback.eval(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Calls eval for each entry found, or just once if the "x" isn't iterable/collection/list/etc. with "x"
     *
     * @param x        the object process
     * @param callback the callback
     */
    @SuppressWarnings({"unchecked"})
    public void loopOrSingle(final Object x, final IterCallback<V> callback) {
        if (x == null) {
            return;
        }

        //A collection
        if (x instanceof Collection<?>) {
            final Collection<?> l = (Collection<?>) x;
            for (final Object o : l) {
                callback.eval((V) o);
            }
            return;
        }

        //An array of Object[]
        if (x.getClass().isArray()) {
            for (final Object o : (Object[]) x) {
                callback.eval((V) o);
            }
            return;
        }

        callback.eval((V) x);
    }

    /**
     * A callback mechanism for processing Maps
     *
     * @param <K> the type map keys
     * @param <V> the type map values
     */
    @FunctionalInterface
    public interface MapIterCallback<K, V> {
        /**
         * The method to call in the callback
         *
         * @param k the key from the map
         * @param v the value for the key
         */
        void eval(K k, V v);
    }

    /**
     * A callback mechanism for processing Iterables
     *
     * @param <V> the type Iterable elements
     */
    @FunctionalInterface
    public interface IterCallback<V> {
        /**
         * The method to call in the callback
         *
         * @param v an element in the Iterable
         */
        void eval(V v);
    }
}
