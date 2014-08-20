package org.mongodb.morphia.geo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

import java.util.ArrayList;
import java.util.List;

public class MultiLineStringConverter extends TypeConverter implements SimpleValueConverter {
    private final PointListConverter pointListConverter = new PointListConverter();

    public MultiLineStringConverter() {
        super(MultiLineString.class);
    }

    @Override
    public Object encode(final Object value, final MappedField optionalExtraInfo) {
        MultiLineString multiLineString = (MultiLineString) value;
        List<LineString> lineStrings = multiLineString.getLineStrings();
        List<Object> encodedLineStrings = new ArrayList<Object>();
        for (final LineString lineString : lineStrings) {
            encodedLineStrings.add(pointListConverter.encode(lineString, optionalExtraInfo));
        }
        return new BasicDBObject("type", GeoJsonType.MULTI_LINE_STRING.getType())
               .append("coordinates", encodedLineStrings);
    }

    @Override
    @SuppressWarnings("unchecked") //always going to have unchecked warnings casting from the raw objects
    public Object decode(final Class<?> targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
        DBObject dbObject = (DBObject) fromDBObject;
        List<LineString> lineStrings = new ArrayList();
        List<List> coordinates = (List<List>) dbObject.get("coordinates");
        for (final List coordinate : coordinates) {
            Object listOfPoints = pointListConverter.decode(targetClass, coordinate, optionalExtraInfo);
            lineStrings.add(new LineString((List<Point>) listOfPoints));
        }
        return new MultiLineString(lineStrings);
    }
}
