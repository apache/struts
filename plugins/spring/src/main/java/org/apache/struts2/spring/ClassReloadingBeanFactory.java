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

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ClassUtils;
import org.springframework.core.DecoratingClassLoader;

import java.lang.reflect.Constructor;


/**
 *  Same as DefaultListableBeanFactory, but it doesn't use the constructor and class cached in RootBeanDefinition
 */
public class ClassReloadingBeanFactory extends DefaultListableBeanFactory {
    @Override
    protected BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, Object[] args) {
        Class beanClass = resolveBeanClass(mbd, beanName, null);

        if (mbd.getFactoryMethodName() != null) {
            return instantiateUsingFactoryMethod(beanName, mbd, args);
        }

        //commented to cached constructor is not used
        /* // Shortcut when re-creating the same bean...
        if (mbd.resolvedConstructorOrFactoryMethod != null) {
            if (mbd.constructorArgumentsResolved) {
                return autowireConstructor(beanName, mbd, null, args);
            } else {
                return instantiateBean(beanName, mbd);
            }
        }*/

        // Need to determine the constructor...
        Constructor[] ctors = determineConstructorsFromBeanPostProcessors(beanClass, beanName);
        if (ctors != null ||
                mbd.getResolvedAutowireMode() == RootBeanDefinition.AUTOWIRE_CONSTRUCTOR ||
                mbd.hasConstructorArgumentValues() || !ObjectUtils.isEmpty(args)) {
            return autowireConstructor(beanName, mbd, ctors, args);
        }

        // No special handling: simply use no-arg constructor.
        return instantiateBean(beanName, mbd);
    }

    protected Class resolveBeanClass(RootBeanDefinition mbd, String beanName, Class[] typesToMatch) {
        try {
             //commented to cached class is not used
            /* if (mbd.hasBeanClass()) {
                return mbd.getBeanClass();
            }*/
            if (typesToMatch != null) {
                ClassLoader tempClassLoader = getTempClassLoader();
                if (tempClassLoader != null) {
                    if (tempClassLoader instanceof DecoratingClassLoader) {
                        DecoratingClassLoader dcl = (DecoratingClassLoader) tempClassLoader;
                        for (int i = 0; i < typesToMatch.length; i++) {
                            dcl.excludeClass(typesToMatch[i].getName());
                        }
                    }
                    String className = mbd.getBeanClassName();
                    return (className != null ? ClassUtils.forName(className, tempClassLoader) : null);
                }
            }
            return mbd.resolveBeanClass(getBeanClassLoader());
        }
        catch (ClassNotFoundException ex) {
            throw new CannotLoadBeanClassException(mbd.getResourceDescription(), beanName, mbd.getBeanClassName(), ex);
        }
        catch (LinkageError err) {
            throw new CannotLoadBeanClassException(mbd.getResourceDescription(), beanName, mbd.getBeanClassName(), err);
        }
    }
}
