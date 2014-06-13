package org.mongodb.morphia.query.validation;

import com.mongodb.DBObject;
import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.query.FilterOperator;

import static java.util.Arrays.asList;

//TODO: Trish - needs simplification
public final class GeoWithinOperationValidator implements OperationValidator {

    private GeoWithinOperationValidator() {
    }

    @SuppressWarnings("unchecked")
    public static boolean validate(final MappedField mf, final Class<?> type, final FilterOperator operator,
                                   final Object value) {
        if (operator.equals(FilterOperator.GEO_WITHIN)
            && (type.isArray() || Iterable.class.isAssignableFrom(type))
            && (mf.getSubType() instanceof Number || asList(int.class,
                                                            long.class,
                                                            double.class,
                                                            float.class).contains(mf.getSubType()))) {
            if (value instanceof DBObject) {
                String key = ((DBObject) value).keySet().iterator().next();
                return key.equals("$box") || key.equals("$center") || key.equals("$centerSphere") || key.equals("$polygon");
            }
        }
        return false;
    }
}