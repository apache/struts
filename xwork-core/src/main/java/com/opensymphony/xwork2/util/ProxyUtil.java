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
package com.opensymphony.xwork2.util;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <code>ProxyUtil</code>
 * <p>
 * Various utility methods dealing with proxies
 * </p>
 *
 */
public class ProxyUtil {
    private static final String SPRING_ADVISED_CLASS_NAME = "org.springframework.aop.framework.Advised";
    private static final String SPRING_SPRINGPROXY_CLASS_NAME = "org.springframework.aop.SpringProxy";
    private static final String SPRING_TARGETCLASSAWARE_CLASS_NAME = "org.springframework.aop.TargetClassAware";

    private static final Map<Class<?>, Boolean> isProxyCache =
            new ConcurrentHashMap<Class<?>, Boolean>(256);
    private static final Map<Member, Boolean> isProxyMemberCache =
            new ConcurrentHashMap<Member, Boolean>(256);

    /**
     * Check whether the given member is a proxy member of a proxy object.
     * @param member the member to check
     * @param object the object to check
     */
    public static boolean isProxyMember(Member member, Object object) {
        if (!isProxy(object))
            return false;

        Boolean flag = isProxyMemberCache.get(member);
        if (flag != null) {
            return flag;
        }

        boolean isProxyMember = isSpringProxyMember(member);

        isProxyMemberCache.put(member, isProxyMember);
        return isProxyMember;
    }

    /**
     * Check whether the given object is a proxy.
     * @param object the object to check
     */
    private static boolean isProxy(Object object) {
        Class<?> clazz = object.getClass();
        Boolean flag = isProxyCache.get(clazz);
        if (flag != null) {
            return flag;
        }

        boolean isProxy = isSpringAopProxy(object);

        isProxyCache.put(clazz, isProxy);
        return isProxy;
    }

    /**
     * Check whether the given object is a Spring proxy.
     * @param object the object to check
     */
    private static boolean isSpringAopProxy(Object object) {
        Class<?> clazz = object.getClass();
        return (implementsInterface(clazz, SPRING_SPRINGPROXY_CLASS_NAME) && (Proxy.isProxyClass(clazz)
                || isCglibProxyClass(clazz)));
    }

    /**
     * Check whether the given member is a member of a spring proxy.
     * @param member the member to check
     */
    private static boolean isSpringProxyMember(Member member) {
        try {
            Class<?> clazz = ClassLoaderUtil.loadClass(SPRING_ADVISED_CLASS_NAME, ProxyUtil.class);
            if (hasMember(clazz, member))
                return true;
            clazz = ClassLoaderUtil.loadClass(SPRING_TARGETCLASSAWARE_CLASS_NAME, ProxyUtil.class);
            if (hasMember(clazz, member))
                return true;
            clazz = ClassLoaderUtil.loadClass(SPRING_SPRINGPROXY_CLASS_NAME, ProxyUtil.class);
            if (hasMember(clazz, member))
                return true;
        } catch (ClassNotFoundException ignored) {
        }

        return false;
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
            Class<?> ifaceClass = ClassLoaderUtil.loadClass(ifaceClassName, ProxyUtil.class);
            return ifaceClass.isAssignableFrom(clazz);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Check whether the given class has a given member.
     * @param clazz the class to check
     * @param member the member to check
     */
    private static boolean hasMember(Class<?> clazz, Member member) {
        if (member instanceof Method) {
            return null != MethodUtils.getMatchingAccessibleMethod(clazz, member.getName(), ((Method) member).getParameterTypes());
        }
        if (member instanceof Field) {
            return null != FieldUtils.getField(clazz, member.getName(), true);
        }
        if (member instanceof Constructor) {
            return null != ConstructorUtils.getMatchingAccessibleConstructor(clazz, ((Constructor) member).getParameterTypes());
        }

        return false;
    }
}