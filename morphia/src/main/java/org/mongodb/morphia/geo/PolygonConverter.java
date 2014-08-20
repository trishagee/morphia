package org.mongodb.morphia.geo;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked") //TODO this needs fixing
public class PolygonConverter extends TypeConverter implements SimpleValueConverter {

    public PolygonConverter() {
        super(Polygon.class);
    }

    @Override
    public Object encode(final Object value, final MappedField optionalExtraInfo) {
        Polygon polygon = (Polygon) value;
        BasicDBObject dbObject = new BasicDBObject("type", GeoJsonType.POLYGON.getType());
        BasicDBList dbList = new BasicDBList();

        dbList.add(getMapper().getConverters().encode(polygon.getExteriorBoundary().getPoints()));

        List<Polygon.PolygonBoundary> polygonBoundaries = new ArrayList<Polygon.PolygonBoundary>();
        polygonBoundaries.add(polygon.getExteriorBoundary());
        polygonBoundaries.addAll(polygon.getInteriorBoundaries());

        Object coordinates = getMapper().getConverters().encode(polygonBoundaries);
        dbObject.append("coordinates", coordinates);
        return dbObject;

    }

    @Override
    public Object decode(final Class<?> targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
        DBObject dbObject = (DBObject) fromDBObject;
        List<Polygon.PolygonBoundary> list = new ArrayList();
        List<List> coordinates = (List<List>) dbObject.get("coordinates");
        for (List coordinate : coordinates) {
            Object decoded = getMapper().getConverters().decode(Polygon.PolygonBoundary.class, coordinate, optionalExtraInfo);
            list.add((Polygon.PolygonBoundary) decoded);
        }
        return new Polygon(list);
    }
}
