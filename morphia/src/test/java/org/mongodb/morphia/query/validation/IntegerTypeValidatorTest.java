package org.mongodb.morphia.query.validation;

import org.junit.Test;
import org.mongodb.morphia.entities.SimpleEntity;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IntegerTypeValidatorTest {
    @Test
    public void shouldAllowIntegerValueWhenTypeIsInteger() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = IntegerTypeValidator.getInstance().apply(Integer.class, new Integer(1), validationFailures);
        // then
        assertThat(validationApplied, is(true));
        assertThat(validationFailures.size(), is(0));
    }

    @Test
    public void shouldAllowIntegerValueWhenTypeIsInt() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = IntegerTypeValidator.getInstance().apply(int.class, new Integer(1), validationFailures);
        // then
        assertThat(validationApplied, is(true));
        assertThat(validationFailures.size(), is(0));
    }

    @Test
    public void shouldAllowIntegerValueWhenTypeIsLong() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = IntegerTypeValidator.getInstance().apply(Long.class, new Integer(1), validationFailures);
        // then
        assertThat(validationApplied, is(true));
        assertThat(validationFailures.size(), is(0));
    }

    @Test
    public void shouldAllowIntegerValueWhenTypeIsPrimitiveLong() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = IntegerTypeValidator.getInstance().apply(long.class, new Integer(1), validationFailures);
        // then
        assertThat(validationApplied, is(true));
        assertThat(validationFailures.size(), is(0));
    }

    @Test

    public void shouldAllowIntValueWhenTypeIsInteger() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = IntegerTypeValidator.getInstance().apply(Integer.class, 1, validationFailures);
        // then
        assertThat(validationApplied, is(true));
        assertThat(validationFailures.size(), is(0));
    }

    @Test
    public void shouldAllowIntValueWhenTypeIsInt() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = IntegerTypeValidator.getInstance().apply(int.class, 1, validationFailures);
        // then
        assertThat(validationApplied, is(true));
        assertThat(validationFailures.size(), is(0));
    }

    @Test
    public void shouldAllowIntValueWhenTypeIsLong() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = IntegerTypeValidator.getInstance().apply(Long.class, 1, validationFailures);
        // then
        assertThat(validationApplied, is(true));
        assertThat(validationFailures.size(), is(0));
    }

    @Test
    public void shouldAllowIntValueWhenTypeIsPrimitiveLong() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = IntegerTypeValidator.getInstance().apply(long.class, 1, validationFailures);
        // then
        assertThat(validationApplied, is(true));
        assertThat(validationFailures.size(), is(0));
    }

    @Test
    public void shouldRejectNonIntegerTypeIfValueIsInt() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = IntegerTypeValidator.getInstance().apply(Integer.class, "some non int value", validationFailures);
        // then
        assertThat(validationApplied, is(true));
        assertThat(validationFailures.size(), is(1));
    }

    @Test
    public void shouldNotApplyValidationIfTypeIsNotIntegerOrLong() {
        // given
        ArrayList<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();
        // when
        boolean validationApplied = IntegerTypeValidator.getInstance().apply(SimpleEntity.class, new Integer(1), validationFailures);
        // then
        assertThat(validationApplied, is(false));
        assertThat(validationFailures.size(), is(0));
    }

}