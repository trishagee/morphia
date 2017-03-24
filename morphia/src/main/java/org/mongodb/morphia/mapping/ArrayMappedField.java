package org.mongodb.morphia.mapping;

import com.mongodb.DBObject;
import org.mongodb.morphia.logging.Logger;
import org.mongodb.morphia.logging.MorphiaLoggerFactory;
import org.mongodb.morphia.utils.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class ArrayMappedField implements MappedField{
    private static final Logger LOG = MorphiaLoggerFactory.get(ArrayMappedField.class);

    private final Type subType;
    private final boolean isSingleValue = false;
    private boolean isMongoType = false;
    private boolean isArray = true;
    private final boolean isMultipleVales = true;
    private MappedFieldImpl mappedField;

    ArrayMappedField(MappedFieldImpl mappedField, Mapper mapper) {
        this.mappedField = mappedField;

        Class realType = mappedField.realType;
        isArray = realType.isArray();

        // get the subtype T, T[]/List<T>/Map<?,T>; subtype of Long[], List<Long> is Long
        if (realType.isArray()) {
            subType = realType.getComponentType();
        } else {
            subType = ReflectionUtils
                    .getParameterizedType(mappedField.getField(), 0);
        }

        isMongoType = ReflectionUtils.isPropertyType(realType);

        if (!isMongoType && subType != null) {
            isMongoType = ReflectionUtils.isPropertyType(subType);
        }

        if (!isMongoType
            && (subType == null || subType == Object.class)) {
            if (LOG.isWarningEnabled() && !mapper.getConverters().hasDbObjectConverter(this)) {
                LOG.warning(String.format("The multi-valued field '%s' is a possible " +
                                          "heterogeneous collection. It cannot be verified. "
                                   + "Please declare a valid type to get rid of this warning. %s", getFullName(), subType));
            }
            isMongoType = true;
        }
    }

    /**
     * @return true if the MappedField is an array
     */
    public boolean isArray() {
        return isArray;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> clazz) {
        return mappedField.getAnnotation(clazz);
    }

    @Override
    public Map<Class<? extends Annotation>, Annotation> getAnnotations() {
        return mappedField.getAnnotations();
    }

    @Override
    public Constructor getCTor() {
        return mappedField.getCTor();
    }

    @Override
    public Class getConcreteType() {
        return mappedField.getConcreteType();
    }

    @Override
    public Object getDbObjectValue(DBObject dbObj) {
        return mappedField.getDbObjectValue(dbObj);
    }

    @Override
    public Class getDeclaringClass() {
        return mappedField.getDeclaringClass();
    }

    @Override
    public Field getField() {
        return mappedField.getField();
    }

    @Override
    public Object getFieldValue(Object instance) {
        return mappedField.getFieldValue(instance);
    }

    @Override
    public String getFullName() {
        return mappedField.getFullName();
    }

    @Override
    public String getJavaFieldName() {
        return mappedField.getJavaFieldName();
    }

    @Override
    public String getNameToStore() {
        return mappedField.getNameToStore();
    }

    @Override
    public boolean isTransient() {
        return mappedField.isTransient();
    }

    @Override
    public boolean isTypeMongoCompatible() {
        return isMongoType;
    }

    @Override
    public Class getType() {
        return mappedField.getType();
    }

    @Override
    public boolean hasAnnotation(Class ann) {
        return mappedField.hasAnnotation(ann);
    }

    @Override
    public void setFieldValue(Object instance, Object value) {
        mappedField.setFieldValue(instance, value);
    }

    @Override
    public List<String> getLoadNames() {
        return mappedField.getLoadNames();
    }

    @Override
    public Class getSubClass() {
        return mappedField.toClass(subType);
    }

    @Override
    public Type getSubType() {
        return subType;
    }
}
