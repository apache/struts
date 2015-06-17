package com.opensymphony.xwork2.conversion;

/**
 * Represents specialized converters which can convert value to given type T
 */
public interface InternalConverter<T> {

    boolean canConvert(Object value);

    T convert(Object value);

}
