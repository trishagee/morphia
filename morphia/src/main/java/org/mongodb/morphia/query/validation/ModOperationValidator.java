package org.mongodb.morphia.query.validation;

import org.mongodb.morphia.query.FilterOperator;
import org.mongodb.morphia.query.ValidationException;
import org.mongodb.morphia.utils.ReflectionUtils;

import java.lang.reflect.Array;

// see http://docs.mongodb.org/manual/reference/operator/query/mod/
public final class ModOperationValidator implements OperationValidator {
    private ModOperationValidator() {
    }

    public static boolean validate(final FilterOperator operator, final Object value) {
        //TODO: Trish - This needs simplification, and I don't want to use Exceptions for logic
        if (operator.equals(FilterOperator.MOD) && value.getClass().isArray()) {
            if (!ReflectionUtils.isIntegerType(Array.get(value, 0).getClass())) {
                throw new ValidationException("Array needs to contain integers for MOD, but contained " + Array.get(value, 0).getClass());
            } else {
                return true;
            }
        }
        return false;
    }

    //    { field: { $mod: [ divisor, remainder ] } }
    //    Changed in version 2.6: The $mod operator errors when passed an array with fewer or more elements. In previous versions, 
    // if passed an array with one element, the $mod operator uses 0 as the remainder value, and if passed an array with more than two 
    // elements, the $mod ignores all but the first two elements. Previous versions do return an error when passed an empty array. See 
    // Not Enough Elements Error and Too Many Elements Error for details.
}
