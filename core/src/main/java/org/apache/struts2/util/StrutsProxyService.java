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
import org.apache.struts2.inject.Inject;
import org.apache.struts2.ognl.OgnlCache;
import org.apache.struts2.ognl.ProxyCacheFactory;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.aop.TargetClassAware;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.SpringProxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;

/**
 * Default implementation of {@link ProxyService}.
 * Provides proxy detection and resolution for Spring AOP and Hibernate proxies.
 *
 * @since 7.2.0
 */
public class StrutsProxyService implements ProxyService {

    private final OgnlCache<Class<?>, Boolean> isProxyCache;
    private final OgnlCache<Member, Boolean> isProxyMemberCache;

    @Inject
    @SuppressWarnings("unchecked")
    public StrutsProxyService(ProxyCacheFactory<?, ?> proxyCacheFactory) {
        this.isProxyCache = (OgnlCache<Class<?>, Boolean>) proxyCacheFactory.buildOgnlCache();
        this.isProxyMemberCache = (OgnlCache<Member, Boolean>) proxyCacheFactory.buildOgnlCache();
    }

    @Override
    public Class<?> ultimateTargetClass(Object candidate) {
        Class<?> result = null;
        if (isSpringAopProxy(candidate)) {
            result = springUltimateTargetClass(candidate);
        } else if (isHibernateProxy(candidate)) {
            result = getHibernateProxyTarget(candidate).getClass();
        }
        if (result == null) {
            result = candidate.getClass();
        }
        return result;
    }

    @Override
    public boolean isProxy(Object object) {
        if (object == null) return false;
        return isProxyCache.computeIfAbsent(object.getClass(),
                k -> isSpringAopProxy(object) || isHibernateProxy(object));
    }

    @Override
    public boolean isProxyMember(Member member, Object object) {
        if (!isStatic(member.getModifiers()) && !isProxy(object)) {
            return false;
        }
        return isProxyMemberCache.computeIfAbsent(member,
                k -> isSpringProxyMember(member) || isHibernateProxyMember(member));
    }

    @Override
    public boolean isHibernateProxy(Object object) {
        try {
            return object != null && HibernateProxy.class.isAssignableFrom(object.getClass());
        } catch (LinkageError ignored) {
            return false;
        }
    }

    @Override
    public boolean isHibernateProxyMember(Member member) {
        try {
            return hasMember(HibernateProxy.class, member);
        } catch (LinkageError ignored) {
            return false;
        }
    }

    @Override
    public Object getHibernateProxyTarget(Object object) {
        try {
            return Hibernate.unproxy(object);
        } catch (LinkageError ignored) {
            return object;
        }
    }

    @Override
    public Member resolveTargetMember(Member proxyMember, Class<?> targetClass) {
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

    @Override
    @Deprecated
    public Member resolveTargetMember(Member proxyMember, Object target) {
        return resolveTargetMember(proxyMember, target.getClass());
    }

    /**
     * Determine the ultimate target class of the given spring bean instance.
     */
    private Class<?> springUltimateTargetClass(Object candidate) {
        try {
            return AopProxyUtils.ultimateTargetClass(candidate);
        } catch (LinkageError ignored) {
            return candidate.getClass();
        }
    }

    /**
     * Check whether the given object is a Spring proxy.
     */
    private boolean isSpringAopProxy(Object object) {
        try {
            return AopUtils.isAopProxy(object);
        } catch (LinkageError ignored) {
            return false;
        }
    }

    /**
     * Check whether the given member is a member of a spring proxy.
     */
    private boolean isSpringProxyMember(Member member) {
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
     */
    private boolean hasMember(Class<?> clazz, Member member) {
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
}
