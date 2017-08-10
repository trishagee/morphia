package org.mongodb.morphia.query;

import com.mongodb.BasicDBObject;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.annotations.Reference;
import org.mongodb.morphia.annotations.Serialized;
import org.mongodb.morphia.entities.EntityWithListsAndArrays;
import org.mongodb.morphia.entities.SimpleEntity;
import org.mongodb.morphia.mapping.MappedClass;
import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.testmodel.Rectangle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mongodb.morphia.query.FilterOperator.ALL;
import static org.mongodb.morphia.query.FilterOperator.EQUAL;
import static org.mongodb.morphia.query.FilterOperator.EXISTS;
import static org.mongodb.morphia.query.FilterOperator.GEO_WITHIN;
import static org.mongodb.morphia.query.FilterOperator.IN;
import static org.mongodb.morphia.query.FilterOperator.MOD;
import static org.mongodb.morphia.query.FilterOperator.NOT_IN;
import static org.mongodb.morphia.query.FilterOperator.SIZE;
import static org.mongodb.morphia.query.QueryValidator.validateQuery;
import static org.mongodb.morphia.query.QueryValidator.validateTypes;

public class QueryValidatorTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldAllowAllOperatorForIterableMapAndArrayValues() {
        // expect
        assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, ALL, Arrays.asList(1, 2),
                                                          new ArrayList<>()), is(true));
        assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, ALL, Collections.emptySet(),
                                                          new ArrayList<>()), is(true));
        assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, ALL, new HashMap<String, String>(),
                                                          new ArrayList<>()), is(true));
        assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, ALL, new int[0],
                                                          new ArrayList<>()), is(true));
    }

    // All of the following tests are whitebox, as I have retrofitted them afterwards.  I have no idea if this is the required
    // functionality or not

    @Test
    public void shouldAllowBooleanValuesForExistsOperator() {
        // expect
        assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, EXISTS, true, new ArrayList<>()),
                   is(true));
    }

    @Test
    public void shouldAllowGeoWithinOperatorWithAllAppropriateTrimmings() {
        // expect
        MappedClass mappedClass = new MappedClass(GeoEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("array").orElse(null);
        assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, List.class, GEO_WITHIN, new BasicDBObject("$box", 1),
                                                          new ArrayList<>()), is(true));
    }

    @Test
    public void shouldAllowInOperatorForIterableMapAndArrayValues() {
        // expect
        assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, IN, Arrays.asList(1, 2),
                                                          new ArrayList<>()), is(true));
        assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, IN, Collections.emptySet(),
                                                          new ArrayList<>()), is(true));
        assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, IN, new HashMap<String, String>(),
                                                          new ArrayList<>()), is(true));
        assertThat(QueryValidator.isCompatibleForOperator(null,
                                                          null,
                                                          SimpleEntity.class,
                                                          IN,
                                                          new int[0],
                                                          new ArrayList<>()),
                   is(true));
    }

    @Test
    public void shouldAllowModOperatorForArrayOfIntegerValues() {
        // expect
        assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, MOD, new int[2],
                                                          new ArrayList<>()), is(true));
    }

    @Test
    public void shouldAllowNotInOperatorForIterableMapAndArrayValues() {
        // expect
        assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, NOT_IN, Arrays.asList(1, 2),
                                                          new ArrayList<>()), is(true));
        assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, NOT_IN, Collections.emptySet(),
                                                          new ArrayList<>()), is(true));
        assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, NOT_IN, new HashMap<String, String>(),
                                                          new ArrayList<>()), is(true));
        assertThat(QueryValidator.isCompatibleForOperator(null,
                                                          null, SimpleEntity.class, NOT_IN, new int[0], new ArrayList<>()),
                   is(true));
    }

    @Test
    //this used to fail
    public void shouldAllowSizeOperatorForArrayListTypesAndIntegerValues() {
        // given
        MappedClass mappedClass = new MappedClass(EntityWithListsAndArrays.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("arrayListOfIntegers").orElse(null);

        // expect
        assertThat(QueryValidator.isCompatibleForOperator(mappedClass,
                                                          mappedField,
                                                          NullClass.class,
                                                          SIZE,
                                                          3,
                                                          new ArrayList<>()),
                   is(true));
    }

    @Test
    public void shouldAllowSizeOperatorForArraysAndIntegerValues() {
        // given
        MappedClass mappedClass = new MappedClass(EntityWithListsAndArrays.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("arrayOfInts").orElse(null);

        // expect
        assertThat(QueryValidator.isCompatibleForOperator(mappedClass,
                                                          mappedField,
                                                          NullClass.class,
                                                          SIZE,
                                                          3,
                                                          new ArrayList<>()),
                   is(true));
    }

    @Test
    public void shouldAllowSizeOperatorForListTypesAndIntegerValues() {
        // given
        MappedClass mappedClass = new MappedClass(EntityWithListsAndArrays.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("listOfIntegers").orElse(null);

        // expect
        assertThat(QueryValidator.isCompatibleForOperator(mappedClass,
                                                          mappedField,
                                                          NullClass.class,
                                                          SIZE,
                                                          3,
                                                          new ArrayList<>()),
                   is(true));
    }

    @Test
    public void shouldAllowTypeThatMatchesKeyTypeValue() {
        // expect
        MappedClass mappedClass = new MappedClass(SimpleEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("integer").orElse(null);
        assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, Integer.class, EQUAL,
                                                          new Key<Number>(Integer.class, "Integer", new ObjectId()),
                                                          new ArrayList<>()), is(true));
    }

    @Test
    public void shouldAllowValueOfPatternWithTypeOfString() {
        // expect
        assertThat(QueryValidator.isCompatibleForOperator(null, null, String.class, EQUAL, Pattern.compile("."),
                                                          new ArrayList<>()), is(true));
    }

    @Test
    public void shouldAllowValueWithEntityAnnotationAndTypeOfKey() {
        // expect
        assertThat(QueryValidator.isCompatibleForOperator(null, null, Key.class, EQUAL, new SimpleEntity(),
                                                          new ArrayList<>()), is(true));
    }

    @Test
    public void shouldAllowValuesOfIntegerIfTypeIsDouble() {
        // expect
        assertThat(QueryValidator.isCompatibleForOperator(null, null, Double.class, EQUAL, 1, new ArrayList<>()),
                   is(true));
        assertThat(QueryValidator.isCompatibleForOperator(null, null, double.class, EQUAL, 1, new ArrayList<>()),
                   is(true));
    }

    @Test
    public void shouldAllowValuesOfIntegerIfTypeIsInteger() {
        // expect
        assertThat(QueryValidator.isCompatibleForOperator(null, null, int.class, EQUAL, 1, new ArrayList<>()),
                   is(true));
        assertThat(QueryValidator.isCompatibleForOperator(null, null, Integer.class, EQUAL, 1,
                                                          new ArrayList<>()), is(true));
    }

    @Test
    public void shouldAllowValuesOfIntegerOrLongIfTypeIsLong() {
        // expect
        assertThat(QueryValidator.isCompatibleForOperator(null, null, Long.class, EQUAL, 1, new ArrayList<>()), is(true));
        assertThat(QueryValidator.isCompatibleForOperator(null, null, long.class, EQUAL, 1, new ArrayList<>()), is(true));
        assertThat(QueryValidator.isCompatibleForOperator(null, null, Long.class, EQUAL, 1L, new ArrayList<>()), is(true));
        assertThat(QueryValidator.isCompatibleForOperator(null, null, long.class, EQUAL, 1L, new ArrayList<>()), is(true));
    }

    @Test
    public void shouldAllowValuesOfList() {
        // expect
        MappedClass mappedClass = new MappedClass(SimpleEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("name").orElse(null);
        assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, List.class, EQUAL, new ArrayList<String>(),
                                                          new ArrayList<>()), is(true));
    }

    @Test
    public void shouldAllowValuesOfLongIfTypeIsDouble() {
        // expect
        assertThat(QueryValidator.isCompatibleForOperator(null, null, Double.class, EQUAL, 1L, new ArrayList<>()),
                   is(true));
        assertThat(QueryValidator.isCompatibleForOperator(null, null, double.class, EQUAL, 1L, new ArrayList<>()),
                   is(true));
    }

    @Test
    public void shouldBeCompatibleIfTypeIsNull() {
        // expect
        // frankly not sure we should just let nulls through
        assertThat(QueryValidator.isCompatibleForOperator(null, null, null, EQUAL, "value", new ArrayList<>()), is(true));
    }

    @Test
    public void shouldBeCompatibleIfValueIsNull() {
        // expect
        // frankly not sure we should just let nulls through
        assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, EQUAL, null, new ArrayList<>()),
                   is(true));
    }

    @Test
    public void shouldNotAllowGeoOperatorIfValueDoesNotContainCorrectField() {
        // expect
        MappedClass mappedClass = new MappedClass(GeoEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("array").orElse(null);
        assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, List.class, GEO_WITHIN,
                                                          new BasicDBObject("name", "value"), new ArrayList<>()),
                   is(false));
    }

    @Test
    public void shouldNotAllowGeoOperatorIfValueIsNotDBObject() {
        // expect
        MappedClass mappedClass = new MappedClass(GeoEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("array").orElse(null);
        assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, List.class, GEO_WITHIN, "value",
                                                          new ArrayList<>()), is(false));
    }

    @Test
    public void shouldNotAllowGeoWithinWhenValueDoesNotContainKeyword() {
        // expect
        MappedClass mappedClass = new MappedClass(GeoEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("array").orElse(null);
        assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, List.class, GEO_WITHIN,
                                                          new BasicDBObject("notValidKey", 1), new ArrayList<>()),
                   is(false));
    }

    @Test
    //this used to fail
    public void shouldNotAllowModOperatorWithNonArrayValue() {
        assertThat(QueryValidator.isCompatibleForOperator(null, null, String.class, MOD, "value", new ArrayList<>()),
                   is(false));
    }

    @Test
    public void shouldNotAllowModOperatorWithNonIntegerArray() {
        // expect
        assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, MOD, new String[]{"value"},
                                                          new ArrayList<>()), is(false));
    }

    @Test
    public void shouldNotAllowNonBooleanValuesForExistsOperator() {
        // given
        MappedClass mappedClass = new MappedClass(SimpleEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("name").orElse(null);
        assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, SimpleEntity.class, EXISTS, "value",
                                                          new ArrayList<>()), is(false));
    }

    @Test
    public void shouldNotAllowNonIntegerTypeIfValueIsInt() {
        // expect
        MappedClass mappedClass = new MappedClass(SimpleEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("name").orElse(null);
        assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, SimpleEntity.class, EQUAL, 1,
                                                          new ArrayList<>()), is(false));
    }

    @Test
    public void shouldNotAllowNonIntegerValueIfTypeIsInt() {
        // expect
        assertThat(QueryValidator.isCompatibleForOperator(null, null, int.class, EQUAL, "some non int value",
                                                          new ArrayList<>()), is(false));
    }

    @Test
    public void shouldNotAllowNonKeyTypeWithKeyValue() {
        // expect
        MappedClass mappedClass = new MappedClass(EntityWithListsAndArrays.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("listOfIntegers").orElse(null);
        assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, SimpleEntity.class, EQUAL,
                                                          new Key<>(String.class, "kind", new ObjectId()),
                                                          new ArrayList<>()),
                   is(false));
    }

    @Test
    //this used to fail
    public void shouldNotAllowNonStringTypeWithValueOfPattern() {
        // expect
        assertThat(QueryValidator.isCompatibleForOperator(null, null, Pattern.class, EQUAL, Pattern.compile("."),
                                                          new ArrayList<>()), is(false));
    }

    @Test
    public void shouldNotAllowOtherValuesForAllOperator() {
        // given
        MappedClass mappedClass = new MappedClass(SimpleEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("name").orElse(null);

        // expect
        assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, SimpleEntity.class, ALL, "value",
                                                          new ArrayList<>()), is(false));
    }

    @Test
    public void shouldNotAllowOtherValuesForInOperator() {
        // expect
        MappedClass mappedClass = new MappedClass(SimpleEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("name").orElse(null);
        assertThat(QueryValidator.isCompatibleForOperator(mappedClass,
                                                          mappedField,
                                                          String.class,
                                                          IN,
                                                          "value",
                                                          new ArrayList<>()),
                   is(false));
    }

    @Test
    public void shouldNotAllowOtherValuesForNotInOperator() {
        // expect
        MappedClass mappedClass = new MappedClass(SimpleEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("name").orElse(null);
        assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, SimpleEntity.class, NOT_IN, "value",
                                                          new ArrayList<>()), is(false));
    }

    @Test
    public void shouldNotAllowSizeOperatorForNonIntegerValues() {
        // expect
        MappedClass mappedClass = new MappedClass(SimpleEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("name").orElse(null);
        assertThat(QueryValidator.isCompatibleForOperator(mappedClass,
                                                          mappedField,
                                                          ArrayList.class,
                                                          SIZE,
                                                          "value",
                                                          new ArrayList<>()),
                   is(false));
    }

    @Test
    public void shouldNotAllowSizeOperatorForNonListTypes() {
        // given
        MappedClass mappedClass = new MappedClass(EntityWithListsAndArrays.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("notAnArrayOrList").orElse(null);

        // expect
        assertThat(QueryValidator.isCompatibleForOperator(mappedClass,
                                                          mappedField,
                                                          NullClass.class,
                                                          SIZE,
                                                          3,
                                                          new ArrayList<>()),
                   is(false));
    }

    @Test
    public void shouldNotAllowStringValueWithTypeThatIsNotString() {
        // expect
        MappedClass mappedClass = new MappedClass(SimpleEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("name").orElse(null);
        assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, Integer.class, EQUAL, "value",
                                                          new ArrayList<>()), is(false));
    }

    @Test
    public void shouldNotAllowTypeThatDoesNotMatchKeyTypeValue() {
        // expect
        MappedClass mappedClass = new MappedClass(SimpleEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("name").orElse(null);
        assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, String.class, EQUAL,
                                                          new Key<Number>(Integer.class, "Integer", new ObjectId()),
                                                          new ArrayList<>()), is(false));
    }

    @Test
    public void shouldNotAllowValueWithoutEntityAnnotationAndTypeOfKey() {
        // expect
        MappedClass mappedClass = new MappedClass(SimpleEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("name").orElse(null);
        assertThat(QueryValidator.isCompatibleForOperator(mappedClass,
                                                          mappedField,
                                                          Key.class,
                                                          EQUAL,
                                                          "value",
                                                          new ArrayList<>()),
                   is(false));
    }

    @Test
    //this used to fail
    public void shouldNotErrorIfModOperatorIsUsedWithZeroLengthArrayOfIntegerValues() {
        // expect
        assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, MOD, new int[0],
                                                          new ArrayList<>()), is(false));
    }

    @Test
    //this used to fail
    public void shouldNotErrorModOperatorWithArrayOfNullValues() {
        // expect
        assertThat(QueryValidator.isCompatibleForOperator(null, null, SimpleEntity.class, MOD, new String[1],
                                                          new ArrayList<>()), is(false));
    }

    @Test
    public void shouldNotErrorWhenValidateQueryCalledWithNullValue() {
        // this unit test is to drive fixing a null pointer in the logging code.  It's a bit stupid but it's an edge case that wasn't
        // caught.
        // when this is called, don't error
        validateQuery(SimpleEntity.class, new Mapper(), new StringBuilder("name"), true);
    }

    @Test
    public void shouldNotErrorWhenValidateTypesCalledWithNullValue() throws NoSuchFieldException {
        // this unit test is to drive fixing a null pointer in the logging code.  It's a bit stupid but it's an edge case that wasn't
        // caught.
        // when this is called, don't error
        validateTypes(new QueryValidator.ValidatedField(), EQUAL, null);
    }

    @Test
    public void shouldRejectNonDoubleValuesIfTypeIsDouble() {
        // expect
        assertThat(QueryValidator.isCompatibleForOperator(null, null, Double.class, EQUAL, "Not a double",
                                                          new ArrayList<>()), is(false));
    }

    @Test
    public void shouldRejectTypesAndValuesThatDoNotMatch() {
        // expect
        MappedClass mappedClass = new MappedClass(SimpleEntity.class, new Mapper());
        MappedField mappedField = mappedClass.getMappedField("name").orElse(null);
        assertThat(QueryValidator.isCompatibleForOperator(mappedClass, mappedField, String.class, EQUAL, 1,
                                                          new ArrayList<>()), is(false));
    }

    @Test
    public void shouldReferToMappedClassInExceptionWhenFieldNotFound() {
        thrown.expect(ValidationException.class);
        thrown.expectMessage("The field 'notAField' could not be found in 'org.bson.types.ObjectId'");
        validateQuery(SimpleEntity.class, new Mapper(), new StringBuilder("id.notAField"), true);
    }

    @Test
    public void shouldReferToMappedClassInExceptionWhenQueryingPastReferenceField() {
        thrown.expect(ValidationException.class);
        thrown.expectMessage("Cannot use dot-notation past 'reference' in 'org.mongodb.morphia.query.QueryValidatorTest$WithReference'");
        validateQuery(WithReference.class, new Mapper(), new StringBuilder("reference.name"), true);
    }

    @Test
    public void shouldReferToMappedClassInExceptionWhenQueryingPastSerializedField() {
        thrown.expect(ValidationException.class);
        thrown.expectMessage("Cannot use dot-notation past 'serialized' in "
                             + "'org.mongodb.morphia.query.QueryValidatorTest$WithSerializedField'");
        validateQuery(WithSerializedField.class, new Mapper(), new StringBuilder("serialized.name"), true);
    }

    @Test
    public void shouldUpdatePathToMongoDBFieldNames() {
        // when
        final StringBuilder fieldName = new StringBuilder("width");
        validateQuery(Rectangle.class, new Mapper(), fieldName, true);

        // then
        //because fieldname is a really nasty output parameter
        Assert.assertThat(fieldName.toString(), is("w"));
    }

    private static class GeoEntity {
        private final int[] array = {1};
    }

    private static class NullClass {
    }

    private static class WithReference {
        @Reference
        private SimpleEntity reference;
    }

    private static class SerializableClass implements Serializable {
        private String name;
    }

    private static class WithSerializedField {
        @Serialized
        private SerializableClass serialized;
    }

}
