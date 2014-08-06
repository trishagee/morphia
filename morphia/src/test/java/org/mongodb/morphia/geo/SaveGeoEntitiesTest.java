package org.mongodb.morphia.geo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.junit.Test;
import org.mongodb.morphia.TestBase;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.query.GeoJson;
import org.mongodb.morphia.testutil.JSONMatcher;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mongodb.morphia.query.GeoJson.point;

/**
 * Test driving features for Issue 643 - add support for saving entities with GeoJSON.
 */
public class SaveGeoEntitiesTest extends TestBase {
    @Test
    public void shouldSaveAnEntityWithALocationStoredAsAPoint() {
        // given
        City city = new City("New City", point(3.0, 7.0));

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

    @Test
    public void shouldSaveAnEntityWithALineStringGeoJsonType() {
        // given
        Route route = new Route("My Route", GeoJson.lineString(point(1, 2), point(3, 5), point(19, 13)));

        // when
        getDs().save(route);

        // then use the underlying driver to ensure it was persisted correctly to the database
        DBObject storedRoute = getDs().getCollection(Route.class).findOne(new BasicDBObject("name", "My Route"),
                                                                          new BasicDBObject("_id", 0).append("className", 0));
        assertThat(storedRoute, is(notNullValue()));
        // lat/long is always long/lat on the server
        assertThat(storedRoute.toString(), JSONMatcher.jsonEqual("  {"
                                                                 + " name: 'My Route',"
                                                                 + " route:"
                                                                 + " {"
                                                                 + "  type: 'LineString', "
                                                                 + "  coordinates: [ [ 2.0,  1.0],"
                                                                 + "                 [ 5.0,  3.0],"
                                                                 + "                 [13.0, 19.0] ]"
                                                                 + " }"
                                                                 + "}"));
    }

    @Test
    public void shouldSaveAnEntityWithAPolygonGeoJsonType() {
        // given
        Area area = new Area("The Area", GeoJson.polygon(point(1.1, 2.0), point(2.3, 3.5), point(3.7, 1.0), point(1.1, 2.0)));

        // when
        getDs().save(area);

        // then use the underlying driver to ensure it was persisted correctly to the database
        DBObject storedArea = getDs().getCollection(Area.class).findOne(new BasicDBObject("name", "The Area"),
                                                                        new BasicDBObject("_id", 0).append("className", 0));
        assertThat(storedArea, is(notNullValue()));
        assertThat(storedArea.toString(), JSONMatcher.jsonEqual("  {"
                                                                + " name: 'The Area',"
                                                                + " area:  "
                                                                + " {"
                                                                + "  type: 'Polygon', "
                                                                + "  coordinates: [ [ 2.0, 1.1],"
                                                                + "                 [ 3.5, 2.3],"
                                                                + "                 [ 1.0, 3.7],"
                                                                + "                 [ 2.0, 1.1] ]"
                                                                + " }"
                                                                + "}"));
    }

    @Entity
    @SuppressWarnings("UnusedDeclaration")
    private static class Route {
        private final String name;
        private final GeoJson.LineString route;

        public Route(final String name, final GeoJson.LineString route) {
            this.name = name;
            this.route = route;
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    private static class Area {
        private final String name;
        private final GeoJson.Polygon area;

        public Area(final String name, final GeoJson.Polygon area) {
            this.name = name;
            this.area = area;
        }
    }
}
