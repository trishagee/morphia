package org.mongodb.morphia.query.validation;

import com.mongodb.BasicDBObject;
import org.junit.Test;
import org.mongodb.morphia.mapping.MappedClass;
import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.mapping.Mapper;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mongodb.morphia.query.FilterOperator.GEO_WITHIN;

//TODO: Trish - this really needs to be beefed up to test all happy paths and edge conditions
public class GeoWithinOperationValidatorTest {
    @Test
    public void shouldAllowGeoWithinOperatorWithAllAppropriateTrimmings() {
        // expect
        MappedClass mappedClass = new MappedClass(GeoEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("array");
        assertThat(GeoWithinOperationValidator.validate(mappedField, List.class, GEO_WITHIN,
                                                        new BasicDBObject("$box", 1)), is(true));
    }

    @Test
    public void shouldNotAllowGeoWithinWhenValueDoesNotContainKeyword() {
        // expect
        MappedClass mappedClass = new MappedClass(GeoEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("array");
        assertThat(GeoWithinOperationValidator.validate(mappedField, List.class, GEO_WITHIN, new BasicDBObject("notValidKey", 1)),
                   is(false));
    }

    @Test
    public void shouldNotAllowGeoWithinWhenValueIsNotADBObject() {
        // expect
        MappedClass mappedClass = new MappedClass(GeoEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("array");
        assertThat(GeoWithinOperationValidator.validate(mappedField, List.class, GEO_WITHIN, "NotAGeoQuery"), is(false));
    }

    @Test
    public void shouldNotAllowGeoWithinOperatorWhenMappedFieldIsNotAnArrayOfNumbers() {
        // expect
        MappedClass mappedClass = new MappedClass(InvalidGeoEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("array");
        assertThat(GeoWithinOperationValidator.validate(mappedField, List.class, GEO_WITHIN,
                                                        new BasicDBObject("$box", 1)), is(false));
    }

    private static class GeoEntity {
        private final int[] array = {1};
    }

    private static class InvalidGeoEntity {
        private final String[] array = {"1"};
    }
}