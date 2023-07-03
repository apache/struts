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
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.apache.tiles.util.CombinedBeanInfo;

import java.beans.FeatureDescriptor;
import java.util.Iterator;

public class TilesContextELResolver extends ELResolver {

    private ELResolver beanElResolver;
    private CombinedBeanInfo requestBeanInfo = new CombinedBeanInfo(new Class[]{Request.class, ApplicationContext.class});

    public TilesContextELResolver(ELResolver beanElResolver) {
        this.beanElResolver = beanElResolver;
    }

    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        return base != null ? null : String.class;
    }

    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return base != null ? null : this.requestBeanInfo.getDescriptors().iterator();
    }

    public Class<?> getType(ELContext context, Object base, Object property) {
        if (base != null) {
            return null;
        } else {
            Class<?> retValue = null;
            if (this.requestBeanInfo.getProperties(Request.class).contains(property)) {
                Request request = (Request)context.getContext(Request.class);
                retValue = this.beanElResolver.getType(context, request, property);
            } else if (this.requestBeanInfo.getProperties(ApplicationContext.class).contains(property)) {
                ApplicationContext applicationContext = (ApplicationContext)context.getContext(ApplicationContext.class);
                retValue = this.beanElResolver.getType(context, applicationContext, property);
            }

            if (retValue != null) {
                context.setPropertyResolved(true);
            }

            return retValue;
        }
    }

    public Object getValue(ELContext context, Object base, Object property) {
        if (base != null) {
            return null;
        } else {
            Object retValue = null;
            if (this.requestBeanInfo.getProperties(Request.class).contains(property)) {
                Request request = (Request)context.getContext(Request.class);
                retValue = this.beanElResolver.getValue(context, request, property);
            } else if (this.requestBeanInfo.getProperties(ApplicationContext.class).contains(property)) {
                ApplicationContext applicationContext = (ApplicationContext)context.getContext(ApplicationContext.class);
                retValue = this.beanElResolver.getValue(context, applicationContext, property);
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
