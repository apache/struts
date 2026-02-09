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

import java.lang.reflect.Member;

/**
 * Service interface for proxy detection and resolution operations.
 * Replaces static {@link ProxyUtil} methods with an injectable service.
 *
 * @since 7.2.0
 */
public interface ProxyService {

    /**
     * Determine the ultimate target class of the given instance, traversing
     * not only a top-level proxy but any number of nested proxies as well &amp;mdash;
     * as long as possible without side effects.
     *
     * @param candidate the instance to check (might be a proxy)
     * @return the ultimate target class (or the plain class of the given
     * object as fallback; never {@code null})
     */
    Class<?> ultimateTargetClass(Object candidate);

    /**
     * Check whether the given object is a proxy.
     *
     * @param object the object to check
     * @return true if the object is a Spring AOP or Hibernate proxy
     */
    boolean isProxy(Object object);

    /**
     * Check whether the given member is a proxy member of a proxy object or is a static proxy member.
     *
     * @param member the member to check
     * @param object the object to check
     * @return true if the member is a proxy member
     */
    boolean isProxyMember(Member member, Object object);

    /**
     * Check whether the given object is a Hibernate proxy.
     *
     * @param object the object to check
     * @return true if the object is a Hibernate proxy
     */
    boolean isHibernateProxy(Object object);

    /**
     * Check whether the given member is a member of a Hibernate proxy.
     *
     * @param member the member to check
     * @return true if the member is a Hibernate proxy member
     */
    boolean isHibernateProxyMember(Member member);

    /**
     * Get the target instance of the given object if it is a Hibernate proxy object,
     * otherwise return the given object.
     *
     * @param object the object to check
     * @return the target instance or the original object
     */
    Object getHibernateProxyTarget(Object object);

    /**
     * Resolve matching member on target class.
     *
     * @param proxyMember the proxy member
     * @param targetClass the target class
     * @return matching member on target object if one exists, otherwise the same member
     */
    Member resolveTargetMember(Member proxyMember, Class<?> targetClass);

    /**
     * @param proxyMember the proxy member
     * @param target      the target object
     * @return matching member on target object if one exists, otherwise the same member
     * @deprecated since 7.1, use {@link #resolveTargetMember(Member, Class)} instead.
     */
    @Deprecated
    Member resolveTargetMember(Member proxyMember, Object target);
}
