package org.mongodb.morphia.query.validation;

import org.junit.Test;

import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PatternValueTypeValidatorTest {
    @Test
    public void shouldAllowValueOfPatternWithTypeOfString() {
        // expect
        assertThat(PatternValueTypeValidator.validate(String.class, Pattern.compile(".")), is(true));
    }

    @Test
    public void shouldNotAllowNonStringTypeWithValueOfPattern() {
        // expect
        assertThat(PatternValueTypeValidator.validate(Pattern.class, Pattern.compile(".")), is(false));
    }
}