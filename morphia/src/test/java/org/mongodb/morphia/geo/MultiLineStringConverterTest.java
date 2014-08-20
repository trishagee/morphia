package org.mongodb.morphia.geo;

import org.junit.Test;
import org.mongodb.morphia.TestBase;
import org.mongodb.morphia.testutil.JSONMatcher;

import static org.junit.Assert.assertThat;
import static org.mongodb.morphia.geo.GeoJson.lineString;
import static org.mongodb.morphia.geo.GeoJson.point;

public class MultiLineStringConverterTest extends TestBase {
    @Test
    public void shouldEncodeAnEntityWithAMultiLineStringGeoJsonType() {
        // given
        MultiLineStringConverter converter = new MultiLineStringConverter();
        converter.setMapper(getMorphia().getMapper());
        MultiLineString multiLineString = GeoJson.multiLineString(lineString(point(1, 2), point(3, 5), point(19, 13)),
                                                                  lineString(point(1.5, 2.0),
                                                                             point(1.9, 2.0),
                                                                             point(1.9, 1.8),
                                                                             point(1.5, 2.0)));

        // when
        Object encoded = converter.encode(multiLineString);

        // then 
        assertThat(encoded.toString(), JSONMatcher.jsonEqual("  {"
                                                             + "  type: 'MultiLineString', "
                                                             + "  coordinates: "
                                                             + "     [ [ [ 2.0,  1.0],"
                                                             + "         [ 5.0,  3.0],"
                                                             + "         [13.0, 19.0] "
                                                             + "       ], "
                                                             + "       [ [ 2.0, 1.5],"
                                                             + "         [ 2.0, 1.9],"
                                                             + "         [ 1.8, 1.9],"
                                                             + "         [ 2.0, 1.5] "
                                                             + "       ]"
                                                             + "     ]"
                                                             + "}"));
    }

}