package org.mongodb.morphia.geo;

import com.mongodb.DBObject;
import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.mapping.Mapper;

import java.util.ArrayList;
import java.util.List;

public class MultiPolygonConverter extends TypeConverter implements SimpleValueConverter {
    private final GeoConverter geoConverter;

    public MultiPolygonConverter() {
        super(MultiPolygon.class);
        List<GeoConverter.GeometryFactory> factories = new ArrayList<GeoConverter.GeometryFactory>();
        factories.add(new MultiPolygonFactory());
        factories.add(new PolygonFactory());
        factories.add(new LineStringFactory());
        factories.add(new PointFactory());
        geoConverter = new GeoConverter(GeoJsonType.MULTI_POLYGON, factories);
    }

    @Override
    public void setMapper(final Mapper mapper) {
        super.setMapper(mapper);
        geoConverter.setMapper(mapper);
    }

    @Override
    public Object encode(final Object value, final MappedField optionalExtraInfo) {
        return geoConverter.encode(((MultiPolygon) value).getPolygons(), optionalExtraInfo);
    }

    @Override
    @SuppressWarnings("unchecked") //always going to have unchecked warnings casting from the raw objects
    public Object decode(final Class<?> targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
        return geoConverter.decode(targetClass, ((DBObject) fromDBObject).get("coordinates"), optionalExtraInfo);
    }

    private class MultiPolygonFactory implements GeoConverter.GeometryFactory<Polygon> {
        @Override
        public Geometry createGeometry(final List<Polygon> polygons) {
            return new MultiPolygon(polygons);
        }
    }

    private class LineStringFactory implements GeoConverter.GeometryFactory<Point> {
        @Override
        public Geometry createGeometry(final List<Point> objects) {
            return new LineString(objects);
        }
    }

    private class PointFactory implements GeoConverter.GeometryFactory<Double> {
        @Override
        public Geometry createGeometry(final List<Double> coordinates) {
            return new Point(coordinates);
        }
    }

    private class PolygonFactory implements GeoConverter.GeometryFactory<LineString> {
        @Override
        public Geometry createGeometry(final List<LineString> boundaries) {
            return new Polygon(boundaries);
        }
    }
}
