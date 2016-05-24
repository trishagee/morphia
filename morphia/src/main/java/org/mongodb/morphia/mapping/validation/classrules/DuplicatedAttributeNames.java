package org.mongodb.morphia.mapping.validation.classrules;


import org.mongodb.morphia.mapping.MappedClass;
import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.mapping.validation.ClassConstraint;
import org.mongodb.morphia.mapping.validation.ConstraintViolation;
import org.mongodb.morphia.mapping.validation.ConstraintViolation.Level;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
            ve.addAll(mappedField.getLoadNames().stream()
                                 .filter(name -> !foundNames.add(name))
                                 .map(name -> new ConstraintViolation(FATAL, mc, mappedField, getClass(),
                                                                      "Mapping to MongoDB field name '" + name
                                                                      + "' is duplicated; you cannot map different "
                                                                      + "java fields to the same MongoDB field."))
                                 .collect(toList()));
        }
    }
}
