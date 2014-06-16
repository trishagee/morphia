package org.mongodb.morphia.query.validation;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.mongodb.morphia.entities.EntityWithListsAndArrays;
import org.mongodb.morphia.entities.EntityWithNoId;
import org.mongodb.morphia.entities.SimpleEntity;
import org.mongodb.morphia.mapping.MappedClass;
import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.mapping.Mapper;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MappedFieldTypeValidatorTest {
    @Test
    public void shouldAllowTypeThatIsAMappedEntityAndAValueWithSameClassAsIdOfMappedEntity() {
        // expect
        assertThat(MappedFieldTypeValidator.validate(new Mapper(), SimpleEntity.class, new ObjectId()), is(true));
    }

    @Test
    public void shouldRejectValueWithATypeThatDoesNotMatchTheEntityIdFieldType() {
        // expect
        assertThat(MappedFieldTypeValidator.validate(new Mapper(), SimpleEntity.class, "some non-ObjectId value"), is(false));
    }

    @Test
    public void shouldRejectIfEntityHasNoIdField() {
        // expect
        assertThat(MappedFieldTypeValidator.validate(new Mapper(), EntityWithNoId.class, "some non-null value"), is(false));
    }

    @Test
    public void shouldAllowArraysOfNumbers() {
        // given 
        MappedClass mappedClass = new MappedClass(EntityWithListsAndArrays.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("arrayOfInts");

        // expect
        assertThat(MappedFieldTypeValidator.isArrayOfNumbers(mappedField), is(true));
    }

    @Test
    public void shouldRejectArraysOfStrings() {
        // given 
        MappedClass mappedClass = new MappedClass(EntityWithListsAndArrays.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("arrayOfStrings");

        // expect
        assertThat(MappedFieldTypeValidator.isArrayOfNumbers(mappedField), is(false));
    }

    @Test
    public void shouldAllowAListThatDoesNotContainNumbers() {
        // given 
        MappedClass mappedClass = new MappedClass(EntityWithListsAndArrays.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("listOfIntegers");

        // expect
        assertThat(MappedFieldTypeValidator.isIterableOfNumbers(mappedField), is(true));
    }

    @Test
    public void shouldRejectAListThatDoesNotContainNumbers() {
        // given 
        MappedClass mappedClass = new MappedClass(EntityWithListsAndArrays.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("listOfStrings");

        // expect
        assertThat(MappedFieldTypeValidator.isIterableOfNumbers(mappedField), is(false));
    }

}