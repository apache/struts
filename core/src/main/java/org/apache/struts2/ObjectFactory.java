/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.config.ConfigurationException;
import org.apache.struts2.config.entities.ActionConfig;
import org.apache.struts2.config.entities.InterceptorConfig;
import org.apache.struts2.config.entities.ResultConfig;
import org.apache.struts2.conversion.TypeConverter;
import org.apache.struts2.factory.ActionFactory;
import org.apache.struts2.factory.ConverterFactory;
import org.apache.struts2.factory.InterceptorFactory;
import org.apache.struts2.factory.ResultFactory;
import org.apache.struts2.factory.UnknownHandlerFactory;
import org.apache.struts2.factory.ValidatorFactory;
import org.apache.struts2.inject.Container;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.interceptor.Interceptor;
import org.apache.struts2.ognl.StrutsContext;
import org.apache.struts2.result.Result;
import org.apache.struts2.util.ClassLoaderUtil;
import org.apache.struts2.validator.Validator;

import java.io.Serializable;
import java.util.Map;

/**
 * ObjectFactory is responsible for building the core framework objects. Users may register their
 * own implementation of the ObjectFactory to control instantiation of these Objects.
 *
 * <p>
 * This default implementation uses the {@link #buildBean(Class,StrutsContext) buildBean}
 * method to create all classes (interceptors, actions, results, etc).
 * </p>
 *
 * @author Jason Carreira
 */
public class ObjectFactory implements Serializable {

    private static final Logger LOG = LogManager.getLogger(ObjectFactory.class);

    private transient ClassLoader ccl;

    private Container container;
    private ActionFactory actionFactory;
    private ResultFactory resultFactory;
    private InterceptorFactory interceptorFactory;
    private ValidatorFactory validatorFactory;
    private ConverterFactory converterFactory;
    private UnknownHandlerFactory unknownHandlerFactory;

    public ObjectFactory() {
    }

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    @Inject(value=StrutsConstants.STRUTS_OBJECT_FACTORY_CLASSLOADER, required=false)
    public void setClassLoader(ClassLoader cl) {
        this.ccl = cl;
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
     * @throws ClassNotFoundException if class not found in classpath
     */
    public <T> Class<T> getClassInstance(String className) throws ClassNotFoundException {
        if (ccl != null) {
            return (Class<T>) ccl.loadClass(className);
        }

        return ClassLoaderUtil.loadClass(className, this.getClass());
    }

    /**
     * Build an instance of the action class to handle a particular request (eg. web request)
     * @param actionName the name the action configuration is set up with in the configuration
     * @param namespace the namespace the action is configured in
     * @param config the action configuration found in the config for the actionName / namespace
     * @param extraContext a Map of extra context which uses the same keys as the {@link org.apache.struts2.ActionContext}
     * @return instance of the action class to handle a web request
     * @throws Exception in case of any error
     */
    public Object buildAction(String actionName, String namespace, ActionConfig config, StrutsContext extraContext) throws Exception {
        return actionFactory.buildAction(actionName, namespace, config, extraContext);
    }

    /**
     * Build a generic Java object of the given type.
     *
     * @param clazz the type of Object to build
     * @param extraContext a Map of extra context which uses the same keys as the {@link org.apache.struts2.ActionContext}
     * @return object for the given type
     * @throws Exception in case of any error
     */
    public <T> T buildBean(Class<T> clazz, StrutsContext extraContext) throws Exception {
        return container.inject(clazz);
    }

    /**
     * @param obj object to inject internal
     * @return the object
     */
    protected Object injectInternalBeans(Object obj) {
        if (obj != null && container != null) {
            LOG.debug("Injecting internal beans into [{}]", obj.getClass().getSimpleName());
            container.inject(obj);
        }
        return obj;
    }

    /**
     * Build a generic Java object of the given type.
     *
     * @param className the type of Object to build
     * @param extraContext a Map of extra context which uses the same keys as the {@link org.apache.struts2.ActionContext}
     * @return object for the given type
     * @throws Exception in case of any error
     */
    public <T> T buildBean(String className, StrutsContext extraContext) throws Exception {
        return buildBean(className, extraContext, true);
    }

    /**
     * Build a generic Java object of the given type.
     *
     * @param className the type of Object to build
     * @param extraContext a Map of extra context which uses the same keys as the {@link org.apache.struts2.ActionContext}
     * @param injectInternal true if inject internal beans
     * @return object for the given type
     * @throws Exception in case of any error
     */
    public <T> T buildBean(String className, StrutsContext extraContext, boolean injectInternal) throws Exception {
        Class<T> clazz = getClassInstance(className);
        return buildBean(clazz, extraContext);
    }

    /**
     * Builds an Interceptor from the InterceptorConfig and the Map of
     * parameters from the interceptor reference. Implementations of this method
     * should ensure that the Interceptor is parametrized with both the
     * parameters from the Interceptor config and the interceptor ref Map (the
     * interceptor ref params take precedence), and that the Interceptor.init()
     * method is called on the Interceptor instance before it is returned.
     *
     * @param interceptorConfig    the InterceptorConfig from the configuration
     * @param interceptorRefParams a Map of params provided in the Interceptor reference in the
     *                             Action mapping or InterceptorStack definition
     * @return interceptor
     */
    public Interceptor buildInterceptor(InterceptorConfig interceptorConfig, Map<String, String> interceptorRefParams) throws ConfigurationException {
        return interceptorFactory.buildInterceptor(interceptorConfig, interceptorRefParams);
    }

    /**
     * Build a Result using the type in the ResultConfig and set the parameters in the ResultConfig.
     *
     * @param resultConfig the ResultConfig found for the action with the result code returned
     * @param extraContext a Map of extra context which uses the same keys as the {@link org.apache.struts2.ActionContext}
     *
     * @return result
     * @throws Exception in case of any error
     */
    public Result buildResult(ResultConfig resultConfig, StrutsContext extraContext) throws Exception {
        return resultFactory.buildResult(resultConfig, extraContext);
    }

    /**
     * Build a Validator of the given type and set the parameters on it
     *
     * @param className the type of Validator to build
     * @param params    property name -&gt; value Map to set onto the Validator instance
     * @param extraContext a Map of extra context which uses the same keys as the {@link org.apache.struts2.ActionContext}
     *
     * @return validator of the given type
     * @throws Exception in case of any error
     */
    public <T> Validator<T> buildValidator(String className, Map<String, Object> params, StrutsContext extraContext) throws Exception {
        return validatorFactory.buildValidator(className, params, extraContext);
    }

    /**
     * Build converter of given type
     *
     * @param converterClass to instantiate
     * @param extraContext a Map of extra context which uses the same keys as the {@link org.apache.struts2.ActionContext}
     * @return instance of converterClass with inject dependencies
     * @throws Exception in case of any error
     */
    public TypeConverter buildConverter(Class<? extends TypeConverter> converterClass, StrutsContext extraContext) throws Exception {
        return converterFactory.buildConverter(converterClass, extraContext);
    }

    /**
     * Builds unknown handler
     *
     * @param unknownHandlerName the unknown handler name
     * @param extraContext extra context
     * @return a unknown handler
     * @throws Exception in case of any error
     */
    public UnknownHandler buildUnknownHandler(String unknownHandlerName, Map<String, Object> extraContext) throws Exception {
        return unknownHandlerFactory.buildUnknownHandler(unknownHandlerName, extraContext);
    }

}
