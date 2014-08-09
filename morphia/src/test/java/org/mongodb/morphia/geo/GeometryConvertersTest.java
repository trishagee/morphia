package org.mongodb.morphia.geo;

import org.junit.Test;
import org.mongodb.morphia.TestBase;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mongodb.morphia.geo.GeoJson.lineString;
import static org.mongodb.morphia.geo.GeoJson.point;

public class GeometryConvertersTest extends TestBase {
    @Test
    public void shouldCorrectlyDecodeGeoCollectionContainingAPoint() {
        // given
        Point point = point(3.0, 7.0);
        GeometryCollection geometryCollection = GeoJson.geometryCollectionBuilder()
                                                       .add(point)
                                                       .build();
        getDs().save(geometryCollection);

        // when
        GeometryCollection found = getDs().find(GeometryCollection.class).get();

        // then
        assertThat(found, is(geometryCollection));
    }

    @Test
    public void shouldCorrectlyDecodeGeoCollectionContainingALineString() {
        // given
        LineString lineString = lineString(point(1, 2), point(3, 5), point(19, 13));
        GeometryCollection geometryCollection = GeoJson.geometryCollectionBuilder()
                                                       .add(lineString)
                                                       .build();
        getDs().save(geometryCollection);

        // when
        GeometryCollection found = getDs().find(GeometryCollection.class).get();

        // then
        assertThat(found, is(geometryCollection));
    }

}