/*
 * Copyright 2017 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2.spring;

import com.opensymphony.xwork2.util.ClassLoaderUtil;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Proxy;

/**
 * <code>SpringUtils</code>
 * <p>
 * Various utility methods dealing with spring framework
 * </p>
 *
 */
public class SpringUtils {
    /**
     * Get the ultimate <em>target</em> object of the supplied {@code candidate}
     * object, unwrapping not only a top-level proxy but also any number of
     * nested proxies.
     * <p>If the supplied {@code candidate} is a Spring proxy, the ultimate target of all
     * nested proxies will be returned; otherwise, the {@code candidate}
     * will be returned <em>as is</em>.
     * @param candidate the instance to check (potentially a Spring AOP proxy;
     * never {@code null})
     * @return the target object or the {@code candidate} (never {@code null})
     * @throws IllegalStateException if an error occurs while unwrapping a proxy
     */
    public static <T> T getUltimateTargetObject(Object candidate) {
        try {
            if (isAopProxy(candidate) &&
                    implementsInterface(candidate.getClass(), "org.springframework.aop.framework.Advised")) {
                Object targetSource = MethodUtils.invokeMethod(candidate, "getTargetSource");
                Object target = MethodUtils.invokeMethod(targetSource, "getTarget");
                return getUltimateTargetObject(target);
            }
        }
        catch (Throwable ex) {
            throw new IllegalStateException("Failed to unwrap proxied object", ex);
        }
        return (T) candidate;
    }

    /**
     * Check whether the given object is a Spring proxy.
     * @param object the object to check
     */
    public static boolean isAopProxy(Object object) {
        Class<?> clazz = object.getClass();
        return (implementsInterface(clazz, "org.springframework.aop.SpringProxy") &&
                (Proxy.isProxyClass(clazz) || isCglibProxyClass(clazz)));
    }

    /**
     * Check whether the specified class is a CGLIB-generated class.
     * @param clazz the class to check
     */
    private static boolean isCglibProxyClass(Class<?> clazz) {
        return (clazz != null && clazz.getName().contains("$$"));
    }

    /**
     * Check whether the given class implements an interface with a given class name.
     * @param clazz the class to check
     * @param ifaceClassName the interface class name to check
     */
    private static boolean implementsInterface(Class<?> clazz, String ifaceClassName) {
        try {
            Class ifaceClass = ClassLoaderUtil.loadClass(ifaceClassName, SpringUtils.class);
            return ifaceClass.isAssignableFrom(clazz);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
