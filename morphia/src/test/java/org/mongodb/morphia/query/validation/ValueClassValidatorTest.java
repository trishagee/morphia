package org.mongodb.morphia.query.validation;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ValueClassValidatorTest {
    @Test
    public void shouldAllowClassesWithExactlyTheSameType() {
        // expect
        assertThat(ValueClassValidator.valueIsA(new Integer(1), Integer.class), is(true));
    }

    @Test
    public void shouldAllowPrimitiveValuesComparedToObjectType() {
        // expect
        assertThat(ValueClassValidator.valueIsA(1, Integer.class), is(true));
    }

    @Test
    public void shouldRejectValueThatDoesNotMatchType() {
        // expect
        assertThat(ValueClassValidator.valueIsA(1, String.class), is(false));
    }

    @Test
    public void shouldRejectValueWithClassThatIsSubclassOfType() {
        // I'm not sure this is actually required behaviour, but it's what is already implemented.
        // expect
        assertThat(ValueClassValidator.valueIsA(new ArrayList(), List.class), is(false));
    }

}