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

package org.apache.struts2.tiles.el.resolver;

import jakarta.el.ELContext;
import jakarta.el.ELResolver;
import org.apache.tiles.request.Request;

import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TilesContextBeanELResolver extends ELResolver {
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        return base != null ? null : String.class;
    }

    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        List<FeatureDescriptor> list = new ArrayList();
        Request request = (Request)context.getContext(Request.class);
        Iterator i$ = request.getAvailableScopes().iterator();

        while(i$.hasNext()) {
            String scope = (String)i$.next();
            this.collectBeanInfo(request.getContext(scope), list);
        }

        return list.iterator();
    }

    public Class<?> getType(ELContext context, Object base, Object property) {
        if (base != null) {
            return null;
        } else {
            Object obj = this.findObjectByProperty(context, property);
            if (obj != null) {
                context.setPropertyResolved(true);
                return obj.getClass();
            } else {
                return null;
            }
        }
    }

    public Object getValue(ELContext context, Object base, Object property) {
        if (base != null) {
            return null;
        } else {
            Object retValue = this.findObjectByProperty(context, property);
            if (retValue != null) {
                context.setPropertyResolved(true);
            }

            return retValue;
        }
    }

    public boolean isReadOnly(ELContext context, Object base, Object property) {
        if (context == null) {
            throw new NullPointerException();
        } else {
            return true;
        }
    }

    public void setValue(ELContext context, Object base, Object property, Object value) {
    }

    protected void collectBeanInfo(Map<String, ? extends Object> map, List<FeatureDescriptor> list) {
        if (map != null && !map.isEmpty()) {
            Iterator i$ = map.entrySet().iterator();

            while(i$.hasNext()) {
                Map.Entry<String, ? extends Object> entry = (Map.Entry)i$.next();
                FeatureDescriptor descriptor = new FeatureDescriptor();
                descriptor.setDisplayName((String)entry.getKey());
                descriptor.setExpert(false);
                descriptor.setHidden(false);
                descriptor.setName((String)entry.getKey());
                descriptor.setPreferred(true);
                descriptor.setShortDescription("");
                descriptor.setValue("type", String.class);
                descriptor.setValue("resolvableAtDesignTime", Boolean.FALSE);
                list.add(descriptor);
            }

        }
    }

    protected Object findObjectByProperty(ELContext context, Object property) {
        Object retValue = null;
        Request request = (Request)context.getContext(Request.class);
        String prop = property.toString();
        String[] scopes = (String[])request.getAvailableScopes().toArray(new String[0]);
        int i = 0;

        do {
            retValue = this.getObject(request.getContext(scopes[i]), prop);
            ++i;
        } while(retValue == null && i < scopes.length);

        return retValue;
    }

    protected Object getObject(Map<String, ? extends Object> map, String property) {
        Object retValue = null;
        if (map != null) {
            retValue = map.get(property);
        }

        return retValue;
    }
}
