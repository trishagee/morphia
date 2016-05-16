package org.mongodb.morphia.converters;


import org.bson.types.Binary;
import org.jetbrains.annotations.NotNull;
import org.mongodb.morphia.annotations.Serialized;
import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.mapping.MappingException;
import org.mongodb.morphia.mapping.Serializer;

import java.io.IOException;
import java.util.Optional;

import static java.lang.String.format;


/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class SerializedObjectConverter extends TypeConverter {
    @Override
    public Object decode(final Class targetClass, final Object fromDBObject, final MappedField f) {
        if (fromDBObject == null) {
            return null;
        }

        if (!((fromDBObject instanceof Binary) || (fromDBObject instanceof byte[]))) {
            throw new MappingException(format("The stored data is not a DBBinary or byte[] instance for %s ; it is a %s",
                                              f.getFullName(), fromDBObject.getClass().getName()));
        }

        try {
            final boolean useCompression = !f.getAnnotation(Serialized.class).disableCompression();
            return Serializer.deserialize(fromDBObject, useCompression);
        } catch (IOException | ClassNotFoundException e) {
            throw new MappingException("While deserializing to " + f.getFullName(), e);
        }
    }

    @Override
    public Optional encode(@NotNull final Object value, @NotNull final MappedField f) {
        try {
            final boolean useCompression = !f.getAnnotation(Serialized.class).disableCompression();
            return Optional.of(Serializer.serialize(value, useCompression));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected boolean isSupported(final Class c, final MappedField optionalExtraInfo) {
        return optionalExtraInfo != null && (optionalExtraInfo.hasAnnotation(Serialized.class));
    }

}
