package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.XWorkTestCase;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StringConverterTest extends XWorkTestCase {

    private Map<String, Object> context = new HashMap<String, Object>();

    public void testDoubleToString() throws Exception {
        // given
        Double d = 10.01;

        StringConverter converter = new StringConverter();
        converter.setLocaleProvider(buildProvider("DE"));

        // when
        Object actual = converter.convertValue(context, null, null, null, d, String.class);

        // then
        assertEquals("10,01", actual);
    }

    private LocaleProvider buildProvider(final String locale) {
        return new LocaleProvider() {
            public Locale getLocale() {
                return new Locale(locale);
            }
        };
    }

}
