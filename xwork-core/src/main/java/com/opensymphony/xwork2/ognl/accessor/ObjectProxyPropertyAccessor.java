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

package com.opensymphony.xwork2.ognl.accessor;

import com.opensymphony.xwork2.ognl.ObjectProxy;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;
import ognl.OgnlContext;

import java.util.Map;

/**
 * Is able to access (set/get) properties on a given object.
 * <p/>
 * Uses Ognl internal.
 *
 * @author Gabe
 */
public class ObjectProxyPropertyAccessor implements PropertyAccessor {

    /**
     * Used by OGNl to generate bytecode
     */
    public String getSourceAccessor(OgnlContext context, Object target, Object index) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Used by OGNl to generate bytecode
     */
    public String getSourceSetter(OgnlContext context, Object target, Object index) {
        return null;  
    }

    public Object getProperty(Map context, Object target, Object name) throws OgnlException {
        ObjectProxy proxy = (ObjectProxy) target;
        setupContext(context, proxy);

        return OgnlRuntime.getPropertyAccessor(proxy.getValue().getClass()).getProperty(context, target, name);

    }

    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        ObjectProxy proxy = (ObjectProxy) target;
        setupContext(context, proxy);

        OgnlRuntime.getPropertyAccessor(proxy.getValue().getClass()).setProperty(context, target, name, value);
    }

    /**
     * Sets up the context with the last property and last class
     * accessed.
     *
     * @param context
     * @param proxy
     */
    private void setupContext(Map context, ObjectProxy proxy) {
        ReflectionContextState.setLastBeanClassAccessed(context, proxy.getLastClassAccessed());
        ReflectionContextState.setLastBeanPropertyAccessed(context, proxy.getLastPropertyAccessed());
    }
}
