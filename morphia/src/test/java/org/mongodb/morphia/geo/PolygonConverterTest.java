package org.mongodb.morphia.geo;

import org.junit.Test;
import org.mongodb.morphia.testutil.JSONMatcher;

import static org.junit.Assert.assertThat;
import static org.mongodb.morphia.geo.GeoJson.point;

public class PolygonConverterTest {
    @Test
    public void shouldSaveAnEntityWithAPolygonGeoJsonType() {
        // given
        PolygonConverter converter = new PolygonConverter();
        Polygon polygon = GeoJson.polygon(point(1.1, 2.0), point(2.3, 3.5), point(3.7, 1.0), point(1.1, 2.0));

        // when
        Object encodedPolygon = converter.encode(polygon);

        // then 
        assertThat(encodedPolygon.toString(), JSONMatcher.jsonEqual("  {"
                                                                    + "  type: 'Polygon', "
                                                                    + "  coordinates: [ [ [ 2.0, 1.1],"
                                                                    + "                   [ 3.5, 2.3],"
                                                                    + "                   [ 1.0, 3.7],"
                                                                    + "                   [ 2.0, 1.1] ] ]"
                                                                    + "}"));
    }

}