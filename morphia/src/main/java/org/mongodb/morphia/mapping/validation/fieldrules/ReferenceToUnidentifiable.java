package org.mongodb.morphia.mapping.validation.fieldrules;


import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;
import org.mongodb.morphia.mapping.MappedClass;
import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.mapping.MappedFieldImpl;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.mapping.MappingException;
import org.mongodb.morphia.mapping.validation.ConstraintViolation;
import org.mongodb.morphia.mapping.validation.ConstraintViolation.Level;

import java.util.Set;


/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class ReferenceToUnidentifiable extends FieldConstraint {

    @Override
    protected void check(final Mapper mapper, final MappedClass mc, final MappedField mappedField, final
    Set<ConstraintViolation> ve) {
        if (mappedField.hasAnnotation(Reference.class)) {
            final Class realType;
            if (mappedField instanceof MappedFieldImpl
                && ((MappedFieldImpl) mappedField).isSingleValue()) {
                realType = mappedField.getType();
            } else {
                realType = mappedField.getSubClass();
            }

            if (realType == null) {
                throw new MappingException("Type is null for this MappedField: " + mappedField);
            }

            if ((!realType.isInterface() && mapper.getMappedClass(realType).getIdField() == null)) {
                ve.add(new ConstraintViolation(Level.FATAL, mc, mappedField, getClass(),
                                               mappedField.getFullName() + " is annotated as a @" + Reference.class.getSimpleName() + " but the "
                                               + mappedField.getType().getName()
                                               + " class is missing the @" + Id.class.getSimpleName() + " annotation"));
            }
        }
    }

}
