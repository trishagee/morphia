package org.mongodb.morphia.query.validation;

import org.junit.Test;
import org.mongodb.morphia.entities.SimpleEntity;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IntegerValueTypeValidatorTest {
    @Test
    public void shouldOnlyAllowValuesOfIntegerIfTypeIsIntOrLong() {
        // expect
        assertThat(IntegerValueTypeValidator.validate(Long.class, new Integer(1)), is(true));
        assertThat(IntegerValueTypeValidator.validate(int.class, new Integer(1)), is(true));
        assertThat(IntegerValueTypeValidator.validate(long.class, new Integer(1)), is(true));
        assertThat(IntegerValueTypeValidator.validate(Integer.class, new Integer(1)), is(true));
    }

    @Test
    public void shouldNotAllowNonIntegerTypeIfValueIsInt() {
        // expect
        assertThat(IntegerValueTypeValidator.validate(SimpleEntity.class, new Integer(1)), is(false));
    }

}