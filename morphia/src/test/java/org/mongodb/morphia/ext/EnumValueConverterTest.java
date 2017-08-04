package org.mongodb.morphia.ext;

import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import org.mongodb.morphia.TestBase;
import org.mongodb.morphia.annotations.Converters;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

import java.util.Optional;

/**
 * Example converter which stores the enum value instead of string (name)
 *
 * @author scotthernandez
 */
public class EnumValueConverterTest extends TestBase {

    @Test
    public void testEnum() {
        final EnumEntity ee = new EnumEntity();
        getDs().save(ee);
        final DBObject dbObj = getDs().getCollection(EnumEntity.class).findOne();
        Assert.assertEquals(1, dbObj.get("val"));
    }

    private enum AEnum {
        One,
        Two

    }

    private static class AEnumConverter extends TypeConverter<AEnum> implements SimpleValueConverter {
        public AEnumConverter() {
            super(AEnum.class);
        }

        @Override
        public AEnum decode(final Class targetClass, @NotNull final Object fromDBObject, final MappedField optionalExtraInfo) {
            return AEnum.values()[(Integer) fromDBObject];
        }

        @Override
        public Optional<?> encode(@NotNull final AEnum value, final MappedField optionalExtraInfo) {
            return Optional.of(value.ordinal());
        }
    }

    @Converters(AEnumConverter.class)
    private static class EnumEntity {
        @Id
        private ObjectId id = new ObjectId();
        private AEnum val = AEnum.Two;

    }
}
