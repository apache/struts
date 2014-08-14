package com.opensymphony.xwork2.conversion.string;

import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.conversion.InternalConverter;

import java.text.NumberFormat;

public class StringDoubleConverter implements InternalConverter<String> {

    private LocaleProvider provider;

    public StringDoubleConverter(LocaleProvider provider) {
        this.provider = provider;
    }

    public boolean canConvert(Object value) {
        return value.getClass() == Double.class || value.getClass() == double.class;
    }

    public String convert(Object value) {
        Double doubleValue = (Double) value;
        NumberFormat format = NumberFormat.getInstance(provider.getLocale());
        return format.format(doubleValue);
    }

}
