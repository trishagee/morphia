package org.mongodb.morphia.query;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mongodb.morphia.annotations.Serialized;
import org.mongodb.morphia.logging.Logger;
import org.mongodb.morphia.logging.MorphiaLoggerFactory;
import org.mongodb.morphia.mapping.MappedClass;
import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.validation.AllOperationValidator;
import org.mongodb.morphia.query.validation.DefaultTypeValidator;
import org.mongodb.morphia.query.validation.DoubleTypeValidator;
import org.mongodb.morphia.query.validation.EntityAnnotatedValueValidator;
import org.mongodb.morphia.query.validation.EntityTypeAndIdValueValidator;
import org.mongodb.morphia.query.validation.ExistsOperationValidator;
import org.mongodb.morphia.query.validation.GeoWithinOperationValidator;
import org.mongodb.morphia.query.validation.InOperationValidator;
import org.mongodb.morphia.query.validation.IntegerTypeValidator;
import org.mongodb.morphia.query.validation.KeyValueTypeValidator;
import org.mongodb.morphia.query.validation.ListValueValidator;
import org.mongodb.morphia.query.validation.LongTypeValidator;
import org.mongodb.morphia.query.validation.ModOperationValidator;
import org.mongodb.morphia.query.validation.NotInOperationValidator;
import org.mongodb.morphia.query.validation.PatternValueValidator;
import org.mongodb.morphia.query.validation.SizeOperationValidator;
import org.mongodb.morphia.query.validation.ValidationFailure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

final class QueryValidator {
    private static final Logger LOG = MorphiaLoggerFactory.get(QueryValidator.class);

    private QueryValidator() {
    }

    /**
     * Validate the path, and value type, returning the mapped field for the field at the path
     */
    @NotNull
    static ValidatedField validateQuery(@Nullable final Class clazz, @NotNull final Mapper mapper,
                                        @NotNull final String propertyPath, final boolean validateNames) {
        final ValidatedField returnValue = new ValidatedField();
        final FieldName fieldName = new FieldName(propertyPath);
        returnValue.fieldName = fieldName;
        if (clazz == null) {
            return returnValue;
        }
        Optional<MappedField> mf;

        if (!isOperator(propertyPath)) {
            MappedClass mc = mapper.getMappedClass(clazz);
            returnValue.mappedClass = mc;

            while (fieldName.hasMoreElements()) {
                final String name = fieldName.nextElement();
                final ValidationExceptionFactory exceptionFactory = new ValidationExceptionFactory(name, mc.getClazz().getName(), propertyPath);
                if (isArrayOperator(name)) {
                    // ignore and move on
                    continue;
                }

                mf = mc.getMappedField(name);

                //translate from java field name to stored field name
                if (!mf.isPresent()) {
                    mf = getMappedFieldFromJavaName(validateNames, fieldName, mc, name, exceptionFactory);
                }
                returnValue.mappedField = mf.orElse(null);

                if (mf.isPresent() && mf.get().isMap()) {
                    //skip the map key validation, and move to the next part
                    if (fieldName.hasMoreElements()) {
                        fieldName.nextElement();
                    }
                    continue;
                }
                if (fieldName.hasMoreElements()) {
                    // look ahead to the next element, some stuff can only be validated from the
                    // context of the current element

                    //catch people trying to search/update into @Reference/@Serialized fields
                    if (validateNames && !canQueryPast(mf.get())) {
                        exceptionFactory.throwQueryingReferenceFieldsException();
                    }
                    //get the next MappedClass for the next field validation
                    if (mf.isPresent()) {
                        MappedField mappedField = mf.get();
                        mc = mapper.getMappedClass((mappedField.isSingleValue()) ? mappedField.getType() : mappedField.getSubClass());
                    }
                }
            }
        }
        return returnValue;
    }

    static void validateTypes(@NotNull ValidatedField validatedField, FilterOperator operator,
                              Object value) {
        List<ValidationFailure> validationFailures = new ArrayList<>();
        final MappedField mappedField = validatedField.mappedField;
        final MappedClass mc = validatedField.mappedClass;
        if (mappedField != null) {
            boolean compatibleForType = isCompatibleForOperator(mc, mappedField, mappedField.getType(), operator, value, validationFailures);
            boolean compatibleForSubclass = isCompatibleForOperator(mc, mappedField, mappedField.getSubClass(), operator, value, validationFailures);

            if ((mappedField.isSingleValue() && !compatibleForType)
                || mappedField.isMultipleValues() && !(compatibleForSubclass || compatibleForType)) {

                if (LOG.isWarningEnabled()) {
                    LOG.warning(format("The type(s) for the query/update may be inconsistent; " +
                                       "using an instance of type '%s' "
                                       + "for the field '%s.%s' which is declared as '%s'", value.getClass().getName(),
                                       mappedField.getDeclaringClass().getName(), mappedField.getJavaFieldName(), mappedField.getType().getName()
                    ));
                    LOG.warning("Validation warnings: \n" + validationFailures);
                }
            }
        }
    }

