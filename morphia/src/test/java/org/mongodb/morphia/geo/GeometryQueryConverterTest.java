package org.mongodb.morphia.geo;

import org.junit.Test;
import org.mongodb.morphia.TestBase;
import org.mongodb.morphia.testutil.JSONMatcher;

import java.util.Optional;

import static org.junit.Assert.assertThat;
import static org.mongodb.morphia.geo.GeoJson.point;

public class GeometryQueryConverterTest extends TestBase {
    @Test
    public void shouldCorrectlyEncodePointsIntoQueryDocument() {
        // given
        GeometryQueryConverter geometryConverter = new GeometryQueryConverter(getMorphia().getMapper());
        geometryConverter.setMapper(getMorphia().getMapper());

        Geometry point = point(3.0, 7.0);

        // when
        Object dbObject = geometryConverter.encode(Optional.of(point));


        // then
        assertThat(dbObject.toString(), JSONMatcher.jsonEqual("  { $geometry : "
                                                              + "  { type : 'Point' , "
                                                              + "    coordinates : " + point.getCoordinates()
                                                              + "  }"
                                                              + "}"));
    }
}
