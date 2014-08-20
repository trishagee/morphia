package org.mongodb.morphia.geo;

import com.mongodb.DBObject;
import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

import java.util.List;

public class MultiLineStringConverter extends TypeConverter implements SimpleValueConverter {
    private final PointListConverter pointListConverter = new PointListConverter();
    private final IteratingConverter iteratingConverter = new IteratingConverter(pointListConverter, 
                                                                                 GeoJsonType.MULTI_LINE_STRING,
                                                                                 LineString.class);

    public MultiLineStringConverter() {
        super(MultiLineString.class);
    }

    @Override
    public Object encode(final Object value, final MappedField optionalExtraInfo) {
        return iteratingConverter.encode(((MultiLineString) value).getLineStrings(), optionalExtraInfo);
    }

    @Override
    @SuppressWarnings("unchecked") //always going to have unchecked warnings casting from the raw objects
    public Object decode(final Class<?> targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
        DBObject dbObject = (DBObject) fromDBObject;
        return new MultiLineString(iteratingConverter.decode((List<List>) dbObject.get("coordinates"), optionalExtraInfo));
    }
}
