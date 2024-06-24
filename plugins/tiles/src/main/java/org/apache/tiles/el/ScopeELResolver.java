/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.el;

import org.apache.tiles.request.Request;

import jakarta.el.ELContext;
import jakarta.el.ELResolver;
import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Resolves beans in request, session and application scope.
 *
 * @since 2.2.1
 */
public class ScopeELResolver extends ELResolver {

    /**
     * The length of the suffix: "Scope".
     */
    private static final int SUFFIX_LENGTH = 5;

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        // only resolve at the root of the context
        if (base != null) {
            return null;
        }

        return Map.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        if (base != null) {
            return Collections.emptyIterator();
        }

        List<FeatureDescriptor> list = new ArrayList<>();

        Request request = (Request) context
            .getContext(Request.class);
        for (String scope : request.getAvailableScopes()) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getType(ELContext context, Object base, Object property) {
        if (base != null || !(property instanceof String) || !((String) property).endsWith("Scope")) {
            return null;
        }

        return Map.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue(ELContext context, Object base, Object property) {
        if (base != null) {
            return null;
        }

        Object retValue = null;
        String propertyString = (String) property;
        if (property != null && propertyString.endsWith("Scope")) {
            Request request = (Request) context
                .getContext(Request.class);
            retValue = request.getContext(propertyString.substring(0,
                propertyString.length() - SUFFIX_LENGTH));
        }

        if (retValue != null) {
            context.setPropertyResolved(true);
        }

        return retValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        if (context == null) {
            throw new NullPointerException();
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(ELContext context, Object base, Object property,
                         Object value) {
        // Does nothing for the moment.
    }
}
