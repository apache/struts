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
package org.apache.struts2.result.xslt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.struts2.StrutsException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is the most general type of adapter, utilizing reflective introspection to present a DOM view of all of
 * the public properties of its value.  For example, a property returning a JavaBean such as:
 *
 * <pre>
 * public Person getMyPerson() { ... }
 * ...
 * class Person {
 *      public String getFirstName();
 *      public String getLastName();
 * }
 * </pre>
 * <p>
 * would be rendered as: &lt;myPerson&gt; &lt;firstName&gt;...&lt;/firstName&gt; &lt;lastName&gt;...&lt;/lastName&gt; &lt;/myPerson&gt;
 */
public class BeanAdapter extends AbstractAdapterElement {

    private static final Logger LOG = LogManager.getLogger(BeanAdapter.class);

    private static final Object[] NULL_PARAMS = new Object[0];

    /**
     * Cache can savely be static because the cached information is the same for all instances of this class.
     */
    private static Map<Class<?>, PropertyDescriptor[]> propertyDescriptorCache;

    public BeanAdapter() {
    }

    public BeanAdapter(AdapterFactory adapterFactory, AdapterNode parent, String propertyName, Object value) {
        setContext(adapterFactory, parent, propertyName, value);
    }

    @Override
    public String getTagName() {
        return getPropertyName();
    }

    @Override
    public NodeList getChildNodes() {
        NodeList nl = super.getChildNodes();
        // Log child nodes for debug:
        if (LOG.isDebugEnabled()) {
            LOG.debug("BeanAdapter getChildNodes for: {}", getTagName());
            LOG.debug(nl.toString());
        }
        return nl;
    }

    @Override
    protected List<Node> buildChildAdapters() {
        LOG.debug("BeanAdapter building children. Property name: {}", getPropertyName());
        List<Node> newAdapters = new ArrayList<>();
        Class<?> type = getPropertyValue().getClass();
        PropertyDescriptor[] props = getPropertyDescriptors(getPropertyValue());

        if (props.length > 0) {
            for (PropertyDescriptor prop : props) {
                Method m = prop.getReadMethod();

                if (m == null) {
                    continue;
                }
                LOG.debug("Bean reading property method: {}", m.getName());

                String propertyName = prop.getName();
                Object propertyValue;

                /*
                    Unwrap any invocation target exceptions and log them.
                    We really need a way to control which properties are accessed.
                    Perhaps with annotations in Java5?
                */
                try {
                    propertyValue = m.invoke(getPropertyValue(), NULL_PARAMS);
                } catch (Exception e) {
                    Exception report = e;
                    if (e instanceof InvocationTargetException) {
                        report = (Exception) ((InvocationTargetException) e).getTargetException();
                    }
                    LOG.error(new ParameterizedMessage("Cannot access bean property: {}", propertyName), report);
                    continue;
                }

                Node childAdapter;

                if (propertyValue == null) {
                    childAdapter = getAdapterFactory().adaptNullValue(this, propertyName);
                } else {
                    childAdapter = getAdapterFactory().adaptNode(this, propertyName, propertyValue);
                }

                if (childAdapter != null) {
                    newAdapters.add(childAdapter);
                }

                LOG.debug("{} adding adapter: {}", this, childAdapter);
            }
        } else {
            // No properties found
            LOG.info("Class {} has no readable properties, trying to adapt {} with StringAdapter...", type.getName(), getPropertyName());
        }

        return newAdapters;
    }

    /**
     * Caching facade method to Introspector.getBeanInfo(Class, Class).getPropertyDescriptors();
     */
    private synchronized PropertyDescriptor[] getPropertyDescriptors(Object bean) {
        try {
            if (propertyDescriptorCache == null) {
                propertyDescriptorCache = new HashMap<>();
            }

            PropertyDescriptor[] props = propertyDescriptorCache.get(bean.getClass());

            if (props == null) {
                LOG.debug("Caching property descriptor for {}", bean.getClass().getName());
                props = Introspector.getBeanInfo(bean.getClass(), Object.class).getPropertyDescriptors();
                propertyDescriptorCache.put(bean.getClass(), props);
            }

            return props;
        } catch (IntrospectionException e) {
            throw new StrutsException("Error getting property descriptors for " + bean + " : " + e.getMessage());
        }
    }
}
