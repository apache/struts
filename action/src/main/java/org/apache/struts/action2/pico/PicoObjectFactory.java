/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
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
/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.apache.struts.action2.pico;

import com.opensymphony.xwork.ObjectFactory;
import com.opensymphony.xwork.Result;
import com.opensymphony.xwork.validator.Validator;
import com.opensymphony.xwork.interceptor.Interceptor;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.InterceptorConfig;
import com.opensymphony.xwork.config.entities.ResultConfig;
import com.opensymphony.xwork.config.ConfigurationException;
import org.nanocontainer.nanowar.ActionsContainerFactory;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.ObjectReference;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * XWork ObjectFactory which uses a PicoContainer to create component instances.
 * </p>
 * 
 */
public class PicoObjectFactory extends ObjectFactory {

    private final ActionsContainerFactory actionsContainerFactory = new ActionsContainerFactory();
    private final ObjectReference objectReference;

    /**
     * Creates a PicoObjectFactory with given object reference, 
     * used to pass the http request to the factory
     * 
     * @param objectReference the ObjectReference 
     */
    public PicoObjectFactory(ObjectReference objectReference) {
        this.objectReference = objectReference;
    }

    public boolean isNoArgConstructorRequired() {
        return false;
    }

    /**
     * Struts / XWork-1.1 method. ExtraContext can be ignored.
     */
    public Object buildBean(Class clazz, Map extraContext) throws Exception {
        return buildBean(clazz);
    }

    /**
     * Struts / XWork-1.1 method. ExtraContext can be ignored.
     */
    public Object buildBean(String className, Map extraContext) throws Exception {
        return buildBean(className);
    }

    /**
     * Struts / XWork-1.1 method. Used to validate a class be loaded.
     * Using actionsContainerFactory for consistency with build methods.
     */
    public Class getClassInstance(String className) throws ClassNotFoundException {
        return actionsContainerFactory.getActionClass(className);
    }

    public Object buildAction(String actionName, String namespace, ActionConfig config, Map extraContext) throws Exception {
        return super.buildAction(actionName, namespace, config, extraContext);
    }

    public Interceptor buildInterceptor(InterceptorConfig interceptorConfig, Map interceptorRefParams) throws ConfigurationException {
        return super.buildInterceptor(interceptorConfig, interceptorRefParams);
    }

    public Result buildResult(ResultConfig resultConfig, Map extraContext) throws Exception {
        return super.buildResult(resultConfig, extraContext);
    }

    public Validator buildValidator(String className, Map params, Map extraContext) throws Exception {
        return super.buildValidator(className, params, extraContext);
    }

    /**
     * Instantiates an action using the PicoContainer found in the request scope.
     */
    public Object buildBean(Class actionClass) throws Exception {
        MutablePicoContainer actionsContainer = actionsContainerFactory.getActionsContainer((HttpServletRequest) objectReference.get());
        Object action = actionsContainer.getComponentInstance(actionClass);

        if (action == null) {
            // The action wasn't registered. Attempt to instantiate it.
            actionsContainer.registerComponentImplementation(actionClass);
            action = actionsContainer.getComponentInstance(actionClass);
        }
        return action;
    }

    public Object buildBean(String className) throws Exception {
        Class actionClass = actionsContainerFactory.getActionClass(className);
        return buildBean(actionClass);
    }
}
