package org.mongodb.morphia.converters;


import org.jetbrains.annotations.NotNull;
import org.mongodb.morphia.mapping.MappedField;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;


/**
 * @author scotthernandez
 */
public class TimestampConverter extends DateConverter {

    /**
     * Creates the Converter.
     */
    public TimestampConverter() {
        super(Timestamp.class);
    }

    @Override
    public Date decode(final Class<Date> targetClass, @NotNull final Object val, final MappedField
            optionalExtraInfo) {
        final Date d = super.decode(targetClass, val, optionalExtraInfo);
        return new Timestamp(d.getTime());
    }

    @Override
    public Optional<Date> encode(@NotNull final Date val, final MappedField optionalExtraInfo) {
        return Optional.of(new Date(val.getTime()));
    }
}
