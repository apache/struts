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
package org.apache.struts2.util;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.struts2.ognl.DefaultOgnlCacheFactory;
import org.apache.struts2.ognl.OgnlCache;
import org.apache.struts2.ognl.OgnlCacheFactory;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.aop.SpringProxy;
import org.springframework.aop.TargetClassAware;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;

/**
 * <code>ProxyUtil</code>
 * <p>
 * Various utility methods dealing with proxies
 * </p>
 *
 */
public class ProxyUtil {
    private static final int CACHE_MAX_SIZE = 10000;
    private static final int CACHE_INITIAL_CAPACITY = 256;
    private static final OgnlCache<Class<?>, Boolean> isProxyCache = new DefaultOgnlCacheFactory<Class<?>, Boolean>(
            CACHE_MAX_SIZE, OgnlCacheFactory.CacheType.WTLFU, CACHE_INITIAL_CAPACITY).buildOgnlCache();
    private static final OgnlCache<Member, Boolean> isProxyMemberCache = new DefaultOgnlCacheFactory<Member, Boolean>(
            CACHE_MAX_SIZE, OgnlCacheFactory.CacheType.WTLFU, CACHE_INITIAL_CAPACITY).buildOgnlCache();
    private static final OgnlCache<Object, Class<?>> targetClassCache = new DefaultOgnlCacheFactory<Object, Class<?>>(
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
        return targetClassCache.computeIfAbsent(candidate, k -> {
            Class<?> result = null;
            if (isSpringAopProxy(k)) {
                result = springUltimateTargetClass(k);
            } else if (isHibernateProxy(k)) {
                result = getHibernateProxyTarget(k).getClass();
            }
            if (result == null) {
                result = k.getClass();
            }
            return result;
        });
    }

    /**
     * Check whether the given object is a proxy.
     * @param object the object to check
     */
    public static boolean isProxy(Object object) {
        if (object == null) return false;
        return isProxyCache.computeIfAbsent(object.getClass(),
                k -> isSpringAopProxy(object) || isHibernateProxy(object));
    }

    /**
     * Check whether the given member is a proxy member of a proxy object or is a static proxy member.
     * @param member the member to check
     * @param object the object to check
     */
    public static boolean isProxyMember(Member member, Object object) {
        if (!isStatic(member.getModifiers()) && !isProxy(object)) {
            return false;
        }
        return isProxyMemberCache.computeIfAbsent(member,
                k -> isSpringProxyMember(member) || isHibernateProxyMember(member));
    }

    /**
     * Check whether the given object is a Hibernate proxy.
     *
     * @param object the object to check
     */
    public static boolean isHibernateProxy(Object object) {
        try {
            return object != null && HibernateProxy.class.isAssignableFrom(object.getClass());
        } catch (LinkageError ignored) {
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
            return hasMember(HibernateProxy.class, member);
        } catch (LinkageError ignored) {
            return false;
        }
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
        try {
            return AopProxyUtils.ultimateTargetClass(candidate);
        } catch (LinkageError ignored) {
            return candidate.getClass();
        }
    }

    /**
     * Check whether the given object is a Spring proxy.
     * @param object the object to check
     */
    private static boolean isSpringAopProxy(Object object) {
        try {
            return AopUtils.isAopProxy(object);
        } catch (LinkageError ignored) {
            return false;
        }
    }

    /**
     * Check whether the given member is a member of a spring proxy.
     * @param member the member to check
     */
    private static boolean isSpringProxyMember(Member member) {
        try {
            if (hasMember(Advised.class, member))
                return true;
            if (hasMember(TargetClassAware.class, member))
                return true;
            if (hasMember(SpringProxy.class, member))
                return true;
        } catch (LinkageError ignored) {
        }
        return false;
    }

    /**
     * Check whether the given class has a given member.
     * @param clazz the class to check
     * @param member the member to check
     */
    private static boolean hasMember(Class<?> clazz, Member member) {
        if (member instanceof Method method) {
            return null != MethodUtils.getMatchingMethod(clazz, member.getName(), method.getParameterTypes());
        }
        if (member instanceof Field) {
            return null != FieldUtils.getField(clazz, member.getName(), true);
        }
        if (member instanceof Constructor<?> constructor) {
            return null != ConstructorUtils.getMatchingAccessibleConstructor(clazz, constructor.getParameterTypes());
        }
        return false;
    }

    /**
     * @return the target instance of the given object if it is a Hibernate proxy object, otherwise the given object
     */
    public static Object getHibernateProxyTarget(Object object) {
        try {
            return Hibernate.unproxy(object);
        } catch (LinkageError ignored) {
            return object;
        }
    }

    /**
     * @deprecated since 7.1, use {@link #resolveTargetMember(Member, Class)} instead.
     */
    @Deprecated
    public static Member resolveTargetMember(Member proxyMember, Object target) {
        return resolveTargetMember(proxyMember, target.getClass());
    }

    /**
     * @return matching member on target object if one exists, otherwise the same member
     */
    public static Member resolveTargetMember(Member proxyMember, Class<?> targetClass) {
        int mod = proxyMember.getModifiers();
        if (proxyMember instanceof Method) {
            if (isPublic(mod)) {
                return MethodUtils.getMatchingAccessibleMethod(targetClass, proxyMember.getName(), ((Method) proxyMember).getParameterTypes());
            } else {
                return MethodUtils.getMatchingMethod(targetClass, proxyMember.getName(), ((Method) proxyMember).getParameterTypes());
            }
        } else if (proxyMember instanceof Field) {
            return FieldUtils.getField(targetClass, proxyMember.getName(), isPublic(mod));
        } else if (proxyMember instanceof Constructor && isPublic(mod)) {
            return ConstructorUtils.getMatchingAccessibleConstructor(targetClass, ((Constructor<?>) proxyMember).getParameterTypes());
        }
        return proxyMember;
    }
}
