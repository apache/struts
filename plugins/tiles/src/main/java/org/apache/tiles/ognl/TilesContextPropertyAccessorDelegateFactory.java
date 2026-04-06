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
package org.apache.tiles.ognl;

import ognl.PropertyAccessor;
import org.apache.tiles.core.util.CombinedBeanInfo;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;

/**
 * Decides the appropriate {@link PropertyAccessor} for the given property name
 * and {@link Request}.
 *
 * @since 2.2.0
 */
public class TilesContextPropertyAccessorDelegateFactory implements PropertyAccessorDelegateFactory<Request> {

    /**
     * The plain object property accessor, to be used directly for
     * {@link Request}.
     */
    private final PropertyAccessor objectPropertyAccessor;

    /**
     * The application context property accessor.
     */
    private final PropertyAccessor applicationContextPropertyAccessor;

    /**
     * The request scope property accessor.
     */
    private final PropertyAccessor anyScopePropertyAccessor;

    /**
     * The session scope property accessor.
     */
    private final PropertyAccessor scopePropertyAccessor;

    /**
     * The bean info of {@link Request} and
     * {@link ApplicationContext}.
     */
    private final CombinedBeanInfo beanInfo;

    /**
     * Constructor.
     *
     * @param objectPropertyAccessor The plain object property accessor, to be
     * used directly for {@link Request}.
     * @param applicationContextPropertyAccessor The application context
     * property accessor.
     * @param anyScopePropertyAccessor The request scope property accessor.
     * @param scopePropertyAccessor The session scope property accessor.
     * @since 2.2.0
     */
    public TilesContextPropertyAccessorDelegateFactory(
            PropertyAccessor objectPropertyAccessor,
            PropertyAccessor applicationContextPropertyAccessor,
            PropertyAccessor anyScopePropertyAccessor,
            PropertyAccessor scopePropertyAccessor
    ) {
        beanInfo = new CombinedBeanInfo(Request.class, ApplicationContext.class);
        this.objectPropertyAccessor = objectPropertyAccessor;
        this.applicationContextPropertyAccessor = applicationContextPropertyAccessor;
        this.anyScopePropertyAccessor = anyScopePropertyAccessor;
        this.scopePropertyAccessor = scopePropertyAccessor;
    }

    /** {@inheritDoc} */
    public PropertyAccessor getPropertyAccessor(String propertyName, Request request) {
        PropertyAccessor retValue;
        if (propertyName.endsWith("Scope")) {
            String scopeName = propertyName.substring(0, propertyName.length() - ScopePropertyAccessor.SCOPE_SUFFIX_LENGTH);
            if (request.getContext(scopeName) != null) {
                return scopePropertyAccessor;
            }
        }
        if (beanInfo.getMappedDescriptors(Request.class).containsKey(propertyName)) {
            retValue = objectPropertyAccessor;
        } else if (beanInfo.getMappedDescriptors(ApplicationContext.class).containsKey(propertyName)) {
            retValue = applicationContextPropertyAccessor;
        } else {
            return anyScopePropertyAccessor;
        }
        return retValue;
    }
}
