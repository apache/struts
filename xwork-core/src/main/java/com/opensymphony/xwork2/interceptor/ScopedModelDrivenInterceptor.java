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
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.inject.Inject;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * <!-- START SNIPPET: description -->
 *
 * An interceptor that enables scoped model-driven actions.
 *
 * <p/>This interceptor only activates on actions that implement the {@link ScopedModelDriven} interface.  If
 * detected, it will retrieve the model class from the configured scope, then provide it to the Action.
 *  
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Interceptor parameters:</u>
 *
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 *
 * <li>className - The model class name.  Defaults to the class name of the object returned by the getModel() method.</li>
 *            
 * <li>name - The key to use when storing or retrieving the instance in a scope.  Defaults to the model
 *            class name.</li>
 *
 * <li>scope - The scope to store and retrieve the model.  Defaults to 'request' but can also be 'session'.</li>
 * </ul>
 *
 * <!-- END SNIPPET: parameters -->
 *
 * <p/> <u>Extending the interceptor:</u>
 *
 * <p/>
 *
 * <!-- START SNIPPET: extending -->
 *
 * There are no known extension points for this interceptor.
 *
 * <!-- END SNIPPET: extending -->
 *
 * <p/> <u>Example code:</u>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * 
 * &lt;-- Basic usage --&gt;
 * &lt;interceptor name="scopedModelDriven" class="com.opensymphony.interceptor.ScopedModelDrivenInterceptor" /&gt;
 * 
 * &lt;-- Using all available parameters --&gt;
 * &lt;interceptor name="gangsterForm" class="com.opensymphony.interceptor.ScopedModelDrivenInterceptor"&gt;
 *      &lt;param name="scope"&gt;session&lt;/param&gt;
 *      &lt;param name="name"&gt;gangsterForm&lt;/param&gt;
 *      &lt;param name="className"&gt;com.opensymphony.example.GangsterForm&lt;/param&gt;
 *  &lt;/interceptor&gt;
 * 
 * <!-- END SNIPPET: example -->
 * </pre>
 */
public class ScopedModelDrivenInterceptor extends AbstractInterceptor {

    private static final Class[] EMPTY_CLASS_ARRAY = new Class[0];
    
    private static final String GET_MODEL = "getModel";
    private String scope;
    private String name;
    private String className;
    private ObjectFactory objectFactory;
    
    @Inject
    public void setObjectFactory(ObjectFactory factory) {
        this.objectFactory = factory;
    }
    
    protected Object resolveModel(ObjectFactory factory, ActionContext actionContext, String modelClassName, String modelScope, String modelName) throws Exception {
        Object model = null;
        Map<String, Object> scopeMap = actionContext.getContextMap();
        if ("session".equals(modelScope)) {
            scopeMap = actionContext.getSession();
        }
        
        model = scopeMap.get(modelName);
        if (model == null) {
            model = factory.buildBean(modelClassName, null);
            scopeMap.put(modelName, model);
        }
        return model;
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();

        if (action instanceof ScopedModelDriven) {
            ScopedModelDriven modelDriven = (ScopedModelDriven) action;
            if (modelDriven.getModel() == null) {
                ActionContext ctx = ActionContext.getContext();
                ActionConfig config = invocation.getProxy().getConfig();
                
                String cName = className;
                if (cName == null) {
                    try {
                        Method method = action.getClass().getMethod(GET_MODEL, EMPTY_CLASS_ARRAY);
                        Class cls = method.getReturnType();
                        cName = cls.getName();
                    } catch (NoSuchMethodException e) {
                        throw new XWorkException("The " + GET_MODEL + "() is not defined in action " + action.getClass() + "", config);
                    }
                }
                String modelName = name;
                if (modelName == null) {
                    modelName = cName;
                }
                Object model = resolveModel(objectFactory, ctx, cName, scope, modelName);
                modelDriven.setModel(model);
                modelDriven.setScopeKey(modelName);
            }
        }
        return invocation.invoke();
    }

    /**
     * @param className the className to set
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param scope the scope to set
     */
    public void setScope(String scope) {
        this.scope = scope;
    }    
}
