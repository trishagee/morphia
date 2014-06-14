package org.mongodb.morphia.query.validation;

import com.mongodb.DBObject;
import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.query.FilterOperator;

public final class GeoWithinOperationValidator implements OperationValidator {

    private GeoWithinOperationValidator() { }

    public static boolean validate(final MappedField mf, final FilterOperator operator, final Object value) {
        if (operator.equals(FilterOperator.GEO_WITHIN)
            && (isArrayAndContainsNumericValues(mf) || isIterableAndContainsNumericValues(mf))) {
            return isValueAValidGeoQuery(value);
        }
        return false;
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
}