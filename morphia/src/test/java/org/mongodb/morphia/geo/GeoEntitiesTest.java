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
public class GeoEntitiesTest extends TestBase {
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
    public void shouldRetrieveGeoJsonPoint() {
        // given
        City city = new City("New City", point(3.0, 7.0));
        getDs().save(city);

        // when
        City found = getDs().find(City.class).field("name").equal("New City").get();

        // then
        assertThat(found, is(notNullValue()));
        assertThat(found, is(city));
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
    public void shouldRetrieveGeoJsonLineString() {
        // given
        Route route = new Route("My Route", GeoJson.lineString(point(1, 2), point(3, 5), point(19, 13)));
        getDs().save(route);

        // when
        Route found = getDs().find(Route.class).field("name").equal("My Route").get();

        // then
        assertThat(found, is(notNullValue()));
        assertThat(found, is(route));
    }

    @Test
    public void shouldSaveAnEntityWithAPolygonGeoJsonType() {
        // given
        Area area = new Area("The Area", GeoJson.polygon(point(1.1, 2.0), point(2.3, 3.5), point(3.7, 1.0), point(1.1, 2.0)));

        // when
        getDs().save(area);

        // then use the underlying driver to ensure it was persisted correctly to the database
        DBObject storedArea = getDs().getCollection(Area.class).findOne(new BasicDBObject("name", "The Area"),
                                                                        new BasicDBObject("_id", 0)
                                                                        .append("className", 0)
                                                                        .append("area.className", 0));
        assertThat(storedArea, is(notNullValue()));
        assertThat(storedArea.toString(), JSONMatcher.jsonEqual("  {"
                                                                + " name: 'The Area',"
                                                                + " area:  "
                                                                + " {"
                                                                + "  type: 'Polygon', "
                                                                + "  coordinates: [ [ [ 2.0, 1.1],"
                                                                + "                   [ 3.5, 2.3],"
                                                                + "                   [ 1.0, 3.7],"
                                                                + "                   [ 2.0, 1.1] ] ]"
                                                                + " }"
                                                                + "}"));
    }

    @Test
    public void shouldRetrieveGeoJsonPolygon() {
        // given
        Area area = new Area("The Area", GeoJson.polygon(point(1.1, 2.0), point(2.3, 3.5), point(3.7, 1.0), point(1.1, 2.0)));
        getDs().save(area);

        // when
        Area found = getDs().find(Area.class).field("name").equal("The Area").get();

        // then
        assertThat(found, is(notNullValue()));
        assertThat(found, is(area));
    }

    @Entity
    @SuppressWarnings("UnusedDeclaration")
    private static final class Route {
        private String name;
        private GeoJson.LineString route;

        private Route() {
        }

        private Route(final String name, final GeoJson.LineString route) {
            this.name = name;
            this.route = route;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Route route1 = (Route) o;

            if (name != null ? !name.equals(route1.name) : route1.name != null) {
                return false;
            }
            if (route != null ? !route.equals(route1.route) : route1.route != null) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (route != null ? route.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Route{"
                   + "name='" + name + '\''
                   + ", route=" + route
                   + '}';
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    private static final class Area {
        private String name;
        private GeoJson.Polygon area;

        private Area() {
        }

        private Area(final String name, final GeoJson.Polygon area) {
            this.name = name;
            this.area = area;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Area area1 = (Area) o;

            if (area != null ? !area.equals(area1.area) : area1.area != null) {
                return false;
            }
            if (name != null ? !name.equals(area1.name) : area1.name != null) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (area != null ? area.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Area{"
                   + "name='" + name + '\''
                   + ", area=" + area
                   + '}';
        }
    }
}
