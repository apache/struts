package com.opensymphony.xwork2.result;

/**
 * Accept parameter name/value to be set on {@link com.opensymphony.xwork2.Result}
 */
public interface ParamNameAwareResult {

    boolean acceptableParameterName(String name, String value);

}
