package org.mongodb.morphia.geo;

import org.junit.Test;
import org.mongodb.morphia.TestBase;
import org.mongodb.morphia.testutil.JSONMatcher;

import static org.junit.Assert.assertThat;
import static org.mongodb.morphia.geo.GeoJson.lineString;
import static org.mongodb.morphia.geo.GeoJson.point;

public class LineStringConverterTest extends TestBase{
    @Test
    public void shouldSaveAnEntityWithALineStringGeoJsonType() {
        // given
        LineStringConverter converter = new LineStringConverter();
        converter.setMapper(getMorphia().getMapper());
        LineString lineString = lineString(point(1, 2), point(3, 5), point(19, 13));

        // when
        Object encodedLineString = converter.encode(lineString);

        // then
        assertThat(encodedLineString.toString(), JSONMatcher.jsonEqual("  {"
                                                                       + "  type: 'LineString', "
                                                                       + "  coordinates: [ [ 2.0,  1.0],"
                                                                       + "                 [ 5.0,  3.0],"
                                                                       + "                 [13.0, 19.0] ]"
                                                                       + "}"));
    }

}