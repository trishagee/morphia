package org.mongodb.morphia.geo;

import org.junit.Test;
import org.mongodb.morphia.TestBase;
import org.mongodb.morphia.testutil.JSONMatcher;

import static org.junit.Assert.assertThat;
import static org.mongodb.morphia.geo.GeoJson.point;

public class PointConverterTest extends TestBase {
    @Test
    public void shouldCorrectlyEncodePointsIntoEntityDocument() {
        // given
        PointConverter pointConverter = new PointConverter();
        pointConverter.setMapper(getMorphia().getMapper());

        Point point = point(3.0, 7.0);

        // when
        Object dbObject = pointConverter.encode(point, null);


        // then
        assertThat(dbObject.toString(), JSONMatcher.jsonEqual("  { " 
                                                              + "  type : 'Point' , "
                                                              + "  coordinates : [7, 3]"
                                                              + "}"));
    }

}