package org.mongodb.morphia.converters;

import org.jetbrains.annotations.NotNull;
import org.mongodb.morphia.mapping.MappedField;

import java.util.Locale;
import java.util.Optional;

/**
 * Converts a Locale to/from a valid database structure.
 */
public class LocaleConverter extends TypeConverter<Locale> implements SimpleValueConverter {

    /**
     * Creates the Converter.
     */
    public LocaleConverter() {
        super(Locale.class);
    }

    @Override
    public Locale decode(final Class<Locale> targetClass,
                         final Object fromDBObject, final MappedField optionalExtraInfo) {
        return parseLocale(fromDBObject.toString());
    }

    @Override
    public Optional<String> encode(@NotNull final Locale val, final MappedField optionalExtraInfo) {
        return Optional.of(val.toString());
    }

    Locale parseLocale(final String localeString) {
        if ((localeString != null) && (localeString.length() != 0)) {
            final int index = localeString.indexOf("_");
            final int index2 = localeString.indexOf("_", index + 1);
            Locale resultLocale;
            if (index == -1) {
                resultLocale = new Locale(localeString);
            } else if (index2 == -1) {
                resultLocale = new Locale(localeString.substring(0, index), localeString.substring(index + 1));
            } else {
                resultLocale = new Locale(
                                             localeString.substring(0, index),
                                             localeString.substring(index + 1, index2),
                                             localeString.substring(index2 + 1));

            }
            return resultLocale;
        }

        return null;
    }
}
