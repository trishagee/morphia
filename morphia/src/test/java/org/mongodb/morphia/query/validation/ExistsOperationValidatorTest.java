package org.mongodb.morphia.query.validation;

import org.junit.Test;
import org.mongodb.morphia.query.FilterOperator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mongodb.morphia.query.FilterOperator.EXISTS;

public class ExistsOperationValidatorTest {

    @Test
    public void shouldAllowBooleanValuesForExistsOperator() {
        // expect
        assertThat(ExistsOperationValidator.validate(EXISTS, true), is(true));
    }

    @Test
    public void shouldNotAllowNonBooleanValuesForExistsOperator() {
        // expect
        assertThat(ExistsOperationValidator.validate(EXISTS, "value"), is(false));
    }

    @Test
    public void shouldNotSupportOperatorsThatAreNotExists() {
        // expect
        assertThat(ExistsOperationValidator.validate(FilterOperator.ALL, true), is(false));
    }

}