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

import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.PropertyAccessor;

import java.util.Map;

/**
 * Uses a {@link PropertyAccessorDelegateFactory} to delegate the methods to
 * another {@link PropertyAccessor}.
 *
 * @param <T> The type of the accessed root object.
 * @since 2.2.0
 */
public class DelegatePropertyAccessor<T> implements PropertyAccessor {

    /**
     * The property accessor factory.
     *
     * @since 2.2.0
     */
    private final PropertyAccessorDelegateFactory<T> factory;

    /**
     * Constructor.
     *
     * @param factory The property accessor factory.
     * @since 2.2.0
     */
    public DelegatePropertyAccessor(PropertyAccessorDelegateFactory<T> factory) {
        this.factory = factory;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Object getProperty(Map context, Object target, Object name) throws OgnlException {
        return factory.getPropertyAccessor((String) name, (T) target).getProperty(context, target, name);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        factory.getPropertyAccessor((String) name, (T) target).setProperty(context, target, name, value);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public String getSourceAccessor(OgnlContext context, Object target, Object index) {
        return factory.getPropertyAccessor((String) index, (T) target).getSourceAccessor(context, target, index);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public String getSourceSetter(OgnlContext context, Object target, Object index) {
        return factory.getPropertyAccessor((String) index, (T) target).getSourceSetter(context, target, index);
    }
}
