package org.mongodb.morphia.converters;


import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.mongodb.morphia.mapping.MappedField;


/**
 * Convert to an ObjectId from string
 *
 * @author scotthernandez
 */
public class ObjectIdConverter extends TypeConverter implements SimpleValueConverter {

    /**
     * Creates the Converter.
     */
    public ObjectIdConverter() {
        super(ObjectId.class);
    }

    @Override
    public Object decode(final Class targetClass, @NotNull final Object val, final MappedField optionalExtraInfo) {
        if (val instanceof ObjectId) {
            return val;
        }

        return new ObjectId(val.toString());
    }
}
