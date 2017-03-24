package org.mongodb.morphia.mapping;

import org.mongodb.morphia.logging.Logger;
import org.mongodb.morphia.logging.MorphiaLoggerFactory;
import org.mongodb.morphia.utils.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;

public class MappedFieldFactory {
    private static final Logger LOG = MorphiaLoggerFactory.get(MappedFieldFactory.class);

    public static MappedField create(final Field f, final Class<?> clazz, final Mapper mapper) {
        MappedFieldImpl mappedField = new MappedFieldImpl(f, clazz, mapper);
        return discover(mapper, mappedField);
    }

    /**
     * Discovers interesting (that we care about) things about the field.
     */
    private static MappedField discover(final Mapper mapper, MappedFieldImpl mappedField) {
        for (final Class<? extends Annotation> clazz : mappedField.INTERESTING) {
            mappedField.addAnnotation(clazz);
        }

        //type must be discovered before the constructor.
        mappedField.discoverType(mapper);
        mappedField.discoverConstructor();
        return discoverMultivalued(mappedField, mapper);
    }

    private static MappedField discoverMultivalued(MappedFieldImpl mappedField, Mapper mapper) {
        if (mappedField.realType.isArray()
            || GenericArrayType.class.isAssignableFrom(mappedField.genericType.getClass())) {
            return new ArrayMappedField(mappedField, mapper);
        }
        else if (Collection.class.isAssignableFrom(mappedField.realType)
                 || Map.class.isAssignableFrom(mappedField.realType)) {
            mappedField.isSingleValue = false;

            mappedField.isMap = Map.class.isAssignableFrom(mappedField.realType);
            mappedField.isSet = Set.class.isAssignableFrom(mappedField.realType);
            //for debugging
            mappedField.isCollection = Collection.class.isAssignableFrom(mappedField.realType);

            // get the subtype T, T[]/List<T>/Map<?,T>; subtype of Long[], List<Long> is Long
            mappedField.subType = (mappedField.realType.isArray()) ? mappedField.realType
                    .getComponentType() : ReflectionUtils
                    .getParameterizedType(mappedField.getField(), mappedField.isMap() ? 1 : 0);

            if (mappedField.isMap) {
                mappedField.mapKeyType = ReflectionUtils.getParameterizedType(mappedField.getField(),
                                                                              0);
            }
        }
        setMongoType(mappedField, mapper);
        return mappedField;
    }

    private static void setMongoType(MappedFieldImpl mappedField, Mapper mapper) {
        boolean isMongoType = ReflectionUtils.isPropertyType(mappedField.realType);
        mappedField.isMongoType = isMongoType;

        if (!isMongoType && mappedField.subType != null) {
            mappedField.isMongoType = ReflectionUtils.isPropertyType(mappedField.subType);
        }

        if (!isMongoType && !mappedField.isSingleValue()
            && (mappedField.getSubType() == null || mappedField.getSubType() == Object.class)) {
            if (LOG.isWarningEnabled() && !mapper.getConverters().hasDbObjectConverter(mappedField)) {
                LOG.warning(format("The multi-valued field '%s' is a possible " +
                                   "heterogeneous collection. It cannot be verified. "
                                   + "Please declare a valid type to get rid of this warning. " +
                                   "%s", mappedField.getFullName(), mappedField.subType));
            }
            mappedField.isMongoType = true;
        }
    }
}
