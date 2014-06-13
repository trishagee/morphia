package org.mongodb.morphia.query.validation;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.entities.SimpleEntity;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class KeyValueTypeValidatorTest {
    @Test
    public void shouldAllowKeyTypeWhenValueIsAKey() {
        // expect
        assertThat(KeyValueTypeValidator.validate(Integer.class, new Key<Number>(Integer.class, new ObjectId())), is(true));
    }

    @Test
    public void shouldNotAllowNonKeyTypeWhenValueIsAKey() {
        // expect
        assertThat(KeyValueTypeValidator.validate(String.class, new Key<Number>(Integer.class, new ObjectId())), is(false));
    }

    @Test
    public void shouldNotAllowTypeThatDoesNotMatchTheKindClassInAKey() {
        // expect
        assertThat(KeyValueTypeValidator.validate(SimpleEntity.class, new Key<String>("kind", new ObjectId())), is(false));
    }

}