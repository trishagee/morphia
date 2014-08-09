package org.mongodb.morphia.geo;

/**
 * Creates Point instances.  The major advantage of this builder is that it reduces confusion over which double is latitude and which is
 * longitude.
 *
 * @see org.mongodb.morphia.geo.Point
 */
public class PointBuilder {
    private double longitude;
    private double latitude;

    /**
     * Add a latitude.
     *
     * @param latitude the latitude of the point
     * @return this PointBuilder
     */
    public PointBuilder latitude(final double latitude) {
        this.latitude = latitude;
        return this;
    }

    /**
     * Add a longitude.
     *
     * @param longitude the longitude of the point
     * @return this PointBuilder
     */
    public PointBuilder longitude(final double longitude) {
        this.longitude = longitude;
        return this;
    }

    /**
     * Creates an immutable point
     *
     * @return the Point with the specifications from this builder.
     */
    public Point build() {
        return new Point(latitude, longitude);
    }
}
