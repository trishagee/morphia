package org.mongodb.morphia.converters;


import org.jetbrains.annotations.NotNull;
import org.mongodb.morphia.mapping.MappedField;

import java.sql.Timestamp;
import java.util.Date;


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
    public Date decode(final Class<Date> targetClass, final Object val, final MappedField optionalExtraInfo) {
        final Date d = super.decode(targetClass, val, optionalExtraInfo);
        return new Timestamp(d.getTime());
    }

    @Override
    public Object encode(@NotNull final Date val, final MappedField optionalExtraInfo) {
        return val == null ? null : new Date(val.getTime());
    }
}
