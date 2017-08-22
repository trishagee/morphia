package org.mongodb.morphia.query;

import org.jetbrains.annotations.NotNull;
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
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

final class QueryValidator {
    private static final Logger LOG = MorphiaLoggerFactory.get(QueryValidator.class);

    private QueryValidator() {
    }

    /**
     * Validate the path, and value type, returning the mapped field for the field at the path
     */
    static ValidatedField validateQuery(final Class clazz, final Mapper mapper, String propertyName, final FilterOperator op,
                                        final Object val, final boolean validateNames, final boolean validateTypes) {
        final ValidatedField validatedField = new ValidatedField(propertyName);

        if (!propertyName.startsWith("$")) {
            final String[] pathElements = propertyName.split("\\.");
            final List<String> databasePathElements = new ArrayList<>(asList(pathElements));
            if (clazz == null) {
                return validatedField;
            }

            MappedClass mc = mapper.getMappedClass(clazz);

            for (int i = 0; ; ) {
                final String fieldName = pathElements[i];
                boolean fieldIsArrayOperator = fieldName.equals("$");

                final Optional<MappedField> mf = getMappedField(fieldName, mc, databasePathElements,
                                                                i, propertyName, validateNames,
                                                                fieldIsArrayOperator);
                validatedField.mappedField = mf;

                i++;
                if (mf.isPresent() && mf.get().isMap()) {
                    //skip the map key validation, and move to the next fieldName
                    i++;
                }

                if (i >= pathElements.length) {
                    break;
                }

                if (!fieldIsArrayOperator) {
                    //catch people trying to search/update into @Reference/@Serialized fields
                    if (validateNames && !canQueryPast(mf.get())) {
                        throw new ValidationException(format("Cannot use dot-notation past '%s' in '%s'; found while"
                                                             + " validating - %s", fieldName, mc.getClazz().getName(), propertyName));
                    }

                    if (!mf.isPresent() && mc.isInterface()) {
                        break;
                    } else if (!mf.isPresent()) {
                        throw new ValidationException(format("The field '%s' could not be found in '%s'", propertyName, mc.getClazz().getName()));
                    }
                    //get the next MappedClass for the next field validation
                    MappedField mappedField = mf.get();
                    mc = mapper.getMappedClass((mappedField.isSingleValue()) ? mappedField.getType() : mappedField.getSubClass());
                }
            }

            //record new property string
            validatedField.databasePath = databasePathElements.stream().collect(joining("."));

            if (validateTypes && validatedField.mappedField.isPresent()) {
                MappedField mappedField = validatedField.mappedField.get();
                List<ValidationFailure> typeValidationFailures = new ArrayList<>();
                boolean compatibleForType = isCompatibleForOperator(mc, mappedField, mappedField.getType(), op, val, typeValidationFailures);
                List<ValidationFailure> subclassValidationFailures = new ArrayList<>();
                boolean compatibleForSubclass = isCompatibleForOperator(mc, mappedField, mappedField.getSubClass(), op, val, subclassValidationFailures);

                if ((mappedField.isSingleValue() && !compatibleForType)
                    || mappedField.isMultipleValues() && !(compatibleForSubclass || compatibleForType)) {

                    if (LOG.isWarningEnabled()) {
                        LOG.warning(format("The type(s) for the query/update may be inconsistent; using an instance of type '%s' "
                                           + "for the field '%s.%s' which is declared as '%s'", val.getClass().getName(),
                                           mappedField.getDeclaringClass().getName(), mappedField.getJavaFieldName(), mappedField.getType().getName()
                                          ));
                        typeValidationFailures.addAll(subclassValidationFailures);
                        LOG.warning("Validation warnings: \n" + typeValidationFailures);
                    }
                }
            }
        }
        return validatedField;
    }

    private static Optional<MappedField> getMappedField(String fieldName, MappedClass mc,
                                                        List<String> databasePathElements,
                                                        int index, String fieldPath,
                                                        boolean validateNames,
                                                        boolean fieldIsArrayOperator) {
        Optional<MappedField> mf = mc.getMappedField(fieldName);

        //translate from java field name to stored field name
        if (!mf.isPresent() && !fieldIsArrayOperator) {
            mf = mc.getMappedFieldByJavaField(fieldName);
            if (validateNames && !mf.isPresent()) {
                throw fieldNotFoundException(fieldPath, mc, fieldName);
            }
            mf.ifPresent(mappedField -> databasePathElements.set(index, mappedField.getNameToStore()));
        }
        return mf;
    }

    @NotNull
    private static ValidationException fieldNotFoundException(String prop, MappedClass mc, String
            part) {
        return new ValidationException(format("The field '%s' could not be found in '%s' while validating - %s; if "
                                              + "you wish to continue please disable validation.", part,
                                              mc.getClazz().getName(), prop
                                            ));
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

    static class ValidatedField {
        private Optional<MappedField> mappedField = Optional.empty();
        private String databasePath;

        public ValidatedField(String databasePath) {
            this.databasePath = databasePath;
        }

        public MappedField getMappedField() {
            return mappedField.orElse(null);
        }

        public String getDatabasePath() {
            return databasePath;
        }
    }
}
