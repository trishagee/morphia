package org.mongodb.morphia.converters;

import com.mongodb.DBRef;
import org.jetbrains.annotations.NotNull;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.mapping.MappedField;

import java.util.Optional;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 * @author scotthernandez
 */
public class KeyConverter extends TypeConverter {

    /**
     * Creates the Converter.
     */
    public KeyConverter() {
        super(Key.class);
    }

    @Override
    public Object decode(final Class targetClass, final Object o, final MappedField optionalExtraInfo) {
        if (o == null) {
            return null;
        }
        if (!(o instanceof DBRef)) {
            throw new ConverterException(String.format("cannot convert %s to Key because it isn't a DBRef", o.toString()));
        }

        return getMapper().refToKey((DBRef) o);
    }

    @Override
    public Optional<DBRef> encode(@NotNull final Object t, final MappedField optionalExtraInfo) {
        return Optional.ofNullable(getMapper().keyToDBRef((Key) t));
    }

}
