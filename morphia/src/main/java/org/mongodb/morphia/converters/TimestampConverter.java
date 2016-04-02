package org.mongodb.morphia.converters;


import org.mongodb.morphia.mapping.MappedField;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;


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
    public Object encode(final Optional<Date> val, final MappedField optionalExtraInfo) {
        return val.map(timestamp -> new Date(timestamp.getTime()))
                  .orElse(null);
    }
}
