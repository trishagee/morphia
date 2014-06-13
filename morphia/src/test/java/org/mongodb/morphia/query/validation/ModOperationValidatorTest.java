package org.mongodb.morphia.query.validation;

import org.junit.Ignore;
import org.junit.Test;
import org.mongodb.morphia.query.ValidationException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mongodb.morphia.query.FilterOperator.MOD;

public class ModOperationValidatorTest {
    @Test
    public void shouldAllowModOperatorForArrayOfIntegerValues() {
        // expect
        assertThat(ModOperationValidator.validate(MOD, new int[1]), is(true));
    }

    @Test
    @Ignore("bug waiting to happen")
    public void shouldNotErrorIfModOperatorIsUsedWithZeroLengthArrayOfIntegerValues() {
        // expect
        assertThat(ModOperationValidator.validate(MOD, new int[0]), is(false));
    }

    @Test(expected = ValidationException.class)
    public void shouldNotAllowModOperatorWithNonIntegerArray() {
        // expect
        ModOperationValidator.validate(MOD, new String[]{"value"});
        //TODO: Trish - this needs to be refactored so Exceptions aren't used
        //        assertThat(ModOperationValidator.validate(MOD, new String[]{"value"}), is(false));
    }

    @Test
    @Ignore("bug waiting to happen")
    public void shouldNotErrorModOperatorWithArrayOfNullValues() {
        // expect
        assertThat(ModOperationValidator.validate(MOD, new String[1]), is(false));
    }

    @Test
    public void shouldNotAllowModOperatorWithNonArrayValue() {
        // expect
        assertThat(ModOperationValidator.validate(MOD, "value"), is(false));
    }

}