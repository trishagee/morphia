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
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

final class QueryValidator {
    private static final Logger LOG = MorphiaLoggerFactory.get(QueryValidator.class);

    private QueryValidator() {
    }

    /**
     * Validate the path, and value type, returning the mapped field for the field at the path
     */
    @Nullable
    static MappedField validateQuery(@Nullable final Class clazz, @NotNull final Mapper mapper,
                                     @NotNull final StringBuilder origProp, final FilterOperator op,
                                     final Object val, final boolean validateNames,
                                     final boolean validateTypes) {
        if (clazz == null) {
            return null;
        }
        final String path = origProp.toString();
        Optional<MappedField> mf = Optional.empty();

        if (!isOperator(path)) {
            final String[] parts = path.split("\\.");
            MappedClass mc = mapper.getMappedClass(clazz);

            int i = 0;
            while (i < parts.length) {
                final String fieldName = parts[i];
                final ValidationExceptionFactory exceptionFactory = new ValidationExceptionFactory(fieldName, mc.getClazz().getName(), path);
                if (isArrayOperator(fieldName)) {
                    // ignore and move on
                    i++;
                    continue;
                }

                mf = mc.getMappedField(fieldName);

                //translate from java field name to stored field name
                if (!mf.isPresent()) {
                    mf = getMappedFieldFromJavaName(validateNames, parts, mc, i, fieldName,
                                                    exceptionFactory);
                }

                if (mf.isPresent() && mf.get().isMap()) {
                    //skip the map key validation, and move to the next part
                    i += 2;
                    continue;
                }
                i++;

                if (i >= parts.length) {
                    continue;
                }

                //catch people trying to search/update into @Reference/@Serialized fields
                if (validateNames && !canQueryPast(mf.get())) {
                    exceptionFactory.throwQueryingReferenceFieldsException();
                }

                if (!mf.isPresent() && mc.isInterface()) {
                    break;
                }
                if (!mf.isPresent()) {
                    exceptionFactory.throwFieldNotFoundException();
                }
                //get the next MappedClass for the next field validation
                MappedField mappedField = mf.get();
                mc = mapper.getMappedClass((mappedField.isSingleValue()) ? mappedField.getType() : mappedField.getSubClass());
            }

            // NASTY: Using a parameter as an output. Setting the StringBuffer to include any
            // translations, e.g. MongoDB property names vs Java property names
            origProp.setLength(0); // clear existing content
            origProp.append(stream(parts).collect(joining(".")));

            if (validateTypes && mf.isPresent()) {
                validateTypes(op, val, mc, mf.get());
            }
        }
        return mf.orElse(null);
    }

    @NotNull
    private static Optional<MappedField> getMappedFieldFromJavaName(boolean validateNames, String[] parts, MappedClass mc, int i, String fieldName, ValidationExceptionFactory exceptionFactory) {
        Optional<MappedField> mf = mc.getMappedFieldByJavaField(fieldName);
        if (validateNames && !mf.isPresent()) {
            exceptionFactory.throwFieldNotFoundException();
        }
        mf.ifPresent(mappedField -> parts[i] = mappedField.getNameToStore());
        return mf;
    }

    private static void validateTypes(FilterOperator operator, Object value, MappedClass mc, MappedField mappedField) {
        List<ValidationFailure> validationFailures = new ArrayList<>();
        boolean compatibleForType = isCompatibleForOperator(mc, mappedField, mappedField.getType(), operator, value, validationFailures);
        boolean compatibleForSubclass = isCompatibleForOperator(mc, mappedField, mappedField.getSubClass(), operator, value, validationFailures);

        if ((mappedField.isSingleValue() && !compatibleForType)
            || mappedField.isMultipleValues() && !(compatibleForSubclass || compatibleForType)) {

            if (LOG.isWarningEnabled()) {
                LOG.warning(format("The type(s) for the query/update may be inconsistent; using an instance of type '%s' "
                                   + "for the field '%s.%s' which is declared as '%s'", value.getClass().getName(),
                                   mappedField.getDeclaringClass().getName(), mappedField.getJavaFieldName(), mappedField.getType().getName()
                                  ));
                LOG.warning("Validation warnings: \n" + validationFailures);
            }
        }
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
                                    || ListValueValidator.getInstance().apply(type, value, validationFailures)
                                    || EntityTypeAndIdValueValidator.getInstance()
                                                                    .apply(mappedClass, mappedField, value, validationFailures)
                                    || DefaultTypeValidator.getInstance().apply(type, value, validationFailures);

        return validationApplied && validationFailures.size() == 0;
    }

}
