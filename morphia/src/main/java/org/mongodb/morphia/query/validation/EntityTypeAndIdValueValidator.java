package org.mongodb.morphia.query.validation;

import org.mongodb.morphia.mapping.MappedClass;
import org.mongodb.morphia.mapping.Mapper;

import java.util.List;

import static java.lang.String.format;

public final class EntityTypeAndIdValueValidator implements Validator {
    private static final EntityTypeAndIdValueValidator INSTANCE = new EntityTypeAndIdValueValidator();
    private EntityTypeAndIdValueValidator() {
    }
    //TODO: I think this should be possible with the MappedField, not the type

    /**
     * Checks the class of the {@code value} against the type of the ID for the {@code type}.  Always applies this validation, 
     * but there's room to change this to not apply it if, for example, the type is not an entity.
     *
     * @param mapper             the mapper
     * @param type               a non-null Class
     * @param value              the value for the query
     * @param validationFailures the list to add any failures to. If validation passes or {@code appliesTo} returned false, this list will
     *                           not change.
     * @return true if the validation was applied.
     */
    public boolean apply(final Mapper mapper, final Class<?> type, final Object value, final List<ValidationFailure> validationFailures) {
        MappedClass mappedClassForType = mapper.getMappedClass(type);
        if (mappedClassForType.getMappedIdField() == null) {
            validationFailures.add(new ValidationFailure(format("Type should be from a class with an ID field. "
                                                                + "Type was %s and its class was a %s", type,
                                                                mappedClassForType.getClazz().getCanonicalName()
                                                               )));
            return true;
        }
        Class<?> classOfValue = value.getClass();
        Class classOfIdFieldForType = mappedClassForType.getMappedIdField().getConcreteType();
        if (!classOfValue.equals(classOfIdFieldForType)) {
            validationFailures.add(new ValidationFailure(format("The value class needs to match the type of ID for the field. "
                                                                + "Value was %s and was a %s and the ID of the type was %s",
                                                                value, classOfValue, classOfIdFieldForType
                                                               )));
        }
        return true;
    }

    /**
     * Get the instance.
     *
     * @return the Singleton instance of this validator
     */
    public static EntityTypeAndIdValueValidator getInstance() {
        return INSTANCE;
    }
}
