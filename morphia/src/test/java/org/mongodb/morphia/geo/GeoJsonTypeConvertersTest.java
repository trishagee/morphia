package org.mongodb.morphia.geo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.junit.Ignore;
import org.junit.Test;
import org.mongodb.morphia.TestBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mongodb.morphia.geo.GeoJson.point;

public class GeoJsonTypeConvertersTest extends TestBase {
    @Test
    @Ignore("honestly, I have no idea how to test converters without doing a full round trip")
    public void shouldCorrectlyDeserialiseGeoCollectionContainingAPointFromDBObject() {
        // given
//        GeometryCollection geometryCollection = GeoJson.geometryCollection()
//                                                       .add(point)
//                                                       .build();
//        DBObject dbObject = getMorphia().toDBObject(geometryCollection);

        getMorphia().map(GeometryCollection.class);
//        getMorphia().getMapper().getConverters().addConverter(new GeoJsonTypeConverter());


        Point point = point(3.0, 7.0);
        List<BasicDBObject> geometries = new ArrayList<BasicDBObject>();
        geometries.add(new BasicDBObject("type", "Point").append("coordinates", Arrays.asList(7.0, 3.0)));
        DBObject dbObject = new BasicDBObject("type", "GeometryCollection").append("geometries", geometries);

        // when
        GeometryCollection geoCollection = getMorphia().fromDBObject(GeometryCollection.class, dbObject);

        // then
//        assertThat(geoCollection, is(instanceOf(Point.class)));
        GeometryCollection geometryCollection = GeoJson.geometryCollection()
                                                       .add(point)
                                                       .build();
        assertThat(geoCollection, is(geometryCollection));
    }

    @Test
    public void shouldCorrectlyDeserialiseGeoCollectionContainingAPoint() {
        // given
        getMorphia().map(GeometryCollection.class);
        //        getMorphia().getMapper().getConverters().addConverter(new GeoJsonTypeConverter());

        Point point = point(3.0, 7.0);
        GeometryCollection geometryCollection = GeoJson.geometryCollection()
                                                       .add(point)
                                                       .build();
        getDs().save(geometryCollection);

        // when
        GeometryCollection found = getDs().find(GeometryCollection.class).get();

        // then
        assertThat(found, is(geometryCollection));
    }

}