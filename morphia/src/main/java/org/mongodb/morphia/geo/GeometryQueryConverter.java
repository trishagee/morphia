package org.mongodb.morphia.geo;

import com.mongodb.BasicDBObject;
import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.mapping.Mapper;

import java.util.Optional;

/**
 * Converts Point objects into DBObjects for querying only.  When saving entities with Points in, this converter should not be used.
 */
public class GeometryQueryConverter extends TypeConverter<Geometry> implements SimpleValueConverter {

    /**
     * Create a new converter.  Registers itself to convert Point classes.
     *
     * @param mapper the Mapper is required as this converter delegates other type encoding back to the mapper
     */
    public GeometryQueryConverter(final Mapper mapper) {
        super.setMapper(mapper);
    }

    @Override
    public Geometry decode(final Class<Geometry> targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
        throw new UnsupportedOperationException("Should never have to decode a query object");
    }

    @Override
    public Object encode(final Optional<Geometry> value, final MappedField optionalExtraInfo) {
        Object encode = getMapper().getConverters().encode(value.get());
        return new BasicDBObject("$geometry", encode);
    }
}
