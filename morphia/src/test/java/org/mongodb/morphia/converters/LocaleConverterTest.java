package org.mongodb.morphia.converters;

import org.junit.Test;
import org.mongodb.morphia.TestBase;

import java.util.Locale;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@SuppressWarnings("ConstantConditions")
public class LocaleConverterTest extends TestBase {

    @Test
    public void shouldEncodeAndDecodeBuiltInLocale() throws Exception {
        // given
        LocaleConverter converter = new LocaleConverter();
        Locale expectedLocale = Locale.CANADA_FRENCH;

        // when
        Optional<?> encoded = converter.encode(expectedLocale);
        Locale decodedLocale = converter.decode(Locale.class, encoded.get());

        // then
        assertThat(decodedLocale, is(expectedLocale));
    }

    @Test
    public void shouldEncodeAndDecodeCountryOnlyLocale() {
        // given
        LocaleConverter converter = new LocaleConverter();
        Locale expectedLocale = new Locale("", "FI");

        // when
        Optional<?> encoded = converter.encode(expectedLocale);
        Locale decodedLocale = converter.decode(Locale.class, encoded.get());

        // then
        assertThat(decodedLocale, is(expectedLocale));
    }

    @Test
    public void shouldEncodeAndDecodeCustomLocale() {
        // given
        LocaleConverter converter = new LocaleConverter();
        Locale expectedLocale = new Locale("de", "DE", "bavarian");

        // when
        Optional<?> encoded = converter.encode(expectedLocale);
        Locale decodedLocale = converter.decode(Locale.class, encoded.get());

        // then
        assertThat(decodedLocale, is(expectedLocale));
        assertThat(decodedLocale.getLanguage(), is("de"));
        assertThat(decodedLocale.getCountry(), is("DE"));
        assertThat(decodedLocale.getVariant(), is("bavarian"));
    }

    @Test
    public void shouldEncodeAndDecodeNoCountryLocale() {
        // given
        LocaleConverter converter = new LocaleConverter();
        Locale expectedLocale = new Locale("fi", "", "VAR");

        // when
        Optional<?> encoded = converter.encode(expectedLocale);
        Locale decodedLocale = converter.decode(Locale.class, encoded.get());

        // then
        assertThat(decodedLocale, is(expectedLocale));
    }

    @Test
    public void shouldEncodeAndDecodeNoLanguageLocale() {
        // given
        LocaleConverter converter = new LocaleConverter();
        Locale expectedLocale = new Locale("", "FI", "VAR");

        // when
        Optional<?> encoded = converter.encode(expectedLocale);
        Locale decodedLocale = converter.decode(Locale.class, encoded.get());

        // then
        assertThat(decodedLocale, is(expectedLocale));
    }

    @Test
    public void shouldEncodeAndDecodeSpecialVariantLocale() {
        // given
        LocaleConverter converter = new LocaleConverter();
        Locale expectedLocale = new Locale("fi", "FI", "VAR_SPECIAL");

        // when
        Optional<?> encoded = converter.encode(expectedLocale);
        Locale decodedLocale = converter.decode(Locale.class, encoded.get());

        // then
        assertThat(decodedLocale, is(expectedLocale));
    }
}
