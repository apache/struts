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
import ognl.DefaultMemberAccess;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.*;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Allows access decisions to be made on the basis of whether a member is static or not.
 * Also blocks or allows access to properties.
 */
public class SecurityMemberAccess extends DefaultMemberAccess {

    private static final Logger LOG = LogManager.getLogger(SecurityMemberAccess.class);

    private final boolean allowStaticMethodAccess;
    private Set<Pattern> excludeProperties = Collections.emptySet();
    private Set<Pattern> acceptProperties = Collections.emptySet();
    private Set<Class<?>> excludedClasses = Collections.emptySet();
    private Set<Pattern> excludedPackageNamePatterns = Collections.emptySet();
    private Set<String> excludedPackageNames = Collections.emptySet();
    private boolean disallowProxyMemberAccess;

    public SecurityMemberAccess(boolean method) {
        super(false);
        allowStaticMethodAccess = method;
    }

    public boolean getAllowStaticMethodAccess() {
        return allowStaticMethodAccess;
    }

    @Override
    public boolean isAccessible(Map context, Object target, Member member, String propertyName) {
        LOG.debug("Checking access for [target: {}, member: {}, property: {}]", target, member, propertyName);

        if (checkEnumAccess(target, member)) {
            LOG.trace("Allowing access to enum: {}", target);
            return true;
        }

        Class targetClass = target.getClass();
        Class memberClass = member.getDeclaringClass();

        if (Modifier.isStatic(member.getModifiers()) && allowStaticMethodAccess) {
            LOG.debug("Support for accessing static methods [target: {}, member: {}, property: {}] is deprecated!", target, member, propertyName);
            if (!isClassExcluded(member.getDeclaringClass())) {
                targetClass = member.getDeclaringClass();
            }
        }

        if (isPackageExcluded(targetClass.getPackage(), memberClass.getPackage())) {
            LOG.warn("Package of target [{}] or package of member [{}] are excluded!", target, member);
            return false;
        }

        if (isClassExcluded(targetClass)) {
            LOG.warn("Target class [{}] is excluded!", target);
            return false;
        }

        if (isClassExcluded(memberClass)) {
            LOG.warn("Declaring class of member type [{}] is excluded!", member);
            return false;
        }

        if (disallowProxyMemberAccess && ProxyUtil.isProxyMember(member, target)) {
            LOG.warn("Access to proxy [{}] is blocked!", member);
            return false;
        }

        boolean allow = true;
        if (!checkStaticMethodAccess(member)) {
            LOG.warn("Access to static [{}] is blocked!", member);
            allow = false;
        }

        //failed static test
        if (!allow) {
            return false;
        }

        // Now check for standard scope rules
        return super.isAccessible(context, target, member, propertyName) && isAcceptableProperty(propertyName);
    }

    protected boolean checkStaticMethodAccess(Member member) {
        int modifiers = member.getModifiers();
        if (Modifier.isStatic(modifiers)) {
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
        
        final String targetPackageName = targetPackage == null ? "" : targetPackage.getName();
        final String memberPackageName = memberPackage == null ? "" : memberPackage.getName();

        for (Pattern pattern : excludedPackageNamePatterns) {
            if (pattern.matcher(targetPackageName).matches() || pattern.matcher(memberPackageName).matches()) {
                return true;
            }
        }

        for (String packageName: excludedPackageNames) {
            if (targetPackageName.startsWith(packageName) || targetPackageName.equals(packageName)
                    || memberPackageName.startsWith(packageName) || memberPackageName.equals(packageName)) {
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