    @NotNull
    private static Optional<MappedField> getMappedFieldFromJavaName(boolean validateNames,
                                                                    FieldName fieldName,
                                                                    MappedClass mc,
                                                                    String javaFieldName,
                                                                    ValidationExceptionFactory exceptionFactory) {
        Optional<MappedField> mf = mc.getMappedFieldByJavaField(javaFieldName);
        if (validateNames && !mf.isPresent()) {
            exceptionFactory.throwFieldNotFoundException();
        }
        mf.ifPresent(mappedField -> fieldName.setMongoName(mappedField.getNameToStore()));
        return mf;
    }

    private static boolean isArrayOperator(@NotNull final String propertyName) {
        return propertyName.equals("$");
    }

    private static boolean isOperator(@NotNull final String propertyName) {
        return propertyName.startsWith("$");
    }

    private static boolean canQueryPast(@NotNull final MappedField mf) {
        return !(mf.isReference() || mf.hasAnnotation(Serialized.class));
    }

    /*package*/
    static boolean isCompatibleForOperator(final MappedClass mappedClass, final MappedField mappedField, final Class<?> type,
                                           final FilterOperator op,
                                           final Object value, final List<ValidationFailure> validationFailures) {
        // TODO: it's really OK to have null values?  I think this is to prevent null pointers further down,
        // but I want to move the null check into the operations that care whether they allow nulls or not.
        if (value == null || type == null) {
            return true;
        }

        boolean validationApplied = ExistsOperationValidator.getInstance().apply(mappedField, op, value, validationFailures)
                                    || SizeOperationValidator.getInstance().apply(mappedField, op, value, validationFailures)
                                    || InOperationValidator.getInstance().apply(mappedField, op, value, validationFailures)
                                    || NotInOperationValidator.getInstance().apply(mappedField, op, value, validationFailures)
                                    || ModOperationValidator.getInstance().apply(mappedField, op, value, validationFailures)
                                    || GeoWithinOperationValidator.getInstance().apply(mappedField, op, value, validationFailures)
                                    || AllOperationValidator.getInstance().apply(mappedField, op, value, validationFailures)
                                    || KeyValueTypeValidator.getInstance().apply(type, value, validationFailures)
                                    || IntegerTypeValidator.getInstance().apply(type, value, validationFailures)
                                    || LongTypeValidator.getInstance().apply(type, value, validationFailures)
                                    || DoubleTypeValidator.getInstance().apply(type, value, validationFailures)
                                    || PatternValueValidator.getInstance().apply(type, value, validationFailures)
                                    || EntityAnnotatedValueValidator.getInstance().apply(type, value, validationFailures)
                                    || ListValueValidator.getInstance().apply(type, value,
                                                                              validationFailures)
                                    || EntityTypeAndIdValueValidator.getInstance()
                                                                    .apply(mappedClass,
                                                                           mappedField, value,
                                                                           validationFailures)
                                    || DefaultTypeValidator.getInstance().apply(type, value, validationFailures);

        return validationApplied && validationFailures.size() == 0;
    }

    static class ValidatedField {
        @Nullable
        private MappedField mappedField;
        private MappedClass mappedClass;
        private FieldName fieldName;

        @Nullable
        public MappedField getMappedField() {
            return mappedField;
        }

        public String getStoredFieldName() {
            return fieldName.getMongoName();
        }
    }

    private static class FieldName implements Enumeration<String> {
        private final String[] javaObjectFieldTokens;
        private final List<String> mongoFieldTokens;
        private final String path;
        private int cursor = 0;

        public FieldName(String path) {
            this.path = path;
            javaObjectFieldTokens = path.split("\\.");
            mongoFieldTokens = new ArrayList<>(Arrays.asList(javaObjectFieldTokens));
        }

        public void setMongoName(String nameToStore) {
            mongoFieldTokens.set(cursor - 1, nameToStore);
        }

        public boolean hasMoreElements() {
            return cursor < javaObjectFieldTokens.length;
        }

        public String nextElement() {
            return javaObjectFieldTokens[cursor++];
        }

        public String getMongoName() {
            return mongoFieldTokens.stream().collect(joining("."));
        }
    }
}
