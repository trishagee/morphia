package org.mongodb.morphia.query.validation;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TypeValidatorTest {
    @Test
    public void shouldAcceptListTypes() {
        // expect
        assertThat(TypeValidator.typeIsAListOrArray(List.class), is(true));
    }

    @Test
    public void shouldAcceptArrayListTypes() {
        // expect
        assertThat(TypeValidator.typeIsAListOrArray(ArrayList.class), is(true));
    }

    @Test
    public void shouldAcceptArrayTypes() {
        // expect
        assertThat(TypeValidator.typeIsAListOrArray(int[].class), is(true));
    }

    @Test
    public void shouldRejectIterablesThatAreNotListOrArray() {
        // expect
        assertThat(TypeValidator.typeIsAListOrArray(Set.class), is(false));
    }

    @Test
    public void shouldRejectOtherTypes() {
        // expect
        assertThat(TypeValidator.typeIsAListOrArray(String.class), is(false));
    }

    @Test
    public void shouldAllowIterableTypesThatAreNotListsAndRejectOtherTypes() {
        // expect
        assertThat(TypeValidator.typeIsIterable(Set.class), is(true));
        assertThat(TypeValidator.typeIsIterable(Map.class), is(false));
        assertThat(TypeValidator.typeIsIterable(int[].class), is(false));
    }

    @Test
    public void shouldAllowMapTypesAndRejectOtherTypes() {
        // given 
        assertThat(TypeValidator.typeIsMap(HashMap.class), is(true));
        assertThat(TypeValidator.typeIsMap(Map.class), is(true));

        assertThat(TypeValidator.typeIsMap(Set.class), is(false));
        assertThat(TypeValidator.typeIsMap(List.class), is(false));
        assertThat(TypeValidator.typeIsMap(int[].class), is(false));
    }

}