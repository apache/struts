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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.text.MessageFormat.format;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toSet;

/**
 * Allows access decisions to be made on the basis of whether a member is static or not.
 * Also blocks or allows access to properties.
 */
public class SecurityMemberAccess implements MemberAccess {

    private static final Logger LOG = LogManager.getLogger(SecurityMemberAccess.class);

    private final boolean allowStaticFieldAccess;
    private Set<Pattern> excludeProperties = emptySet();
    private Set<Pattern> acceptProperties = emptySet();
    private Set<String> excludedClasses = emptySet();
    private Set<Pattern> excludedPackageNamePatterns = emptySet();
    private Set<String> excludedPackageNames = emptySet();
    private Set<String> excludedPackageExemptClasses = emptySet();
    private boolean disallowProxyMemberAccess;
    private boolean disallowDefaultPackageAccess;

    /**
     * SecurityMemberAccess
     * - access decisions based on whether member is static (or not)
     * - block or allow access to properties (configurable-after-construction)
     *
     * @param allowStaticFieldAccess if set to true static fields (constants) will be accessible
     */
    public SecurityMemberAccess(boolean allowStaticFieldAccess) {
        this.allowStaticFieldAccess = allowStaticFieldAccess;
        useExcludedClasses(excludedClasses); // Initialise default exclusions
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
        if (state == null) {
            return;
        }
        if ((Boolean) state) {
            throw new IllegalArgumentException(format(
                    "Improper restore state [true] for target [{0}], member [{1}], propertyName [{2}]",
                    target,
                    member,
                    propertyName));
        }
        ((AccessibleObject) member).setAccessible(false);
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

        if (disallowProxyMemberAccess && ProxyUtil.isProxyMember(member, target)) {
            LOG.warn("Access to proxy is blocked! Target class [{}] of target [{}], member [{}]", targetClass, target, member);
            return false;
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

        if (disallowDefaultPackageAccess) {
            if (targetClass.getPackage() == null || targetClass.getPackage().getName().isEmpty()) {
                LOG.warn("Class [{}] from the default package is excluded!", targetClass);
                return false;
            }
            if (memberClass.getPackage() == null || memberClass.getPackage().getName().isEmpty()) {
                LOG.warn("Class [{}] from the default package is excluded!", memberClass);
                return false;
            }
        }

        if (isPackageExcluded(targetClass)) {
            LOG.warn("Package [{}] of target class [{}] of target [{}] is excluded!",
                    targetClass.getPackage(),
                    targetClass,
                    target);
            return false;
        }

        if (isPackageExcluded(memberClass)) {
            LOG.warn("Package [{}] of member [{}] are excluded!", memberClass.getPackage(), member);
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

    protected boolean isPackageExcluded(Class<?> clazz) {
        return !excludedPackageExemptClasses.contains(clazz.getName()) && (isExcludedPackageNames(clazz) || isExcludedPackageNamePatterns(clazz));
    }

    public static String toPackageName(Class<?> clazz) {
        if (clazz.getPackage() == null) {
            return "";
        }
        return clazz.getPackage().getName();
    }

    protected boolean isExcludedPackageNamePatterns(Class<?> clazz) {
        return excludedPackageNamePatterns.stream().anyMatch(pattern -> pattern.matcher(toPackageName(clazz)).matches());
    }

    protected boolean isExcludedPackageNames(Class<?> clazz) {
        return isExcludedPackageNamesStatic(clazz, excludedPackageNames);
    }

    public static boolean isExcludedPackageNamesStatic(Class<?> clazz, Set<String> excludedPackageNames) {
        List<String> packageParts = Arrays.asList(toPackageName(clazz).split("\\."));
        for (int i = 0; i < packageParts.size(); i++) {
            String parentPackage = String.join(".", packageParts.subList(0, i + 1));
            if (excludedPackageNames.contains(parentPackage)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isClassExcluded(Class<?> clazz) {
        return excludedClasses.contains(clazz.getName());
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
        useExcludedClasses(excludedClasses.stream().map(Class::getName).collect(toSet()));
    }

    public void useExcludedClasses(Set<String> excludedClasses) {
        Set<String> newExcludedClasses = new HashSet<>(excludedClasses);
        newExcludedClasses.add(Object.class.getName());
        if (!allowStaticFieldAccess) {
            newExcludedClasses.add(Class.class.getName());
        }
        this.excludedClasses = unmodifiableSet(newExcludedClasses);
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
        useExcludedPackageExemptClasses(excludedPackageExemptClasses.stream().map(Class::getName).collect(toSet()));
    }

    public void useExcludedPackageExemptClasses(Set<String> excludedPackageExemptClasses) {
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

    public void disallowDefaultPackageAccess(boolean disallowDefaultPackageAccess) {
        this.disallowDefaultPackageAccess = disallowDefaultPackageAccess;
    }
}
