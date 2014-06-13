package org.mongodb.morphia.query.validation;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mongodb.morphia.query.FilterOperator.IN;

public class InOperationValidatorTest {
    @Test
    public void shouldAllowInOperatorForIterableMapAndArrayValues() {
        // expect
        //TODO: Trisha - split into separate tests (or turn into Spock test)
        assertThat(InOperationValidator.validate(IN, Collections.emptySet()), is(true));
        assertThat(InOperationValidator.validate(IN, new HashMap<String, String>()), is(true));
        assertThat(InOperationValidator.validate(IN, new int[0]), is(true));

        List<Integer> list = Arrays.asList(1, 2);
        assertThat(InOperationValidator.validate(IN, list), is(true));
        ArrayList<Integer> arrayList = new ArrayList<Integer>(list);
        assertThat(InOperationValidator.validate(IN, arrayList), is(true));
    }

    @Test
    public void shouldNotAllowOtherValuesForInOperator() {
        // expect
        assertThat(InOperationValidator.validate(IN, "value"), is(false));
    }

}