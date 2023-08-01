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
 * Uses a {@link PropertyAccessor} as a delegate, but passing a nested object as
 * target.
 *
 * @param <T> The root object type from which the target object will be extracted.
 * @since 2.2.0
 */
public class NestedObjectDelegatePropertyAccessor<T> implements PropertyAccessor {

    /**
     * The extractor of the nested object.
     *
     * @since 2.2.0
     */
    private final NestedObjectExtractor<T> nestedObjectExtractor;

    /**
     * The delegated property accessor.
     *
     * @since 2.2.0
     */
    private final PropertyAccessor propertyAccessor;

    /**
     * Constructor.
     *
     * @param nestedObjectExtractor The extractor of the nested object.
     * @param propertyAccessor      The delegated property accessor.
     * @since 2.2.0
     */
    public NestedObjectDelegatePropertyAccessor(NestedObjectExtractor<T> nestedObjectExtractor, PropertyAccessor propertyAccessor) {
        this.nestedObjectExtractor = nestedObjectExtractor;
        this.propertyAccessor = propertyAccessor;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Object getProperty(Map context, Object target, Object name) throws OgnlException {
        return propertyAccessor.getProperty(context, nestedObjectExtractor.getNestedObject((T) target), name);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        propertyAccessor.setProperty(context, nestedObjectExtractor.getNestedObject((T) target), name, value);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public String getSourceAccessor(OgnlContext context, Object target, Object index) {
        return propertyAccessor.getSourceAccessor(context, nestedObjectExtractor.getNestedObject((T) target), index);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public String getSourceSetter(OgnlContext context, Object target, Object index) {
        return propertyAccessor.getSourceSetter(context, nestedObjectExtractor.getNestedObject((T) target), index);
    }
}
