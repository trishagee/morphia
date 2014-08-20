package org.mongodb.morphia.geo;

import org.junit.Test;
import org.mongodb.morphia.TestBase;
import org.mongodb.morphia.testutil.JSONMatcher;

import static org.junit.Assert.assertThat;
import static org.mongodb.morphia.geo.GeoJson.point;

public class MultiPointConverterTest extends TestBase {
    @Test
    public void shouldEncodeAnEntityWithALocationStoredAsAMultiPoint() {
        // given
        MultiPointConverter converter = new MultiPointConverter();
        MultiPoint multiPoint = GeoJson.multiPoint(point(1, 2), point(3, 5), point(19, 13));

        // when
        Object encoded = converter.encode(multiPoint);

        // then use the underlying driver to ensure it was persisted correctly to the database
        assertThat(encoded.toString(), JSONMatcher.jsonEqual("  {"
                                                             + "  type: 'MultiPoint', "
                                                             + "  coordinates: [ [ 2.0,  1.0],"
                                                             + "                 [ 5.0,  3.0],"
                                                             + "                 [13.0, 19.0] ]"
                                                             + "}"));
    }

}
