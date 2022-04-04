/*
 * $Id$
 *
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
package org.apache.struts2.spring;

import org.springframework.beans.factory.support.SimpleInstantiationStrategy;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Constructor;

/**
 * Same as SimpleInstantiationStrategy, but constructor is not cached
 */
public class ClassReloadingInstantiationStrategy extends SimpleInstantiationStrategy {
    public Object instantiate(
            RootBeanDefinition beanDefinition, String beanName, BeanFactory owner) {

        // Don't override the class with CGLIB if no overrides.
        if (beanDefinition.getMethodOverrides().isEmpty()) {
            Class clazz = beanDefinition.getBeanClass();
            if (clazz.isInterface()) {
                throw new BeanInstantiationException(clazz, "Specified class is an interface");
            }
            try {
                Constructor constructor = clazz.getDeclaredConstructor((Class[]) null);
                return BeanUtils.instantiateClass(constructor, null);
            }
            catch (Exception ex) {
                throw new BeanInstantiationException(clazz, "No default constructor found", ex);
            }
        } else {
            // Must generate CGLIB subclass.
            return instantiateWithMethodInjection(beanDefinition, beanName, owner);
        }
    }
}
