package org.mongodb.morphia.query;

import org.mongodb.morphia.annotations.Reference;
import org.mongodb.morphia.annotations.Serialized;
import org.mongodb.morphia.logging.Logger;
import org.mongodb.morphia.logging.MorphiaLoggerFactory;
import org.mongodb.morphia.mapping.MappedClass;
import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.validation.AllOperationValidator;
import org.mongodb.morphia.query.validation.EntityAnnotatedValueValidator;
import org.mongodb.morphia.query.validation.ExistsOperationValidator;
import org.mongodb.morphia.query.validation.GeoWithinOperationValidator;
import org.mongodb.morphia.query.validation.InOperationValidator;
import org.mongodb.morphia.query.validation.IntegerOrLongValueTypeValidator;
import org.mongodb.morphia.query.validation.IntegerValueTypeValidator;
import org.mongodb.morphia.query.validation.KeyValueTypeValidator;
import org.mongodb.morphia.query.validation.ListValueValidator;
import org.mongodb.morphia.query.validation.ModOperationValidator;
import org.mongodb.morphia.query.validation.NotInOperationValidator;
import org.mongodb.morphia.query.validation.PatternValueTypeValidator;
import org.mongodb.morphia.query.validation.SizeOperationValidator;

import static java.lang.String.format;

//TODO: Trisha - this really needs a test, if only to document what it's doing
final class QueryValidator {
    private static final Logger LOG = MorphiaLoggerFactory.get(QueryValidator.class);

    private QueryValidator() {
    }

    @SuppressWarnings("unchecked")
    /*package*/ static boolean isCompatibleForOperator(final MappedField mf, final Class<?> type, final FilterOperator op,
                                                       final Object value) {
        try {
            if (value == null || type == null) {
                return true;
            } else if (ExistsOperationValidator.validate(op, value)) {
                return true;
            } else if (SizeOperationValidator.validate(type, op, value)) {
                return true;
            } else if (InOperationValidator.validate(op, value)) {
                return true;
            } else if (NotInOperationValidator.validate(op, value)) {
                return true;
            } else if (ModOperationValidator.validate(op, value)) {
                return true;
            } else if (GeoWithinOperationValidator.validate(mf, type, op, value)) {
                return true;
            } else if (AllOperationValidator.validate(op, value)) {
                return true;
            } else if (IntegerValueTypeValidator.validate(type, value)) {
                return true;
            } else if (IntegerOrLongValueTypeValidator.validate(type, value)) {
                return true;
            } else if (PatternValueTypeValidator.validate(type, value)) {
                return true;
            } else if (EntityAnnotatedValueValidator.validate(type, value)) {
                return true;
            } else if (KeyValueTypeValidator.validate(type, value)) {
                return true;
            } else if (ListValueValidator.validator(value)) {
                return true;
            } else if (mf.getMapper().getMappedClass(type) != null && mf.getMapper().getMappedClass(type).getMappedIdField() != null
                       && value.getClass().equals(mf.getMapper().getMappedClass(type).getMappedIdField().getConcreteType())) {
                return true;
            } else if (!value.getClass().isAssignableFrom(type) && !value.getClass()
                                                                         .getSimpleName()
                                                                         .equalsIgnoreCase(type.getSimpleName())) {
                return false;
            }
            return true;
        } catch (final ValidationException e) {
            return false;
        }
    }

    /**
     * Validate the path, and value type, returning the mapped field for the field at the path
     */
    static MappedField validateQuery(final Class clazz, final Mapper mapper, final StringBuilder origProp, final FilterOperator op,
                                     final Object val, final boolean validateNames, final boolean validateTypes) {
        //TODO: cache validations (in static?).

        MappedField mf = null;
        final String prop = origProp.toString();
        boolean hasTranslations = false;

        if (validateNames) {
            final String[] parts = prop.split("\\.");
            if (clazz == null) {
                return null;
            }

            MappedClass mc = mapper.getMappedClass(clazz);
            //CHECKSTYLE:OFF
            for (int i = 0; ; ) {
                //CHECKSTYLE:ON
                final String part = parts[i];
                mf = mc.getMappedField(part);

                //translate from java field name to stored field name
                if (mf == null) {
                    mf = mc.getMappedFieldByJavaField(part);
                    if (mf == null) {
                        throw new ValidationException(format("The field '%s' could not be found in '%s' while validating - %s; if "
                                                             + "you wish to continue please disable validation.", part,
                                                             clazz.getName(), prop
                                                            ));
                    }
                    hasTranslations = true;
                    parts[i] = mf.getNameToStore();
                }

                i++;
                if (mf.isMap()) {
                    //skip the map key validation, and move to the next part
                    i++;
                }

                //catch people trying to search/update into @Reference/@Serialized fields
                if (i < parts.length && !canQueryPast(mf)) {
                    throw new ValidationException(format("Can not use dot-notation past '%s' could not be found in '%s' while"
                                                         + " validating - %s", part, clazz.getName(), prop));
                }

                if (i >= parts.length) {
                    break;
                }
                //get the next MappedClass for the next field validation
                mc = mapper.getMappedClass((mf.isSingleValue()) ? mf.getType() : mf.getSubClass());
            }

            //record new property string if there has been a translation to any part
            if (hasTranslations) {
                origProp.setLength(0); // clear existing content
                origProp.append(parts[0]);
                for (int i = 1; i < parts.length; i++) {
                    origProp.append('.');
                    origProp.append(parts[i]);
                }
            }

            if (validateTypes) {
                boolean compatibleForType = isCompatibleForOperator(mf, mf.getType(), op, val);
                boolean compatibleForSubclass = isCompatibleForOperator(mf, mf.getSubClass(), op, val);

                if ((mf.isSingleValue() && !compatibleForType)
                    || mf.isMultipleValues() && !(compatibleForSubclass || compatibleForType)) {

                    if (LOG.isWarningEnabled()) {
                        String className = val == null ? "null" : val.getClass().getName();
                        LOG.warning(format("The type(s) for the query/update may be inconsistent; using an instance of type '%s' "
                                           + "for the field '%s.%s' which is declared as '%s'", className,
                                           mf.getDeclaringClass().getName(), mf.getJavaFieldName(), mf.getType().getName()
                                          ));
                    }
                }
            }
        }
        return mf;
    }

    private static boolean canQueryPast(final MappedField mf) {
        return !(mf.hasAnnotation(Reference.class) || mf.hasAnnotation(Serialized.class));
    }

}
