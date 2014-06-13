package org.mongodb.morphia.query.validation;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mongodb.morphia.query.FilterOperator.NOT_IN;

public class NotInOperationValidatorTest {
    @Test
    public void shouldAllowNotInOperatorForIterableMapAndArrayValues() {
        // expect
        assertThat(NotInOperationValidator.validate(NOT_IN, Arrays.asList(1, 2)), is(true));
        assertThat(NotInOperationValidator.validate(NOT_IN, Collections.emptySet()), is(true));
        assertThat(NotInOperationValidator.validate(NOT_IN, new HashMap<String, String>()), is(true));
        assertThat(NotInOperationValidator.validate(NOT_IN, new int[0]), is(true));
    }

    @Test
    public void shouldNotAllowOtherValuesForNotInOperator() {
        // expect
        assertThat(NotInOperationValidator.validate(NOT_IN, "value"), is(false));
    }
}