package org.mongodb.morphia.geo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.junit.Test;
import org.mongodb.morphia.TestBase;
import org.mongodb.morphia.query.GeoJson;
import org.mongodb.morphia.testutil.JSONMatcher;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class SaveGeoEntitiesTest extends TestBase {
    @Test
    public void shouldSaveAnEntityWithALocationStoredAsAPoint() {
        // given
        City city = new City("New City", GeoJson.point(3.0, 7.0));

        // when
        getDs().save(city);

        // then use the underlying driver to ensure it was persisted correctly to the database
        DBObject storedCity = getDs().getCollection(City.class).findOne(new BasicDBObject("name", "New City"),
                                                                        new BasicDBObject("_id", 0).append("className", 0));
        assertThat(storedCity, is(notNullValue()));
        assertThat(storedCity.toString(), JSONMatcher.jsonEqual("  {" 
                                                                + " name: 'New City'," 
                                                                + " location:  " 
                                                                + " {"
                                                                + "  type: 'Point', "
                                                                + "  coordinates: [7.0, 3.0]"
                                                                + " }" 
                                                                + "}"));

    }

}
