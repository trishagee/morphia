package org.mongodb.morphia.query.validation;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mongodb.morphia.query.FilterOperator.ALL;

public class AllOperationValidatorTest {
    @Test
    public void shouldAllowAllOperatorForIterableMapAndArrayValues() {
        // expect
        assertThat(AllOperationValidator.validate(ALL, Arrays.asList(1, 2)), is(true));
        assertThat(AllOperationValidator.validate(ALL, Collections.emptySet()), is(true));
        assertThat(AllOperationValidator.validate(ALL, new HashMap<String, String>()), is(true));
        assertThat(AllOperationValidator.validate(ALL, new int[0]), is(true));
    }

    @Test
    public void shouldNotAllowOtherValuesForAllOperator() {
        // expect
        assertThat(AllOperationValidator.validate(ALL, "invalid value"), is(false));
    }

}