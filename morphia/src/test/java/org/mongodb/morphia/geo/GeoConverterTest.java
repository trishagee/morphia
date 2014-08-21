package org.mongodb.morphia.geo;

import org.junit.Test;
import org.mongodb.morphia.TestBase;
import org.mongodb.morphia.testutil.JSONMatcher;

import static org.junit.Assert.assertThat;
import static org.mongodb.morphia.geo.GeoJson.lineString;
import static org.mongodb.morphia.geo.GeoJson.point;

public class GeoConverterTest extends TestBase{
    @Test
    public void shouldEncodeAnEntityWithAMultiPolygonGeoJsonType() {
        // given
        GeoConverter.MultiPolygonConverter converter = new GeoConverter.MultiPolygonConverter();
        converter.setMapper(getMorphia().getMapper());
        Polygon polygonWithHoles = GeoJson.polygonBuilder(point(1.1, 2.0), point(2.3, 3.5), point(3.7, 1.0), point(1.1, 2.0))
                                          .interiorRing(point(1.5, 2.0), point(1.9, 2.0), point(1.9, 1.8), point(1.5, 2.0))
                                          .interiorRing(point(2.2, 2.1), point(2.4, 1.9), point(2.4, 1.7), point(2.1, 1.8),
                                                        point(2.2, 2.1))
                                          .build();
        MultiPolygon multiPolygon = GeoJson.multiPolygon(GeoJson.polygon(point(1.1, 2.0),
                                                                         point(2.3, 3.5),
                                                                         point(3.7, 1.0),
                                                                         point(1.1, 2.0)),
                                                         polygonWithHoles);

        // when
        Object encoded = converter.encode(multiPolygon);

        // then 
        assertThat(encoded.toString(), JSONMatcher.jsonEqual("  {"
                                                             + "  type: 'MultiPolygon', "
                                                             + "  coordinates: [ [ [ [ 2.0, 1.1],"
                                                             + "                     [ 3.5, 2.3],"
                                                             + "                     [ 1.0, 3.7],"
                                                             + "                     [ 2.0, 1.1],"
                                                             + "                   ]"
                                                             + "                 ],"
                                                             + "                 [ [ [ 2.0, 1.1],"
                                                             + "                     [ 3.5, 2.3],"
                                                             + "                     [ 1.0, 3.7],"
                                                             + "                     [ 2.0, 1.1] "
                                                             + "                   ],"
                                                             + "                   [ [ 2.0, 1.5],"
                                                             + "                     [ 2.0, 1.9],"
                                                             + "                     [ 1.8, 1.9],"
                                                             + "                     [ 2.0, 1.5] "
                                                             + "                   ],"
                                                             + "                   [ [ 2.1, 2.2],"
                                                             + "                     [ 1.9, 2.4],"
                                                             + "                     [ 1.7, 2.4],"
                                                             + "                     [ 1.8, 2.1],"
                                                             + "                     [ 2.1, 2.2] "
                                                             + "                   ]"
                                                             + "                 ]"
                                                             + "               ]"
                                                             + "}"));
    }

    @Test
    public void shouldConvertAnEntityWithAPolygonGeoJsonType() {
        // given
        GeoConverter.PolygonConverter converter = new GeoConverter.PolygonConverter();
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
    public void shouldEncodeAnEntityWithAMultiLineStringGeoJsonType() {
        // given
        GeoConverter.MultiLineStringConverter converter = new GeoConverter.MultiLineStringConverter();
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

    @Test
    public void shouldSaveAnEntityWithALineStringGeoJsonType() {
        // given
        GeoConverter.LineStringConverter converter = new GeoConverter.LineStringConverter();
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

    @Test
    public void shouldCorrectlyEncodePointsIntoEntityDocument() {
        // given
        GeoConverter.PointConverter pointConverter = new GeoConverter.PointConverter();
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
