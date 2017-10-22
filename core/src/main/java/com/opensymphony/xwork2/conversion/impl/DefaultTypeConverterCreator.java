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
package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.conversion.TypeConverterCreator;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.XWorkTypeConverterWrapper;

/**
 * Default implementation of {@link TypeConverterCreator}
 */
public class DefaultTypeConverterCreator implements TypeConverterCreator {

    private ObjectFactory objectFactory;

    @Inject
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    public TypeConverter createTypeConverter(String className) throws Exception {
        Object obj = objectFactory.buildBean(className, null);
        return getTypeConverter(obj);
    }

    public TypeConverter createTypeConverter(Class<?> clazz) throws Exception {
        Object obj = objectFactory.buildBean(clazz, null);
        return getTypeConverter(obj);
    }

    protected TypeConverter getTypeConverter(Object obj) {
        if (obj instanceof TypeConverter) {
            return (TypeConverter) obj;

            // For backwards compatibility
        } else if (obj instanceof ognl.TypeConverter) {
            return new XWorkTypeConverterWrapper((ognl.TypeConverter) obj);
        } else {
            throw new IllegalArgumentException("Type converter class " + obj.getClass() + " doesn't implement com.opensymphony.xwork2.conversion.TypeConverter");
        }
    }

}
