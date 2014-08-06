package org.mongodb.morphia.query;

import com.mongodb.DBObject;
import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.mongodb.morphia.testutil.JSONMatcher.jsonEqual;

public class PointTest {
    @Test
    public void shouldConvertToDBObject() {
        // given
        double latitude = 5.1;
        double longitude = 7.3;
        GeoJson.Point point = GeoJson.point(latitude, longitude);

        // when
        DBObject dbObject = point.asDBObject();

        // then
        assertThat(dbObject.toString(), jsonEqual("  {"
                                                  + "  type: 'Point', "
                                                  + "  coordinates: [" + longitude + ", " + latitude + "]"
                                                  + "}"
                                                 ));
    }

}
