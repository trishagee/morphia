package org.mongodb.morphia.geo;

import com.mongodb.BasicDBObject;
import org.junit.Test;
import org.mongodb.morphia.TestBase;
import org.mongodb.morphia.testutil.JSONMatcher;

import static org.junit.Assert.assertThat;
import static org.mongodb.morphia.geo.GeoJson.point;

public class GeometryQueryConverterTest extends TestBase {
    @SuppressWarnings("ConstantConditions")
    @Test
    public void shouldCorrectlyEncodePointsIntoQueryDocument() {
        // given
        GeometryQueryConverter geometryConverter = new GeometryQueryConverter(getMorphia().getMapper());
        geometryConverter.setMapper(getMorphia().getMapper());

        Point point = point(3.0, 7.0);

        // when
        BasicDBObject dbObject = (BasicDBObject) geometryConverter.encode(point).get();


        // then
        assertThat(dbObject.toString(), JSONMatcher.jsonEqual("  { $geometry : "
                                                              + "  { type : 'Point' , "
                                                              + "    coordinates : " + point.getCoordinates()
                                                              + "  }"
                                                              + "}"));
    }
}
