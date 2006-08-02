// Copyright 2006 Google Inc. All Rights Reserved.

package org.apache.struts2.impl;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.util.OgnlUtil;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.interceptor.Interceptor;

import java.util.Map;
import java.util.HashMap;

public class StrutsObjectFactory extends ObjectFactory {

    public Interceptor buildInterceptor(InterceptorConfig interceptorConfig, Map refParams)
            throws ConfigurationException {
        String className = interceptorConfig.getClassName();

        Map<String, String> params = new HashMap<String, String>();
        Map typeParams = interceptorConfig.getParams();
        if (typeParams != null && !typeParams.isEmpty())
            params.putAll(typeParams);
        if (refParams != null && !refParams.isEmpty())
            params.putAll(refParams);
        params.putAll(refParams);

        try {
            // interceptor instances are long-lived and used across user sessions, so don't try to pass in any extra
            // context
            Object o = buildBean(className, null);
            OgnlUtil.setProperties(params, o);

            if (o instanceof Interceptor) {
                Interceptor interceptor = (Interceptor) o;
                interceptor.init();
                return interceptor;
            }

            if (o instanceof org.apache.struts2.spi.Interceptor)
                return new InterceptorAdapter((org.apache.struts2.spi.Interceptor) o);

            throw new ConfigurationException(
                    "Class [" + className + "] does not implement Interceptor", interceptorConfig);
        } catch (InstantiationException e) {
            throw new ConfigurationException(
                    "Unable to instantiate an instance of Interceptor class [" + className + "].",
                    e, interceptorConfig);
        } catch (IllegalAccessException e) {
            throw new ConfigurationException(
                    "IllegalAccessException while attempting to instantiate an instance of Interceptor class ["
                            + className + "].",
                    e, interceptorConfig);
        } catch (Exception e) {
            throw new ConfigurationException(
                    "Caught Exception while registering Interceptor class " + className,
                    e, interceptorConfig);
        } catch (NoClassDefFoundError e) {
            throw new ConfigurationException(
                    "Could not load class " + className
                            + ". Perhaps it exists but certain dependencies are not available?",
                    e, interceptorConfig);
        }
    }

    public Result buildResult(ResultConfig resultConfig, Map extraContext) throws Exception {
        String resultClassName = resultConfig.getClassName();
        if (resultClassName == null)
            return null;

        Object result = buildBean(resultClassName, extraContext);
        OgnlUtil.setProperties(resultConfig.getParams(), result, extraContext);

        if (result instanceof Result)
            return (Result) result;

        if (result instanceof org.apache.struts2.spi.Result)
            return new ResultAdapter((org.apache.struts2.spi.Result) result);

        throw new ConfigurationException(result.getClass().getName() + " does not implement Result.");
    }
}
