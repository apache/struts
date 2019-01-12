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
package org.apache.struts2.conversion;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.conversion.TypeConverterCreator;
import com.opensymphony.xwork2.inject.Inject;

/**
 * Default implementation of {@link TypeConverterCreator}
 */
public class StrutsTypeConverterCreator implements TypeConverterCreator {

    private ObjectFactory objectFactory;

    @Inject
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    public TypeConverter createTypeConverter(String className) throws Exception {
        Class<?> clazz = objectFactory.getClassInstance(className);
        return createTypeConverter(clazz);
    }

    public TypeConverter createTypeConverter(Class<?> clazz) throws Exception {
        if (TypeConverter.class.isAssignableFrom(clazz)) {
            Class<? extends TypeConverter> converterClass = (Class<? extends TypeConverter>) clazz;
            return objectFactory.buildConverter(converterClass, null);
        } else {
            throw new IllegalArgumentException("Type converter class " + clazz.getName() + " doesn't implement " + TypeConverter.class.getName());
        }
    }

}
