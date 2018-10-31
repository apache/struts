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
package com.opensymphony.xwork2.ognl;

import com.opensymphony.xwork2.util.ProxyUtil;
import ognl.MemberAccess;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Allows access decisions to be made on the basis of whether a member is static or not.
 * Also blocks or allows access to properties.
 */
public class SecurityMemberAccess implements MemberAccess {

    private static final Logger LOG = LogManager.getLogger(SecurityMemberAccess.class);

    private final boolean allowStaticMethodAccess;
    private Set<Pattern> excludeProperties = Collections.emptySet();
    private Set<Pattern> acceptProperties = Collections.emptySet();
    private Set<Class<?>> excludedClasses = Collections.emptySet();
    private Set<Pattern> excludedPackageNamePatterns = Collections.emptySet();
    private Set<String> excludedPackageNames = Collections.emptySet();
    private boolean disallowProxyMemberAccess;

    public SecurityMemberAccess(boolean method) {
        allowStaticMethodAccess = method;
    }

    public boolean getAllowStaticMethodAccess() {
        return allowStaticMethodAccess;
    }

    @Override
    public Object setup(Map context, Object target, Member member, String propertyName) {
        Object result = null;

        if (isAccessible(context, target, member, propertyName)) {
            AccessibleObject accessible = (AccessibleObject) member;

            if (!accessible.isAccessible()) {
                result = Boolean.FALSE;
                accessible.setAccessible(true);
            }
        }
        return result;
    }

    @Override
    public void restore(Map context, Object target, Member member, String propertyName, Object state) {
        if (state != null) {
            ((AccessibleObject) member).setAccessible((Boolean) state);
        }
    }

    @Override
    public boolean isAccessible(Map context, Object target, Member member, String propertyName) {
        LOG.debug("Checking access for [target: {}, member: {}, property: {}]", target, member, propertyName);

        if (checkEnumAccess(target, member)) {
            LOG.trace("Allowing access to enum: target [{}], member [{}]", target, member);
            return true;
        }

        if (!checkStaticMethodAccess(member)) {
            LOG.warn("Access to static [{}] is blocked!", member);
            return false;
        }

        final Class memberClass = member.getDeclaringClass();

        if (isClassExcluded(memberClass)) {
            LOG.warn("Declaring class of member type [{}] is excluded!", member);
            return false;
        }

        // target can be null in case of accessing static fields, since OGNL 3.2.8
        Class targetClass = Modifier.isStatic(member.getModifiers()) ? memberClass : target.getClass();

        if (isPackageExcluded(targetClass.getPackage(), memberClass.getPackage())) {
            LOG.warn("Package [{}] of target class [{}] of target [{}] or package [{}] of member [{}] are excluded!", targetClass.getPackage(), targetClass,
                    target, memberClass.getPackage(), member);
            return false;
        }

        if (isClassExcluded(targetClass)) {
            LOG.warn("Target class [{}] of target [{}] is excluded!", targetClass, target);
            return false;
        }

        if (disallowProxyMemberAccess && ProxyUtil.isProxyMember(member, target)) {
            LOG.warn("Access to proxy is blocked! Target class [{}] of target [{}], member [{}]", targetClass, target, member);
            return false;
        }

        return Modifier.isPublic(member.getModifiers()) && isAcceptableProperty(propertyName);
    }

    protected boolean checkStaticMethodAccess(Member member) {
        int modifiers = member.getModifiers();
        if (Modifier.isStatic(modifiers)) {
            if (allowStaticMethodAccess) {
                LOG.debug("Support for accessing static methods [member: {}] is deprecated!", member);
            }
            return allowStaticMethodAccess;
        } else {
            return true;
        }
    }

    protected boolean checkEnumAccess(Object target, Member member) {
        if (target instanceof Class) {
            Class clazz = (Class) target;
            if (Enum.class.isAssignableFrom(clazz) && member.getName().equals("values")) {
                return true;
            }
        }
        return false;
    }

    protected boolean isPackageExcluded(Package targetPackage, Package memberPackage) {
        if (targetPackage == null || memberPackage == null) {
            LOG.warn("The use of the default (unnamed) package is discouraged!");
        }
        
        String targetPackageName = targetPackage == null ? "" : targetPackage.getName();
        String memberPackageName = memberPackage == null ? "" : memberPackage.getName();

        for (Pattern pattern : excludedPackageNamePatterns) {
            if (pattern.matcher(targetPackageName).matches() || pattern.matcher(memberPackageName).matches()) {
                return true;
            }
        }

        targetPackageName = targetPackageName + ".";
        memberPackageName = memberPackageName + ".";

        for (String packageName: excludedPackageNames) {
            if (targetPackageName.startsWith(packageName) || memberPackageName.startsWith(packageName)) {
                return true;
            }
        }

        return false;
    }

    protected boolean isClassExcluded(Class<?> clazz) {
        if (clazz == Object.class || (clazz == Class.class && !allowStaticMethodAccess)) {
            return true;
        }
        for (Class<?> excludedClass : excludedClasses) {
            if (clazz.isAssignableFrom(excludedClass)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isAcceptableProperty(String name) {
        return name == null || ((!isExcluded(name)) && isAccepted(name));
    }

    protected boolean isAccepted(String paramName) {
        if (!this.acceptProperties.isEmpty()) {
            for (Pattern pattern : acceptProperties) {
                Matcher matcher = pattern.matcher(paramName);
                if (matcher.matches()) {
                    return true;
                }
            }

            //no match, but acceptedParams is not empty
            return false;
        }

        //empty acceptedParams
        return true;
    }

    protected boolean isExcluded(String paramName) {
        if (!this.excludeProperties.isEmpty()) {
            for (Pattern pattern : excludeProperties) {
                Matcher matcher = pattern.matcher(paramName);
                if (matcher.matches()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setExcludeProperties(Set<Pattern> excludeProperties) {
        this.excludeProperties = excludeProperties;
    }

    public void setAcceptProperties(Set<Pattern> acceptedProperties) {
        this.acceptProperties = acceptedProperties;
    }

    public void setExcludedClasses(Set<Class<?>> excludedClasses) {
        this.excludedClasses = excludedClasses;
    }

    public void setExcludedPackageNamePatterns(Set<Pattern> excludedPackageNamePatterns) {
        this.excludedPackageNamePatterns = excludedPackageNamePatterns;
    }

    public void setExcludedPackageNames(Set<String> excludedPackageNames) {
        this.excludedPackageNames = excludedPackageNames;
    }

    public void setDisallowProxyMemberAccess(boolean disallowProxyMemberAccess) {
        this.disallowProxyMemberAccess = disallowProxyMemberAccess;
    }
}
