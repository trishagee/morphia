package org.mongodb.morphia.query.validation;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IntegerOrLongValueTypeValidatorTest {
    @Test
    public void shouldOnlyAllowValuesOfIntegerIfTypeIsDouble() {
        // expect
        assertThat(IntegerOrLongValueTypeValidator.validate(Double.class, new Integer(1)), is(true));
        assertThat(IntegerOrLongValueTypeValidator.validate(double.class, new Integer(1)), is(true));
    }

    @Test
    public void shouldOnlyAllowValuesOfLongIfTypeIsDouble() {
        // expect
        assertThat(IntegerOrLongValueTypeValidator.validate(Double.class, new Long(1)), is(true));
        assertThat(IntegerOrLongValueTypeValidator.validate(double.class, new Long(1)), is(true));
    }

    @Test
    public void shouldNotAllowNonDoubleTypeIfValueIsLong() {
        // expect
        assertThat(IntegerOrLongValueTypeValidator.validate(String.class, new Long(1)), is(false));
    }

    @Test
    public void becauseOfTheWayTheValidationHasBeenSplitUpThisValidatorWillRejectLongValuesWithLongType() {
        // expect
        //TODO: do something simpler with type validation so this is no longer true
        assertThat(IntegerOrLongValueTypeValidator.validate(Long.class, new Long(1)), is(false));
    }
}