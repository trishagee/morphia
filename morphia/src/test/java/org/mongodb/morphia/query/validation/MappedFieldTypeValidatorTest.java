package org.mongodb.morphia.query.validation;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.mongodb.morphia.entities.EntityWithNoId;
import org.mongodb.morphia.entities.SimpleEntity;
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
    public void shouldNotAllowValueWithATypeThatDoesNotMatchTheEntityIdField() {
        // expect
        assertThat(MappedFieldTypeValidator.validate(new Mapper(), SimpleEntity.class, "some non-ObjectId value"), is(false));
    }

    @Test
    public void shouldRejectIfEntityHasNoIdField() {
        // expect
        assertThat(MappedFieldTypeValidator.validate(new Mapper(), EntityWithNoId.class, "some non-null value"), is(false));
    }

}