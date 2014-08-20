package org.mongodb.morphia.geo;

import com.mongodb.BasicDBObject;
import org.junit.Test;
import org.mongodb.morphia.TestBase;
import org.mongodb.morphia.testutil.JSONMatcher;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mongodb.morphia.geo.GeoJson.point;

public class PolygonConverterTest extends TestBase {
    @Test
    public void shouldConvertAnEntityWithAPolygonGeoJsonType() {
        // given
        PolygonConverter converter = new PolygonConverter();
        converter.setMapper(getMorphia().getMapper());
        Polygon polygon = GeoJson.polygonBuilder(point(1.1, 2.0), point(2.3, 3.5), point(3.7, 1.0), point(1.1, 2.0))
                                 .interiorRing(point(1.5, 2.0), point(1.9, 2.0), point(1.9, 1.8), point(1.5, 2.0))
                                 .interiorRing(point(2.2, 2.1), point(2.4, 1.9), point(2.4, 1.7), point(2.1, 1.8),
                                               point(2.2, 2.1))
                                 .build();

        // when
        Object encodedPolygon = converter.encode(polygon);

        // then 
        assertThat(encodedPolygon.toString(), JSONMatcher.jsonEqual("  {"
                                                                    + "  type: 'Polygon', "
                                                                    + "  coordinates: "
                                                                    + "    [ [ [ 2.0, 1.1],"
                                                                    + "        [ 3.5, 2.3],"
                                                                    + "        [ 1.0, 3.7],"
                                                                    + "        [ 2.0, 1.1] "
                                                                    + "      ],"
                                                                    + "      [ [ 2.0, 1.5],"
                                                                    + "        [ 2.0, 1.9],"
                                                                    + "        [ 1.8, 1.9],"
                                                                    + "        [ 2.0, 1.5] "
                                                                    + "      ],"
                                                                    + "      [ [ 2.1, 2.2],"
                                                                    + "        [ 1.9, 2.4],"
                                                                    + "        [ 1.7, 2.4],"
                                                                    + "        [ 1.8, 2.1],"
                                                                    + "        [ 2.1, 2.2] "
                                                                    + "      ]"
                                                                    + "    ]"
                                                                    + "}"));
    }

    @Test
    @SuppressWarnings("unchecked") // there's some nasty List stuff in here for saving the right-shaped DBObject into the database
    public void shouldDecodeGeoJsonPolygon() {
        // given
        PolygonConverter converter = new PolygonConverter();
        converter.setMapper(getMorphia().getMapper());

        BasicDBObject dbObject = new BasicDBObject("type", "Polygon");
        List<List<Double>> exteriorBoundary = asList(asList(2.0, 1.1));
        List<List<Double>> interiorBoundary = asList(asList(3.1, 4.5));
        List<List<List<Double>>> coordinates = asList(exteriorBoundary, interiorBoundary);
        dbObject.append("coordinates", coordinates);

        // when
        Object decoded = converter.decode(Polygon.class, dbObject);

        // then
        assertThat(decoded, is(instanceOf(Polygon.class)));
        
        Polygon.PolygonBoundary polygonExteriorBoundary = ((Polygon) decoded).getExteriorBoundary();
        assertThat(polygonExteriorBoundary.getPoints().size(), is(1));
        
        Point pointExteriorBoundary = polygonExteriorBoundary.getPoints().get(0);
        assertThat(pointExteriorBoundary.getLongitude(), is(2.0));
        assertThat(pointExteriorBoundary.getLatitude(), is(1.1));
        
        List<Polygon.PolygonBoundary> pointInteriorBoundaries = ((Polygon) decoded).getInteriorBoundaries();
        assertThat(pointInteriorBoundaries.size(), is(1));
        assertThat(pointInteriorBoundaries.get(0).getPoints().size(), is(1));
        
        Point pointInteriorBoundary = pointInteriorBoundaries.get(0).getPoints().get(0);
        assertThat(pointInteriorBoundary.getLongitude(), is(3.1));
        assertThat(pointInteriorBoundary.getLatitude(), is(4.5));
    }

}