package com.opensymphony.xwork2.conversion;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds reference to all internal converters to simplify lookup and registration
 */
public class InternalConverters<T> {

    private final InternalConverter<T> NO_OP_CONVERTER = new InternalConverter<T>() {

        public boolean canConvert(Object value) {
            return false;
        }

        public T convert(Object value) {
            return null;
        }
    };

    private List<InternalConverter<T>> converters = new ArrayList<InternalConverter<T>>();

    public boolean register(InternalConverter<T> converter) {
        return converters.add(converter);
    }

    public InternalConverter<T> lookup(Object value) {
        for (InternalConverter<T> converter : converters) {
            if (converter.canConvert(value)) {
                return converter;
            }
        }
        return NO_OP_CONVERTER;
    }

}
