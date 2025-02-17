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
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.ognl.DefaultOgnlCacheFactory;
import com.opensymphony.xwork2.ognl.OgnlCache;
import com.opensymphony.xwork2.ognl.OgnlCacheFactory;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

import static java.lang.reflect.Modifier.isPublic;

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
    private static final String SPRING_SINGLETONTARGETSOURCE_CLASS_NAME = "org.springframework.aop.target.SingletonTargetSource";
    private static final String SPRING_TARGETCLASSAWARE_CLASS_NAME = "org.springframework.aop.TargetClassAware";
    private static final String HIBERNATE_HIBERNATEPROXY_CLASS_NAME = "org.hibernate.proxy.HibernateProxy";
    private static final int CACHE_MAX_SIZE = 10000;
    private static final int CACHE_INITIAL_CAPACITY = 256;
    private static final OgnlCache<Class<?>, Boolean> isProxyCache = new DefaultOgnlCacheFactory<Class<?>, Boolean>(
            CACHE_MAX_SIZE, OgnlCacheFactory.CacheType.WTLFU, CACHE_INITIAL_CAPACITY).buildOgnlCache();
    private static final OgnlCache<Member, Boolean> isProxyMemberCache = new DefaultOgnlCacheFactory<Member, Boolean>(
            CACHE_MAX_SIZE, OgnlCacheFactory.CacheType.WTLFU, CACHE_INITIAL_CAPACITY).buildOgnlCache();

    /**
     * Determine the ultimate target class of the given instance, traversing
     * not only a top-level proxy but any number of nested proxies as well &mdash;
     * as long as possible without side effects.
     * @param candidate the instance to check (might be a proxy)
     * @return the ultimate target class (or the plain class of the given
     * object as fallback; never {@code null})
     */
    public static Class<?> ultimateTargetClass(Object candidate) {
        Class<?> result = null;
        if (isSpringAopProxy(candidate))
            result = springUltimateTargetClass(candidate);

        if (result == null) {
            result = candidate.getClass();
        }

        return result;
    }

    /**
     * Check whether the given object is a proxy.
     * @param object the object to check
     */
    public static boolean isProxy(Object object) {
        if (object == null) return false;
        Class<?> clazz = object.getClass();
        Boolean flag = isProxyCache.get(clazz);
        if (flag != null) {
            return flag;
        }

        boolean isProxy = isSpringAopProxy(object) || isHibernateProxy(object);

        isProxyCache.put(clazz, isProxy);
        return isProxy;
    }

    /**
     * Check whether the given member is a proxy member of a proxy object or is a static proxy member.
     * @param member the member to check
     * @param object the object to check
     */
    public static boolean isProxyMember(Member member, Object object) {
        if (!Modifier.isStatic(member.getModifiers()) && !isProxy(object) && !isHibernateProxy(object)) {
            return false;
        }

        Boolean flag = isProxyMemberCache.get(member);
        if (flag != null) {
            return flag;
        }

        boolean isProxyMember = isSpringProxyMember(member) || isHibernateProxyMember(member);

        isProxyMemberCache.put(member, isProxyMember);
        return isProxyMember;
    }

    /**
     * Check whether the given object is a Hibernate proxy.
     *
     * @param object the object to check
     */
    public static boolean isHibernateProxy(Object object) {
        try {
            return object != null && HibernateProxy.class.isAssignableFrom(object.getClass());
        } catch (NoClassDefFoundError ignored) {
            return false;
        }
    }

    /**
     * Check whether the given member is a member of a Hibernate proxy.
     *
     * @param member the member to check
     */
    public static boolean isHibernateProxyMember(Member member) {
        try {
            Class<?> clazz = ClassLoaderUtil.loadClass(HIBERNATE_HIBERNATEPROXY_CLASS_NAME, ProxyUtil.class);
            return hasMember(clazz, member);
        } catch (ClassNotFoundException ignored) {
        }

        return false;
    }

    /**
     * Determine the ultimate target class of the given spring bean instance, traversing
     * not only a top-level spring proxy but any number of nested spring proxies as well &mdash;
     * as long as possible without side effects, that is, just for singleton targets.
     * @param candidate the instance to check (might be a spring AOP proxy)
     * @return the ultimate target class (or the plain class of the given
     * object as fallback; never {@code null})
     */
    private static Class<?> springUltimateTargetClass(Object candidate) {
        Object current = candidate;
        Class<?> result = null;
        while (null != current && implementsInterface(current.getClass(), SPRING_TARGETCLASSAWARE_CLASS_NAME)) {
            try {
                result = (Class<?>) MethodUtils.invokeMethod(current, "getTargetClass");
            } catch (Throwable ignored) {
            }
            current = getSingletonTarget(current);
        }
        if (result == null) {
            Class<?> clazz = candidate.getClass();
            result = (isCglibProxyClass(clazz) ? clazz.getSuperclass() : candidate.getClass());
        }
        return result;
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
     * Obtain the singleton target object behind the given spring proxy, if any.
     * @param candidate the (potential) spring proxy to check
     * @return the singleton target object, or {@code null} in any other case
     * (not a spring proxy, not an existing singleton target)
     */
    private static Object getSingletonTarget(Object candidate) {
        try {
            if (implementsInterface(candidate.getClass(), SPRING_ADVISED_CLASS_NAME)) {
                Object targetSource = MethodUtils.invokeMethod(candidate, "getTargetSource");
                if (implementsInterface(targetSource.getClass(), SPRING_SINGLETONTARGETSOURCE_CLASS_NAME)) {
                    return MethodUtils.invokeMethod(targetSource, "getTarget");
                }
            }
        } catch (Throwable ignored) {
        }

        return null;
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
            return null != MethodUtils.getMatchingMethod(clazz, member.getName(), ((Method) member).getParameterTypes());
        }
        if (member instanceof Field) {
            return null != FieldUtils.getField(clazz, member.getName(), true);
        }
        if (member instanceof Constructor) {
            return null != ConstructorUtils.getMatchingAccessibleConstructor(clazz, ((Constructor) member).getParameterTypes());
        }

        return false;
    }

    /**
     * @return the target instance of the given object if it is a Hibernate proxy object, otherwise the given object
     */
    public static Object getHibernateProxyTarget(Object object) {
        try {
            return Hibernate.unproxy(object);
        } catch (NoClassDefFoundError ignored) {
            return object;
        }
    }

    /**
     * @return matching member on target object if one exists, otherwise the same member
     */
    public static Member resolveTargetMember(Member proxyMember, Object target) {
        int mod = proxyMember.getModifiers();
        if (proxyMember instanceof Method) {
            if (isPublic(mod)) {
                return MethodUtils.getMatchingAccessibleMethod(target.getClass(), proxyMember.getName(), ((Method) proxyMember).getParameterTypes());
            } else {
                return MethodUtils.getMatchingMethod(target.getClass(), proxyMember.getName(), ((Method) proxyMember).getParameterTypes());
            }
        } else if (proxyMember instanceof Field) {
            return FieldUtils.getField(target.getClass(), proxyMember.getName(), isPublic(mod));
        } else if (proxyMember instanceof Constructor && isPublic(mod)) {
            return ConstructorUtils.getMatchingAccessibleConstructor(target.getClass(), ((Constructor<?>) proxyMember).getParameterTypes());
        }
        return proxyMember;
    }
}
