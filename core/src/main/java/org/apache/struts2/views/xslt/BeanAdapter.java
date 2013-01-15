/*
 * $Id$
 *
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

package org.apache.struts2.views.xslt;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
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
 *
 * would be rendered as: <myPerson> <firstName>...</firstName> <lastName>...</lastName> </myPerson>
 */
public class BeanAdapter extends AbstractAdapterElement {
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Object[] NULLPARAMS = new Object[0];

    /**
     * Cache can savely be static because the cached information is the same for all instances of this class.
     */
    private static Map<Class, PropertyDescriptor[]> propertyDescriptorCache;

    //~ Instance fields ////////////////////////////////////////////////////////

    private Logger log = LoggerFactory.getLogger(this.getClass());

    //~ Constructors ///////////////////////////////////////////////////////////

    public BeanAdapter() {
    }

    public BeanAdapter(
            AdapterFactory adapterFactory, AdapterNode parent, String propertyName, Object value) {
        setContext(adapterFactory, parent, propertyName, value);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public String getTagName() {
        return getPropertyName();
    }

    public NodeList getChildNodes() {
        NodeList nl = super.getChildNodes();
        // Log child nodes for debug:
        if (log.isDebugEnabled() && nl != null) {
            log.debug("BeanAdapter getChildNodes for: " + getTagName());
            log.debug(nl.toString());
        }
        return nl;
    }

    protected List<Node> buildChildAdapters() {
        log.debug("BeanAdapter building children.  PropName = " + getPropertyName());
        List<Node> newAdapters = new ArrayList<Node>();
        Class type = getPropertyValue().getClass();
        PropertyDescriptor[] props = getPropertyDescriptors(getPropertyValue());

        if (props.length > 0) {
            for (PropertyDescriptor prop : props) {
                Method m = prop.getReadMethod();

                if (m == null) {
                    //FIXME: write only property or indexed access
                    continue;
                }
                log.debug("Bean reading property method: " + m.getName());

                String propertyName = prop.getName();
                Object propertyValue;

                /*
                    Unwrap any invocation target exceptions and log them.
                    We really need a way to control which properties are accessed.
                    Perhaps with annotations in Java5?
                */
                try {
                    propertyValue = m.invoke(getPropertyValue(), NULLPARAMS);
                } catch (Exception e) {
                    if (e instanceof InvocationTargetException)
                        e = (Exception) ((InvocationTargetException) e).getTargetException();
                    if (log.isErrorEnabled()) {
                        log.error("Cannot access bean property: #0", e, propertyName);
                    }
                    continue;
                }

                Node childAdapter;

                if (propertyValue == null) {
                    childAdapter = getAdapterFactory().adaptNullValue(this, propertyName);
                } else {
                    childAdapter = getAdapterFactory().adaptNode(this, propertyName, propertyValue);
                }

                if (childAdapter != null)
                    newAdapters.add(childAdapter);

                if (log.isDebugEnabled()) {
                    log.debug(this + " adding adapter: " + childAdapter);
                }
            }
        } else {
            // No properties found
            log.info(
                    "Class " + type.getName() + " has no readable properties, " + " trying to adapt " + getPropertyName() + " with StringAdapter...");
        }

        return newAdapters;
    }

    /**
     * Caching facade method to Introspector.getBeanInfo(Class, Class).getPropertyDescriptors();
     */
    private synchronized PropertyDescriptor[] getPropertyDescriptors(Object bean) {
        try {
            if (propertyDescriptorCache == null) {
                propertyDescriptorCache = new HashMap<Class, PropertyDescriptor[]>();
            }

            PropertyDescriptor[] props = propertyDescriptorCache.get(bean.getClass());

            if (props == null) {
                log.debug("Caching property descriptor for " + bean.getClass().getName());
                props = Introspector.getBeanInfo(bean.getClass(), Object.class).getPropertyDescriptors();
                propertyDescriptorCache.put(bean.getClass(), props);
            }

            return props;
        } catch (IntrospectionException e) {
            e.printStackTrace();
            throw new StrutsException("Error getting property descriptors for " + bean + " : " + e.getMessage());
        }
    }
}
