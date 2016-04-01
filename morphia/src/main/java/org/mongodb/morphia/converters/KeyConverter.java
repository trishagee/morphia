package org.mongodb.morphia.converters;

import com.mongodb.DBRef;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.mapping.MappedField;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author scotthernandez
 */
public class KeyConverter extends TypeConverter<Key> {

    /**
     * Creates the Converter.
     */
    public KeyConverter() {
        super(Key.class);
    }

    @Override
    public Key decode(final Class targetClass, final Object o, final MappedField optionalExtraInfo) {
        if (!(o instanceof DBRef)) {
            throw new ConverterException(String.format("cannot convert %s to Key because it isn't a DBRef", o.toString()));
        }

        return getMapper().refToKey((DBRef) o);
    }

    @Override
    public Object encode(final Key val, final MappedField optionalExtraInfo) {
        return getMapper().keyToDBRef(val);
    }

}
