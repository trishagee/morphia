package org.mongodb.morphia.geo;

import com.mongodb.DBObject;
import org.junit.Test;
import org.mongodb.morphia.TestBase;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

import java.util.List;

import static org.junit.Assert.assertThat;
import static org.mongodb.morphia.testutil.IndexMatcher.hasIndexNamed;
import static org.mongodb.morphia.utils.IndexType.GEO2DSPHERE;

public class DeclareGeoIndexTest extends TestBase {
    // Tests issue 290
    @Test
    public void shouldApplyGeoIndexDeclaredAtTheClassLevel() {
        // given
        Place pointB = new Place(GeoJson.point(3.1, 7.5), "Point B");
        getDs().save(pointB);

        // when
        getDs().ensureIndexes();

        // then
        List<DBObject> indexInfo = getDs().getCollection(Place.class).getIndexInfo();
        assertThat(indexInfo, hasIndexNamed("location_2dsphere"));
    }

    @SuppressWarnings("unused")
    @Indexes(@Index(fields = @Field(value = "location", type = GEO2DSPHERE)))
    private static final class Place {
        private Point location;
        private String name;

        private Place(final Point location, final String name) {
            this.location = location;
            this.name = name;
        }

        private Place() {
        }
    }
}
