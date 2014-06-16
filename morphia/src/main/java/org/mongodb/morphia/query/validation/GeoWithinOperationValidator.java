package org.mongodb.morphia.query.validation;

import com.mongodb.DBObject;
import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.query.FilterOperator;

import java.util.List;

import static java.lang.String.format;
import static org.mongodb.morphia.query.FilterOperator.GEO_WITHIN;

public enum GeoWithinOperationValidator implements OperationValidator {
    INSTANCE;

    @Override
    public boolean apply(final MappedField mappedField, final FilterOperator operator, final Object value,
                         final List<ValidationFailure> validationFailures) {
        if (appliesTo(operator)) {
            validate(mappedField, value, validationFailures);
            return true;
        }
        return false;
    }

    public static void validate(final MappedField mappedField, final Object value,
                                final List<ValidationFailure> validationFailures) {
        if (!isArrayAndContainsNumericValues(mappedField) && !isIterableAndContainsNumericValues(mappedField)) {
            validationFailures.add(new ValidationFailure(format("For a $geoWithin operation, if field '%s' is an array or iterable it "
                                                                + "should have numeric values. Instead it had: %s",
                                                                mappedField, mappedField.getSubClass()
                                                               )));

        }
        if (!isValueAValidGeoQuery(value)) {
            validationFailures.add(new ValidationFailure(format("For a $geoWithin operation, the value should be a valid geo query. "
                                                                + "Instead it was: %s",
                                                                value
                                                               )));
        }
    }

    private static boolean isArrayAndContainsNumericValues(final MappedField mf) {
        Class subClass = mf.getSubClass();
        return mf.getType().isArray()
               && (subClass == int.class || subClass == long.class || subClass == double.class || subClass == float.class);
    }

    private static boolean isIterableAndContainsNumericValues(final MappedField mf) {
        return Iterable.class.isAssignableFrom(mf.getType()) && Number.class.isAssignableFrom(mf.getSubClass());
    }

    // this could be a lot more rigorous
    private static boolean isValueAValidGeoQuery(final Object value) {
        if (value instanceof DBObject) {
            String key = ((DBObject) value).keySet().iterator().next();
            return key.equals("$box") || key.equals("$center") || key.equals("$centerSphere") || key.equals("$polygon");
        }
        return false;
    }

    private static boolean appliesTo(final FilterOperator operator) {
        return operator.equals(GEO_WITHIN);
    }
}