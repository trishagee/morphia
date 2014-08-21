package org.mongodb.morphia.geo;

import org.junit.Test;
import org.mongodb.morphia.TestBase;
import org.mongodb.morphia.testutil.JSONMatcher;

import static org.junit.Assert.assertThat;
import static org.mongodb.morphia.geo.GeoJson.point;

public class MultiPolygonConverterTest extends TestBase{
    @Test
    public void shouldEncodeAnEntityWithAMultiPolygonGeoJsonType() {
        // given
        MultiPolygonConverter converter = new MultiPolygonConverter();
        converter.setMapper(getMorphia().getMapper());
        Polygon polygonWithHoles = GeoJson.polygonBuilder(point(1.1, 2.0), point(2.3, 3.5), point(3.7, 1.0), point(1.1, 2.0))
                                          .interiorRing(point(1.5, 2.0), point(1.9, 2.0), point(1.9, 1.8), point(1.5, 2.0))
                                          .interiorRing(point(2.2, 2.1), point(2.4, 1.9), point(2.4, 1.7), point(2.1, 1.8),
                                                        point(2.2, 2.1))
                                          .build();
        MultiPolygon multiPolygon = GeoJson.multiPolygon(GeoJson.polygon(point(1.1, 2.0),
                                                                         point(2.3, 3.5),
                                                                         point(3.7, 1.0),
                                                                         point(1.1, 2.0)),
                                                         polygonWithHoles);

        // when
        Object encoded = converter.encode(multiPolygon);

        // then 
        assertThat(encoded.toString(), JSONMatcher.jsonEqual("  {"
                                                             + "  type: 'MultiPolygon', "
                                                             + "  coordinates: [ [ [ [ 2.0, 1.1],"
                                                             + "                     [ 3.5, 2.3],"
                                                             + "                     [ 1.0, 3.7],"
                                                             + "                     [ 2.0, 1.1],"
                                                             + "                   ]"
                                                             + "                 ],"
                                                             + "                 [ [ [ 2.0, 1.1],"
                                                             + "                     [ 3.5, 2.3],"
                                                             + "                     [ 1.0, 3.7],"
                                                             + "                     [ 2.0, 1.1] "
                                                             + "                   ],"
                                                             + "                   [ [ 2.0, 1.5],"
                                                             + "                     [ 2.0, 1.9],"
                                                             + "                     [ 1.8, 1.9],"
                                                             + "                     [ 2.0, 1.5] "
                                                             + "                   ],"
                                                             + "                   [ [ 2.1, 2.2],"
                                                             + "                     [ 1.9, 2.4],"
                                                             + "                     [ 1.7, 2.4],"
                                                             + "                     [ 1.8, 2.1],"
                                                             + "                     [ 2.1, 2.2] "
                                                             + "                   ]"
                                                             + "                 ]"
                                                             + "               ]"
                                                             + "}"));
    }

}
