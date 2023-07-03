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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ScopeELResolver extends ELResolver {

    private static final int SUFFIX_LENGTH = 5;

    public ScopeELResolver() {
    }

    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        return base != null ? null : Map.class;
    }

    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        if (base != null) {
            List<FeatureDescriptor> retValue = Collections.emptyList();
            return retValue.iterator();
        } else {
            List<FeatureDescriptor> list = new ArrayList();
            Request request = (Request)context.getContext(Request.class);
            Iterator i$ = request.getAvailableScopes().iterator();

            while(i$.hasNext()) {
                String scope = (String)i$.next();
                FeatureDescriptor descriptor = new FeatureDescriptor();
                descriptor.setDisplayName(scope + "Scope");
                descriptor.setExpert(false);
                descriptor.setHidden(false);
                descriptor.setName(scope + "Scope");
                descriptor.setPreferred(true);
                descriptor.setShortDescription("");
                descriptor.setValue("type", Map.class);
                descriptor.setValue("resolvableAtDesignTime", Boolean.FALSE);
                list.add(descriptor);
            }

            return list.iterator();
        }
    }

    public Class<?> getType(ELContext context, Object base, Object property) {
        return base == null && property != null && property instanceof String && ((String)property).endsWith("Scope") ? Map.class : null;
    }

    public Object getValue(ELContext context, Object base, Object property) {
        if (base != null) {
            return null;
        } else {
            Object retValue = null;
            String propertyString = (String)property;
            if (property instanceof String && propertyString.endsWith("Scope")) {
                Request request = (Request)context.getContext(Request.class);
                retValue = request.getContext(propertyString.substring(0, propertyString.length() - 5));
            }

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
}
