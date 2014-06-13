package org.mongodb.morphia.query.validation;

import org.junit.Ignore;
import org.junit.Test;
import org.mongodb.morphia.entities.SimpleEntity;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mongodb.morphia.query.FilterOperator.SIZE;

public class SizeOperationValidatorTest {
    @Test
    public void shouldAllowSizeOperatorForListTypesAndIntegerValues() {
        // expect
        assertThat(SizeOperationValidator.validate(List.class, SIZE, 3), is(true));
    }

    @Test
    @Ignore("So we don't allow arrays?")
    public void shouldAllowSizeOperatorForArraysAndIntegerValues() {
        // expect
        assertThat(SizeOperationValidator.validate(new int[0].getClass(), SIZE, 3), is(true));
    }

    @Test
    @Ignore("OK this is weird, I'd expect ArrayList to be OK. I think the logic is backwards")
    public void shouldAllowSizeOperatorForAllListTypesAndIntegerValues() {
        // expect
        assertThat(SizeOperationValidator.validate(ArrayList.class, SIZE, 3), is(true));
    }

    @Test
    public void shouldNotAllowSizeOperatorForNonListTypes() {
        // expect
        assertThat(SizeOperationValidator.validate(SimpleEntity.class, SIZE, 3), is(false));
    }

    @Test
    public void shouldNotAllowSizeOperatorForNonIntegerValues() {
        // expect
        assertThat(SizeOperationValidator.validate(ArrayList.class, SIZE, "value"), is(false));
    }

}