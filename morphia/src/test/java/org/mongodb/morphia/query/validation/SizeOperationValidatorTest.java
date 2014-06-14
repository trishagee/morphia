package org.mongodb.morphia.query.validation;

import org.junit.Test;
import org.mongodb.morphia.entities.EntityWithListsAndArrays;
import org.mongodb.morphia.mapping.MappedClass;
import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.mapping.Mapper;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mongodb.morphia.query.FilterOperator.EQUAL;
import static org.mongodb.morphia.query.FilterOperator.SIZE;

public class SizeOperationValidatorTest {
    @Test
    public void shouldRejectOperatorThatIsNotSize() {
        // given
        MappedClass mappedClass = new MappedClass(EntityWithListsAndArrays.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("list");

        // expect
        assertThat(SizeOperationValidator.validate(mappedField, EQUAL, 1), is(false));
    }

    @Test
    public void shouldAllowSizeOperatorForListTypesAndIntegerValues() {
        // given
        MappedClass mappedClass = new MappedClass(EntityWithListsAndArrays.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("list");
        
        // expect
        assertThat(SizeOperationValidator.validate(mappedField, SIZE, 3), is(true));
    }

    @Test
    public void shouldAllowSizeOperatorForArraysAndIntegerValues() {
        // given
        MappedClass mappedClass = new MappedClass(EntityWithListsAndArrays.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("array");

        // expect
        assertThat(SizeOperationValidator.validate(mappedField, SIZE, 3), is(true));
    }

    @Test
    public void shouldAllowSizeOperatorForArrayListTypesAndIntegerValues() {
        // given
        MappedClass mappedClass = new MappedClass(EntityWithListsAndArrays.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("arrayList");

        // expect
        assertThat(SizeOperationValidator.validate(mappedField, SIZE, 3), is(true));
    }

    @Test
    public void shouldNotAllowSizeOperatorForNonListTypes() {
        // given
        MappedClass mappedClass = new MappedClass(EntityWithListsAndArrays.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("notAnArrayOrList");

        // expect
        assertThat(SizeOperationValidator.validate(mappedField, SIZE, 3), is(false));
    }

    @Test
    public void shouldNotAllowSizeOperatorForNonIntegerValues() {
        // given
        MappedClass mappedClass = new MappedClass(EntityWithListsAndArrays.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("list");

        // expect
        assertThat(SizeOperationValidator.validate(mappedField, SIZE, "value"), is(false));
    }
}