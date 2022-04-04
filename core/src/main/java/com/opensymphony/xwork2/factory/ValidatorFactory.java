package com.opensymphony.xwork2.factory;

import com.opensymphony.xwork2.validator.Validator;

import java.util.Map;

/**
 * Dedicated interface used by {@link com.opensymphony.xwork2.ObjectFactory} to build {@link Validator}
 */
public interface ValidatorFactory {

    /**
     * Build a Validator of the given type and set the parameters on it
     *
     * @param className the type of Validator to build
     * @param params    property name -&gt; value Map to set onto the Validator instance
     * @param extraContext a Map of extra context which uses the same keys as the {@link com.opensymphony.xwork2.ActionContext}
     *
     * @return a validator instance
     *
     * @throws Exception in case of any errors
     */
    Validator buildValidator(String className, Map<String, Object> params, Map<String, Object> extraContext) throws Exception;

}
