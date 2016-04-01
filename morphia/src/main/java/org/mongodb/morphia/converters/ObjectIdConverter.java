package org.mongodb.morphia.converters;


import org.bson.types.ObjectId;
import org.mongodb.morphia.mapping.MappedField;


/**
 * Convert to an ObjectId from string
 *
 * @author scotthernandez
 */
public class ObjectIdConverter extends TypeConverter<ObjectId> implements SimpleValueConverter {

    /**
     * Creates the Converter.
     */
    public ObjectIdConverter() {
        super(ObjectId.class);
    }

    @Override
    public ObjectId decode(final Class targetClass, final Object val, final MappedField optionalExtraInfo) {
        if (val instanceof ObjectId) {
            return (ObjectId) val;
        }

        return new ObjectId(val.toString());
    }
}
