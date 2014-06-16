package org.mongodb.morphia.query.validation;

import com.mongodb.DBObject;
import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.query.FilterOperator;

import java.util.List;

import static java.lang.String.format;
import static org.mongodb.morphia.query.FilterOperator.GEO_WITHIN;

/**
 * Supports validation for queries using the {@code FilterOperator.GEO_WITHIN} operator.
 */
public final class GeoWithinOperationValidator extends OperationValidator {
    private static final GeoWithinOperationValidator INSTANCE = new GeoWithinOperationValidator();

    private GeoWithinOperationValidator() {
    }

    @Override
    protected void validate(final MappedField mappedField, final Object value,
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

    @Override
    protected FilterOperator getOperator() {
        return GEO_WITHIN;
    }

    /**
     * Get the instance.
     *
     * @return the Singleton instance of this validator
     */
    public static GeoWithinOperationValidator getInstance() {
        return INSTANCE;
    }
}