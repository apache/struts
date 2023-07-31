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
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
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

    private final boolean allowStaticFieldAccess;
    private Set<Pattern> excludeProperties = Collections.emptySet();
    private Set<Pattern> acceptProperties = Collections.emptySet();
    private Set<Class<?>> excludedClasses = Collections.emptySet();
    private Set<Pattern> excludedPackageNamePatterns = Collections.emptySet();
    private Set<String> excludedPackageNames = Collections.emptySet();
    private Set<Class<?>> excludedPackageExemptClasses = Collections.emptySet();
    private boolean disallowProxyMemberAccess;

    /**
     * SecurityMemberAccess
     * - access decisions based on whether member is static (or not)
     * - block or allow access to properties (configurable-after-construction)
     *
     * @param allowStaticFieldAccess if set to true static fields (constants) will be accessible
     */
    public SecurityMemberAccess(boolean allowStaticFieldAccess) {
        this.allowStaticFieldAccess = allowStaticFieldAccess;
    }

    @Override
    public Object setup(Map context, Object target, Member member, String propertyName) {
        Object result = null;

        if (isAccessible(context, target, member, propertyName)) {
            final AccessibleObject accessible = (AccessibleObject) member;

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
            final AccessibleObject accessible = (AccessibleObject) member;
            final boolean stateBoolean = ((Boolean) state).booleanValue();  // Using twice (avoid unboxing)
            if (!stateBoolean) {
                accessible.setAccessible(stateBoolean);
            } else {
                throw new IllegalArgumentException("Improper restore state [" + stateBoolean + "] for target [" + target +
                    "], member [" + member + "], propertyName [" + propertyName + "]");
            }
        }
    }

    @Override
    public boolean isAccessible(Map context, Object target, Member member, String propertyName) {
        LOG.debug("Checking access for [target: {}, member: {}, property: {}]", target, member, propertyName);

        final int memberModifiers = member.getModifiers();
        final Class<?> memberClass = member.getDeclaringClass();
        // target can be null in case of accessing static fields, since OGNL 3.2.8
        final Class<?> targetClass = Modifier.isStatic(memberModifiers) ? memberClass : target.getClass();
        if (!memberClass.isAssignableFrom(targetClass)) {
            throw new IllegalArgumentException("Target does not match member!");
        }

        if (!checkPublicMemberAccess(memberModifiers)) {
            LOG.warn("Access to non-public [{}] is blocked!", member);
            return false;
        }

        if (!checkStaticFieldAccess(member, memberModifiers)) {
            LOG.warn("Access to static field [{}] is blocked!", member);
            return false;
        }

        // it needs to be before calling #checkStaticMethodAccess()
        if (checkEnumAccess(target, member)) {
            LOG.trace("Allowing access to enum: target [{}], member [{}]", target, member);
            return true;
        }

        if (!checkStaticMethodAccess(member, memberModifiers)) {
            LOG.warn("Access to static method [{}] is blocked!", member);
            return false;
        }

        if (isClassExcluded(memberClass)) {
            LOG.warn("Declaring class of member type [{}] is excluded!", member);
            return false;
        }

        if (targetClass != memberClass && isClassExcluded(targetClass)) {
            // Optimization: Already checked memberClass exclusion, so if-and-only-if targetClass == memberClass, this check is redundant.
            LOG.warn("Target class [{}] of target [{}] is excluded!", targetClass, target);
            return false;
        }

        if (targetClass.getPackage() == null || memberClass.getPackage() == null) {
            LOG.warn("The use of the default (unnamed) package is discouraged!");
        }

        if (isPackageExcluded(targetClass, memberClass)) {
            LOG.warn(
                    "Package [{}] of target class [{}] of target [{}] or package [{}] of member [{}] are excluded!",
                    targetClass.getPackage(),
                    targetClass,
                    target,
                    memberClass.getPackage(),
                    member);
            return false;
        }

        if (disallowProxyMemberAccess && ProxyUtil.isProxyMember(member, target)) {
            LOG.warn("Access to proxy is blocked! Target class [{}] of target [{}], member [{}]", targetClass, target, member);
            return false;
        }

        return isAcceptableProperty(propertyName);
    }

    /**
     * Check access for static method (via modifiers).
     *
     * Note: For non-static members, the result is always true.
     *
     * @param member
     * @param memberModifiers
     *
     * @return
     */
    protected boolean checkStaticMethodAccess(Member member, int memberModifiers) {
        return !Modifier.isStatic(memberModifiers) || member instanceof Field;
    }

    /**
     * Check access for static field (via modifiers).
     * <p>
     * Note: For non-static members, the result is always true.
     *
     * @param member
     * @param memberModifiers
     * @return
     */
    protected boolean checkStaticFieldAccess(Member member, int memberModifiers) {
        if (Modifier.isStatic(memberModifiers) && member instanceof Field) {
            return allowStaticFieldAccess;
        } else {
            return true;
        }
    }

    /**
     * Check access for public members (via modifiers)
     * <p>
     * Returns true if-and-only-if the member is public.
     *
     * @param memberModifiers
     * @return
     */
    protected boolean checkPublicMemberAccess(int memberModifiers) {
        return Modifier.isPublic(memberModifiers);
    }

    protected boolean checkEnumAccess(Object target, Member member) {
        if (target instanceof Class) {
            final Class<?> clazz = (Class<?>) target;
            return Enum.class.isAssignableFrom(clazz) && member.getName().equals("values");
        }
        return false;
    }

    protected boolean isPackageExcluded(Class<?> targetClass, Class<?> memberClass) {
        if (targetClass == null || memberClass == null) {
            throw new IllegalArgumentException(
                    "Parameters should never be null - if member is static, targetClass should be the same as memberClass.");
        }

        Set<Class<?>> classesToCheck = new HashSet<>();
        classesToCheck.add(targetClass);
        classesToCheck.add(memberClass);

        for (Class<?> clazz : classesToCheck) {
            if (!isExcludedPackageExempt(clazz) && (isExcludedPackageNamePatterns(clazz) || isExcludedPackageNames(clazz))) {
                return true;
            }
        }
        return false;
    }

    protected String toPackageName(Class<?> clazz) {
        if (clazz.getPackage() == null) {
            return "";
        } else {
            return clazz.getPackage().getName();
        }
    }

    protected boolean isExcludedPackageNamePatterns(Class<?> clazz) {
        String packageName = toPackageName(clazz);
        for (Pattern pattern : excludedPackageNamePatterns) {
            if (pattern.matcher(packageName).matches()) {
                return true;
            }
        }
        return false;
    }

    protected boolean isExcludedPackageNames(Class<?> clazz) {
        String suffixedPackageName = toPackageName(clazz) + ".";
        for (String excludedPackageName : excludedPackageNames) {
            if (suffixedPackageName.startsWith(excludedPackageName)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isClassExcluded(Class<?> clazz) {
        if (clazz == Object.class || (clazz == Class.class && !allowStaticFieldAccess)) {
            return true;
        }
        return excludedClasses.stream().anyMatch(clazz::isAssignableFrom);
    }

    protected boolean isExcludedPackageExempt(Class<?> clazz) {
        return excludedPackageExemptClasses.stream().anyMatch(clazz::equals);
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

    /**
     * @deprecated please use {@link #useExcludeProperties(Set)}
     */
    @Deprecated
    public void setExcludeProperties(Set<Pattern> excludeProperties) {
        this.excludeProperties = excludeProperties;
    }

    public void useExcludeProperties(Set<Pattern> excludeProperties) {
        this.excludeProperties = excludeProperties;
    }

    /**
     * @deprecated please use {@link #useAcceptProperties(Set)}
     */
    @Deprecated
    public void setAcceptProperties(Set<Pattern> acceptedProperties) {
        this.acceptProperties = acceptedProperties;
    }

    public void useAcceptProperties(Set<Pattern> acceptedProperties) {
        this.acceptProperties = acceptedProperties;
    }

    /**
     * @deprecated please use {@link #useExcludedClasses(Set)}
     */
    @Deprecated
    public void setExcludedClasses(Set<Class<?>> excludedClasses) {
        this.excludedClasses = excludedClasses;
    }

    public void useExcludedClasses(Set<Class<?>> excludedClasses) {
        this.excludedClasses = excludedClasses;
    }

    /**
     * @deprecated please use {@link #useExcludedPackageNamePatterns(Set)}
     */
    @Deprecated
    public void setExcludedPackageNamePatterns(Set<Pattern> excludedPackageNamePatterns) {
        this.excludedPackageNamePatterns = excludedPackageNamePatterns;
    }

    public void useExcludedPackageNamePatterns(Set<Pattern> excludedPackageNamePatterns) {
        this.excludedPackageNamePatterns = excludedPackageNamePatterns;
    }

    /**
     * @deprecated please use {@link #useExcludedPackageNames(Set)}
     */
    @Deprecated
    public void setExcludedPackageNames(Set<String> excludedPackageNames) {
        this.excludedPackageNames = excludedPackageNames;
    }

    public void useExcludedPackageNames(Set<String> excludedPackageNames) {
        this.excludedPackageNames = excludedPackageNames;
    }

    /**
     * @deprecated please use {@link #useExcludedPackageExemptClasses(Set)}
     */
    @Deprecated
    public void setExcludedPackageExemptClasses(Set<Class<?>> excludedPackageExemptClasses) {
        this.excludedPackageExemptClasses = excludedPackageExemptClasses;
    }

    public void useExcludedPackageExemptClasses(Set<Class<?>> excludedPackageExemptClasses) {
        this.excludedPackageExemptClasses = excludedPackageExemptClasses;
    }

    /**
     * @deprecated please use {@link #disallowProxyMemberAccess(boolean)}
     */
    @Deprecated
    public void setDisallowProxyMemberAccess(boolean disallowProxyMemberAccess) {
        this.disallowProxyMemberAccess = disallowProxyMemberAccess;
    }

    public void disallowProxyMemberAccess(boolean disallowProxyMemberAccess) {
        this.disallowProxyMemberAccess = disallowProxyMemberAccess;
    }
}
