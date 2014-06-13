package org.mongodb.morphia.query.validation;

import org.junit.Test;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.entities.SimpleEntity;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EntityAnnotatedValueValidatorTest {
    @Test
    public void shouldAllowValueWithEntityAnnotationAndTypeOfKey() {
        // expect
        assertThat(EntityAnnotatedValueValidator.validate(Key.class, new SimpleEntity()), is(true));
    }

    @Test
    public void shouldNotAllowValueWithEntityAnnotationAndNonKeyType() {
        // expect
        assertThat(EntityAnnotatedValueValidator.validate(String.class, new SimpleEntity()), is(false));
    }

    @Test
    public void shouldNotAllowValueWithoutEntityAnnotationAndTypeOfKey() {
        // expect
        assertThat(EntityAnnotatedValueValidator.validate(Key.class, "value"), is(false));
    }
}