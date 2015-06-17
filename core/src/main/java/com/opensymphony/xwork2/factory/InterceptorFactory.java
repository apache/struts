package com.opensymphony.xwork2.factory;

import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.interceptor.Interceptor;

import java.util.Map;

/**
 * Dedicated interface used by {@link com.opensymphony.xwork2.ObjectFactory} to build {@link com.opensymphony.xwork2.interceptor.Interceptor}
 */
public interface InterceptorFactory {

    /**
     * Builds an Interceptor from the InterceptorConfig and the Map of
     * parameters from the interceptor reference. Implementations of this method
     * should ensure that the Interceptor is parameterized with both the
     * parameters from the Interceptor config and the interceptor ref Map (the
     * interceptor ref params take precedence), and that the Interceptor.init()
     * method is called on the Interceptor instance before it is returned.
     *
     * @param interceptorConfig    the InterceptorConfig from the configuration
     * @param interceptorRefParams a Map of params provided in the Interceptor reference in the
     *                             Action mapping or InterceptorStack definition
     */
    Interceptor buildInterceptor(InterceptorConfig interceptorConfig, Map<String, String> interceptorRefParams) throws ConfigurationException;

}
