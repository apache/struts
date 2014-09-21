package com.opensymphony.xwork2.factory;

import com.opensymphony.xwork2.UnknownHandler;

import java.util.Map;

/**
 * Dedicated interface used by {@link com.opensymphony.xwork2.ObjectFactory} to build {@link com.opensymphony.xwork2.UnknownHandler}
 */
public interface UnknownHandlerFactory {

    /**
     * Builds unknown handler of given name
     *
     * @param unknownHandlerName name of unknown handler defined in struts.xml
     * @param extraContext extra params
     * @return instance of {@link com.opensymphony.xwork2.UnknownHandler} with injected dependencies
     */
    UnknownHandler buildUnknownHandler(String unknownHandlerName, Map<String, Object> extraContext) throws Exception;

}
