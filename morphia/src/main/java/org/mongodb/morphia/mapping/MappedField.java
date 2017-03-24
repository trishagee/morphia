package org.mongodb.morphia.mapping;

import com.mongodb.DBObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public interface MappedField {
    @SuppressWarnings("unchecked")
    <T extends Annotation> T getAnnotation(Class<T> clazz);

    Map<Class<? extends Annotation>, Annotation> getAnnotations();

    Constructor getCTor();

    Class getConcreteType();

    Object getDbObjectValue(DBObject dbObj);

    Class getDeclaringClass();

    Field getField();

    Object getFieldValue(Object instance);

    String getFullName();

    String getJavaFieldName();

    String getNameToStore();

    boolean isTransient();

    boolean isTypeMongoCompatible();

    Class getType();

    boolean hasAnnotation(Class ann);

    void setFieldValue(Object instance, Object value);

    List<String> getLoadNames();

    Class getSubClass();

    Type getSubType();

}
