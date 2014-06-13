package org.mongodb.morphia.query.validation;

import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ListValueValidatorTest {
    @Test
    public void shouldAllowValuesOfList() {
        // expect
        assertThat(ListValueValidator.validator(new ArrayList<String>()), is(true));
    }
}