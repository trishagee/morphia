package org.mongodb.morphia.converters;


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
    public Date decode(final Class targetClass, final Object val, final MappedField optionalExtraInfo) {
        final Date d = (Date) super.decode(targetClass, val, optionalExtraInfo);
        return new Timestamp(d.getTime());
    }

    @Override
    public Object encode(final Date val, final MappedField optionalExtraInfo) {
        return val == null ? null : new Date(((Timestamp) val).getTime());
    }
}
