package org.mongodb.morphia.query.validation;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DefaultTypeValueValidatorTest {
    @Test
    public void shouldAllowTypesThatMatchTheClassOfTheValue() {
        // expect
        assertThat(DefaultTypeValueValidator.validate(String.class, "some String"), is(true));
    }

    @Test
    public void shouldAllowTypesThatTheRealTypeOfTheValue() {
        // given
        List<Integer> valueAsList = Arrays.asList(1);
        assertThat(DefaultTypeValueValidator.validate(ArrayList.class, valueAsList), is(true));
    }

    @Test
    public void shouldNotAllowTypesThatAreSuperclasses() {
        // given
        assertThat(DefaultTypeValueValidator.validate(Map.class, new HashMap()), is(false));
    }

    @Test
    public void shouldRejectTypesAndValuesThatDoNotMatch() {
        // expect
        assertThat(DefaultTypeValueValidator.validate(String.class, 1), is(false));
    }
}