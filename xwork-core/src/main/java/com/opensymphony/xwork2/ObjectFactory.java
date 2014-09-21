/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.factory.ActionFactory;
import com.opensymphony.xwork2.factory.ConverterFactory;
import com.opensymphony.xwork2.factory.InterceptorFactory;
import com.opensymphony.xwork2.factory.ResultFactory;
import com.opensymphony.xwork2.factory.UnknownHandlerFactory;
import com.opensymphony.xwork2.factory.ValidatorFactory;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.validator.Validator;

import java.io.Serializable;
import java.util.Map;


/**
 * ObjectFactory is responsible for building the core framework objects. Users may register their 
 * own implementation of the ObjectFactory to control instantiation of these Objects.
 * <p/>
 * This default implementation uses the {@link #buildBean(Class,java.util.Map) buildBean} 
 * method to create all classes (interceptors, actions, results, etc).
 * <p/>
 *
 * @author Jason Carreira
 */
public class ObjectFactory implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(ObjectFactory.class);

    private transient ClassLoader ccl;
    private Container container;

    private ActionFactory actionFactory;
    private ResultFactory resultFactory;
    private InterceptorFactory interceptorFactory;
    private ValidatorFactory validatorFactory;
    private ConverterFactory converterFactory;
    private UnknownHandlerFactory unknownHandlerFactory;

    @Inject(value="objectFactory.classloader", required=false)
    public void setClassLoader(ClassLoader cl) {
        this.ccl = cl;
    }
    
    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    @Inject
    public void setActionFactory(ActionFactory actionFactory) {
        this.actionFactory = actionFactory;
    }

    @Inject
    public void setResultFactory(ResultFactory resultFactory) {
        this.resultFactory = resultFactory;
    }

    @Inject
    public void setInterceptorFactory(InterceptorFactory interceptorFactory) {
        this.interceptorFactory = interceptorFactory;
    }

    @Inject
    public void setValidatorFactory(ValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
    }

    @Inject
    public void setConverterFactory(ConverterFactory converterFactory) {
        this.converterFactory = converterFactory;
    }

    @Inject
    public void setUnknownHandlerFactory(UnknownHandlerFactory unknownHandlerFactory) {
        this.unknownHandlerFactory = unknownHandlerFactory;
    }

    /**
     * @deprecated Since 2.1
     */
    @Deprecated public static ObjectFactory getObjectFactory() {
        return ActionContext.getContext().getContainer().getInstance(ObjectFactory.class);
    }

    /**
     * Allows for ObjectFactory implementations that support
     * Actions without no-arg constructors.
     *
     * @return true if no-arg constructor is required, false otherwise
     */
    public boolean isNoArgConstructorRequired() {
        return true;
    }

    /**
     * Utility method to obtain the class matched to className. Caches look ups so that subsequent
     * lookups will be faster.
     *
     * @param className The fully qualified name of the class to return
     * @return The class itself
     * @throws ClassNotFoundException
     */
    public Class getClassInstance(String className) throws ClassNotFoundException {
        if (ccl != null) {
            return ccl.loadClass(className);
        }

        return ClassLoaderUtil.loadClass(className, this.getClass());
    }

    /**
     * Build an instance of the action class to handle a particular request (eg. web request)
     * @param actionName the name the action configuration is set up with in the configuration
     * @param namespace the namespace the action is configured in
     * @param config the action configuration found in the config for the actionName / namespace
     * @param extraContext a Map of extra context which uses the same keys as the {@link com.opensymphony.xwork2.ActionContext}
     * @return instance of the action class to handle a web request
     * @throws Exception
     */
    public Object buildAction(String actionName, String namespace, ActionConfig config, Map<String, Object> extraContext) throws Exception {
        return actionFactory.buildAction(actionName, namespace, config, extraContext);
    }

    /**
     * Build a generic Java object of the given type.
     *
     * @param clazz the type of Object to build
     * @param extraContext a Map of extra context which uses the same keys as the {@link com.opensymphony.xwork2.ActionContext}
     */
    public Object buildBean(Class clazz, Map<String, Object> extraContext) throws Exception {
        return clazz.newInstance();
    }

    /**
     * @param obj
     */
    protected Object injectInternalBeans(Object obj) {
        if (obj != null && container != null) {
            container.inject(obj);
        }
        return obj;
    }

    /**
     * Build a generic Java object of the given type.
     *
     * @param className the type of Object to build
     * @param extraContext a Map of extra context which uses the same keys as the {@link com.opensymphony.xwork2.ActionContext}
     */
    public Object buildBean(String className, Map<String, Object> extraContext) throws Exception {
        return buildBean(className, extraContext, true);
    }
    
    /**
     * Build a generic Java object of the given type.
     *
     * @param className the type of Object to build
     * @param extraContext a Map of extra context which uses the same keys as the {@link com.opensymphony.xwork2.ActionContext}
     */
    public Object buildBean(String className, Map<String, Object> extraContext, boolean injectInternal) throws Exception {
        Class clazz = getClassInstance(className);
        Object obj = buildBean(clazz, extraContext);
        if (injectInternal) {
            injectInternalBeans(obj);
        }
        return obj;
    }

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
    public Interceptor buildInterceptor(InterceptorConfig interceptorConfig, Map<String, String> interceptorRefParams) throws ConfigurationException {
        return interceptorFactory.buildInterceptor(interceptorConfig, interceptorRefParams);
    }

    /**
     * Build a Result using the type in the ResultConfig and set the parameters in the ResultConfig.
     *
     * @param resultConfig the ResultConfig found for the action with the result code returned
     * @param extraContext a Map of extra context which uses the same keys as the {@link com.opensymphony.xwork2.ActionContext}
     */
    public Result buildResult(ResultConfig resultConfig, Map<String, Object> extraContext) throws Exception {
        return resultFactory.buildResult(resultConfig, extraContext);
    }

    /**
     * Build a Validator of the given type and set the parameters on it
     *
     * @param className the type of Validator to build
     * @param params    property name -> value Map to set onto the Validator instance
     * @param extraContext a Map of extra context which uses the same keys as the {@link com.opensymphony.xwork2.ActionContext}
     */
    public Validator buildValidator(String className, Map<String, Object> params, Map<String, Object> extraContext) throws Exception {
        return validatorFactory.buildValidator(className, params, extraContext);
    }

    /**
     * Build converter of given type
     *
     * @param converterClass to instantiate
     * @param extraContext a Map of extra context which uses the same keys as the {@link com.opensymphony.xwork2.ActionContext}
     * @return instance of converterClass with inject dependencies
     */
    public TypeConverter buildConverter(Class<? extends TypeConverter> converterClass, Map<String, Object> extraContext) throws Exception {
        return converterFactory.buildConverter(converterClass, extraContext);
    }

    /**
     * Builds unknown handler
     *
     * @param unknownHandlerName
     * @param extraContext
     * @return
     * @throws Exception
     */
    public UnknownHandler buildUnknownHandler(String unknownHandlerName, Map<String, Object> extraContext) throws Exception {
        return unknownHandlerFactory.buildUnknownHandler(unknownHandlerName, extraContext);
    }
    
}
