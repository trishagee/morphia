package org.mongodb.morphia.mapping.validation;

import org.junit.Test;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;
import org.mongodb.morphia.mapping.Mapper;

import java.util.Map;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class MappingValidatorTest {
    @Test
    public void shouldKeepTrackOfAllViolations() {
        Mapper mapper = new Mapper();
        try {
            mapper.addMappedClass(MultipleValidationErrors.class);
            fail("Should have thrown a mapping exception with 3 violations");
        } catch (ConstraintViolationException e) {
            assertThat(e.getMessage(), startsWith("Number of violations: 3"));
        }
    }

    @Entity
    @SuppressWarnings("unused") //used by Morphia
    public static class MultipleValidationErrors {
        @Id
        private String id;
        @Id
        private String id2;

        @Embedded(value = "value")
        private Map<String, Integer> content1;
        @Property(value = "value")
        private String content2;
    }
}
