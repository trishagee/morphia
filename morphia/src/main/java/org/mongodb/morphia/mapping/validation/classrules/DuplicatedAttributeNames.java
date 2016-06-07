package org.mongodb.morphia.mapping.validation.classrules;


import org.mongodb.morphia.mapping.MappedClass;
import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.mapping.validation.ClassConstraint;
import org.mongodb.morphia.mapping.validation.ConstraintViolation;

import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.mongodb.morphia.mapping.validation.ConstraintViolation.Level.FATAL;


/**
 * @author josephpachod
 */
public class DuplicatedAttributeNames implements ClassConstraint {

    @Override
    public void check(final Mapper mapper, final MappedClass mc, final Set<ConstraintViolation> ve) {
        final Set<String> foundNames = new HashSet<>();
        for (final MappedField mappedField : mc.getPersistenceFields()) {
            // TODO: I suspect a re-think of this approach would lead to further code simplification
            ve.addAll(mappedField.getLoadNames().stream()
                                 .filter(name -> !foundNames.add(name))
                                 .map(name -> new ConstraintViolation(FATAL, mc, mappedField, getClass(),
                                         "Mapping to MongoDB field name '" + name
                                                 + "' is duplicated; you cannot map different java fields to the same MongoDB field."))
                                 .collect(toList()));
        }
    }
}
