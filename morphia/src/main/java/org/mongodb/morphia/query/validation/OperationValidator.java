package org.mongodb.morphia.query.validation;

import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.query.FilterOperator;

import java.util.List;

/**
 * Implement this interface to provide a way of validating part of a query that contains a {@code FilterOperator}.
 */
public interface OperationValidator extends Validator {
    /**
     * Apply validation for the given operator.  If the operator does not match the operator required by the implementing class, then this
     * method will return false to show validation was not applied.  If the operator is the one being validated, this method will return
     * true, and any failures in validation will be added to the list of {@code validationFailures}.
     *
     * @param mappedField
     * @param operator           any FilterOperator for a query
     * @param value              the value to apply the operator to
     * @param validationFailures the list to add any failures to. If validation passes or the operator is not an $exists operator, this list
     *                           will not change.
     * @return true if validation was applied, false if this validation doesn't apply to this operator.
     */
    boolean apply(final MappedField mappedField, final FilterOperator operator, final Object value,
                  final List<ValidationFailure> validationFailures);

}
