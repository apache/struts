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
package org.apache.struts2.ognl.accessor;

import org.apache.struts2.ognl.ObjectProxy;
import org.apache.struts2.ognl.StrutsContext;
import org.apache.struts2.util.reflection.ReflectionContextState;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;

/**
 * Is able to access (set/get) properties on a given object.
 * <p>
 * Uses Ognl internal.
 * </p>
 *
 * @author Gabe
 */
public class ObjectProxyPropertyAccessor implements PropertyAccessor<StrutsContext> {

    /**
     * Used by OGNl to generate bytecode
     */
    @Override
    public String getSourceAccessor(StrutsContext context, Object target, Object index) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Used by OGNl to generate bytecode
     */
    @Override
    public String getSourceSetter(StrutsContext context, Object target, Object index) {
        return null;
    }

    @Override
    public Object getProperty(StrutsContext context, Object target, Object name) throws OgnlException {
        ObjectProxy proxy = (ObjectProxy) target;
        setupContext(context, proxy);

        PropertyAccessor<StrutsContext> propertyAccessor = OgnlRuntime.getPropertyAccessor(proxy.getValue().getClass());
        return propertyAccessor.getProperty(context, target, name);

    }

    @Override
    public void setProperty(StrutsContext context, Object target, Object name, Object value) throws OgnlException {
        ObjectProxy proxy = (ObjectProxy) target;
        setupContext(context, proxy);

        PropertyAccessor<StrutsContext> propertyAccessor = OgnlRuntime.getPropertyAccessor(proxy.getValue().getClass());
        propertyAccessor.setProperty(context, target, name, value);
    }

    /**
     * Sets up the context with the last property and last class
     * accessed.
     *
     * @param context
     * @param proxy
     */
    private void setupContext(StrutsContext context, ObjectProxy proxy) {
        ReflectionContextState.setLastBeanClassAccessed(context, proxy.getLastClassAccessed());
        ReflectionContextState.setLastBeanPropertyAccessed(context, proxy.getLastPropertyAccessed());
    }
}
