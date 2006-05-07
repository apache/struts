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
package org.apache.struts.action2.views.xslt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 */
public class BeanAdapter extends DefaultElementAdapter {

    private static final Object[] NULLPARAMS = new Object[0];

    /**
     * Cache can savely be static because the cached information is
     * the same for all instances of this class.
     */
    private static Map propertyDescriptorCache;


    private Log log = LogFactory.getLog(this.getClass());


    public BeanAdapter(DOMAdapter rootAdapter, AdapterNode parent, String propertyName, Object value) {
        super(rootAdapter, parent, propertyName, value);
    }


    public String getTagName() {
        return getPropertyName();
    }

    protected List buildChildrenAdapters() {
        List newAdapters = new ArrayList();
        Class type = getValue().getClass();
        PropertyDescriptor[] props = getPropertyDescriptors(getValue());

        if (props.length > 0) {
            for (int i = 0; i < props.length; i++) {
                Method m = props[i].getReadMethod();

                if (m == null) {
                    //FIXME: write only property or indexed access
                    continue;
                }

                String propertyName = props[i].getName();
                if (! getRootAdapter().isAdaptable(getRootAdapter(), this, propertyName))
                    continue;

                Object propertyValue = null;

                /** 999 white magic hack start 999 **
                 * some property accessors will throw exceptions, e.g. getLocale() in struts.ActionSupport *grrr*
                 * IMHO property accessors should not have those side effects - meier@meisterbohne.de
                 */
                try {
                    propertyValue = m.invoke(getValue(), NULLPARAMS);
                } catch (Exception e) {
                    log.error("Exception when checking property " + propertyName, e);
                    continue;
                }

                /** 999 white magic hack end 999 **/
                AdapterNode childAdapter;

                if (propertyValue == null) {
                    childAdapter = getRootAdapter().adaptNullValue(getRootAdapter(), this, propertyName);
                } else {
                    childAdapter = getRootAdapter().adapt(getRootAdapter(), this, propertyName, propertyValue);
                }

                if( childAdapter != null)
                    newAdapters.add(childAdapter);

                if (log.isDebugEnabled()) {
                    log.debug(this + " adding adapter: " + childAdapter);
                }
            }
        } else {
            // No properties found
            log.info("Class " + type.getName() + " has no readable properties, " + " trying to adapt " + getPropertyName() + " with ToStringAdapter...");

            //newAdapters.add(new ToStringAdapter(getRootAdapter(), this, getPropertyName(), getValue()));
        }

        return newAdapters;
    }

    /**
     * Caching facade method to Introspector.getBeanInfo(Class, Class).getPropertyDescriptors();
     */
    private synchronized PropertyDescriptor[] getPropertyDescriptors(Object bean) {
        try {
            if (propertyDescriptorCache == null) {
                propertyDescriptorCache = new HashMap();
            }

            PropertyDescriptor[] props = (PropertyDescriptor[]) propertyDescriptorCache.get(bean.getClass());

            if (props == null) {
                log.debug("Caching property descriptor for " + bean.getClass().getName());
                props = Introspector.getBeanInfo(bean.getClass(), Object.class).getPropertyDescriptors();
                propertyDescriptorCache.put(bean.getClass(), props);
            }

            return props;
        } catch (IntrospectionException e) {
            e.printStackTrace();
            throw new RuntimeException("Error getting property descriptors for " + bean + " : " + e.getMessage());
        }
    }
}
