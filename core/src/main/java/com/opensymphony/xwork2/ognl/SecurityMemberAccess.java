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
import java.lang.reflect.Method;
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
    private boolean enforceAllowlistEnabled = false;
    private Set<String> allowlistClasses = emptySet();
    private Set<String> allowlistPackageNames = emptySet();
    private boolean disallowProxyMemberAccess = false;
    private boolean disallowDefaultPackageAccess = false;

    @Inject
    public SecurityMemberAccess(@Inject(value = StrutsConstants.STRUTS_ALLOW_STATIC_FIELD_ACCESS) String allowStaticFieldAccess) {
        this(BooleanUtils.toBoolean(allowStaticFieldAccess));
    }

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

        if (target != null) {
            // Special case: Target is a Class object but not Class.class
            if (Class.class.equals(target.getClass()) && !Class.class.equals(target)) {
                if (!isStatic(member)) {
                    throw new IllegalArgumentException("Member expected to be static!");
                }
                if (!member.getDeclaringClass().equals(target)) {
                    throw new IllegalArgumentException("Target class does not match static member!");
                }
                target = null; // This information is not useful to us and conflicts with following logic which expects target to be null or an instance containing the member
            // Standard case: Member should exist on target
            } else if (!member.getDeclaringClass().isAssignableFrom(target.getClass())) {
                throw new IllegalArgumentException("Member does not exist on target!");
            }
        }

        if (!checkProxyMemberAccess(target, member)) {
            LOG.warn("Access to proxy is blocked! Member class [{}] of target [{}], member [{}]", member.getDeclaringClass(), target, member);
            return false;
        }

        if (!checkPublicMemberAccess(member)) {
            LOG.warn("Access to non-public [{}] is blocked!", member);
            return false;
        }

        if (!checkStaticFieldAccess(member)) {
            LOG.warn("Access to static field [{}] is blocked!", member);
            return false;
        }

        if (!checkStaticMethodAccess(member)) {
            LOG.warn("Access to static method [{}] is blocked!", member);
            return false;
        }

        if (!checkDefaultPackageAccess(target, member)) {
            return false;
        }

        if (!checkExclusionList(target, member)) {
            return false;
        }

        if (!checkAllowlist(target, member)) {
            return false;
        }

        if (!isAcceptableProperty(propertyName)) {
            return false;
        }

        return true;
    }

    /**
     * @return {@code true} if member access is allowed
     */
    protected boolean checkAllowlist(Object target, Member member) {
        Class<?> memberClass = member.getDeclaringClass();
        if (!enforceAllowlistEnabled) {
            return true;
        }
        if (!isClassAllowlisted(memberClass)) {
            LOG.warn(format("Declaring class [{0}] of member type [{1}] is not allowlisted!", memberClass, member));
            return false;
        }
        if (target == null || target.getClass() == memberClass) {
            return true;
        }
        Class<?> targetClass = target.getClass();
        if (!isClassAllowlisted(targetClass)) {
            LOG.warn(format("Target class [{0}] of target [{1}] is not allowlisted!", targetClass, target));
            return false;
        }
        return true;
    }

    protected boolean isClassAllowlisted(Class<?> clazz) {
        return allowlistClasses.contains(clazz.getName()) || isClassBelongsToPackages(clazz, allowlistPackageNames);
    }

    /**
     * @return {@code true} if member access is allowed
     */
    protected boolean checkExclusionList(Object target, Member member) {
        Class<?> memberClass = member.getDeclaringClass();
        if (isClassExcluded(memberClass)) {
            LOG.warn("Declaring class of member type [{}] is excluded!", memberClass);
            return false;
        }
        if (isPackageExcluded(memberClass)) {
            LOG.warn("Package [{}] of member class [{}] of member [{}] is excluded!",
                    memberClass.getPackage(),
                    memberClass,
                    target);
            return false;
        }
        if (target == null || target.getClass() == memberClass) {
            return true;
        }
        Class<?> targetClass = target.getClass();
        if (isClassExcluded(targetClass)) {
            LOG.warn("Target class [{}] of target [{}] is excluded!", targetClass, target);
            return false;
        }
        if (isPackageExcluded(targetClass)) {
            LOG.warn("Package [{}] of target [{}] is excluded!", targetClass.getPackage(), member);
            return false;
        }
        return true;
    }

    /**
     * @return {@code true} if member access is allowed
     */
    protected boolean checkDefaultPackageAccess(Object target, Member member) {
        if (!disallowDefaultPackageAccess) {
            return true;
        }
        Class<?> memberClass = member.getDeclaringClass();
        if (memberClass.getPackage() == null || memberClass.getPackage().getName().isEmpty()) {
            LOG.warn("Class [{}] from the default package is excluded!", memberClass);
            return false;
        }
        if (target == null || target.getClass() == memberClass) {
            return true;
        }
        Class<?> targetClass = target.getClass();
        if (targetClass.getPackage() == null || targetClass.getPackage().getName().isEmpty()) {
            LOG.warn("Class [{}] from the default package is excluded!", targetClass);
            return false;
        }
        return true;
    }

    /**
     * @return {@code true} if member access is allowed
     */
    protected boolean checkProxyMemberAccess(Object target, Member member) {
        return !(disallowProxyMemberAccess && ProxyUtil.isProxyMember(member, target));
    }

    /**
     * Check access for static method (via modifiers).
     * <p>
     * Note: For non-static members, the result is always true.
     *
     * @return {@code true} if member access is allowed
     */
    protected boolean checkStaticMethodAccess(Member member) {
        if (checkEnumAccess(member)) {
            LOG.trace("Exempting Enum#values from static method check: class [{}]", member.getDeclaringClass());
            return true;
        }
        return member instanceof Field || !isStatic(member);
    }

    private static boolean isStatic(Member member) {
        return Modifier.isStatic(member.getModifiers());
    }

    /**
     * Check access for static field (via modifiers).
     * <p>
     * Note: For non-static members, the result is always true.
     *
     * @return {@code true} if member access is allowed
     */
    protected boolean checkStaticFieldAccess(Member member) {
        if (allowStaticFieldAccess) {
            return true;
        }
        return !(member instanceof Field) || !isStatic(member);
    }

    /**
     * Check access for public members (via modifiers)
     *
     * @return {@code true} if member access is allowed
     */
    protected boolean checkPublicMemberAccess(Member member) {
        return Modifier.isPublic(member.getModifiers());
    }

    /**
     * @return {@code true} if member access is allowed
     */
    protected boolean checkEnumAccess(Member member) {
        return member.getDeclaringClass().isEnum()
                && isStatic(member)
                && member instanceof Method
                && member.getName().equals("values")
                && ((Method) member).getParameterCount() == 0;
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
        return isClassBelongsToPackages(clazz, excludedPackageNames);
    }

    public static boolean isClassBelongsToPackages(Class<?> clazz, Set<String> matchingPackages) {
        List<String> packageParts = Arrays.asList(toPackageName(clazz).split("\\."));
        for (int i = 0; i < packageParts.size(); i++) {
            String parentPackage = String.join(".", packageParts.subList(0, i + 1));
            if (matchingPackages.contains(parentPackage)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isClassExcluded(Class<?> clazz) {
        return excludedClasses.contains(clazz.getName());
    }

    /**
     * @return {@code true} if member access is allowed
     */
    protected boolean isAcceptableProperty(String name) {
        return name == null || !isExcluded(name) && isAccepted(name);
    }

    protected boolean isAccepted(String paramName) {
        if (acceptProperties.isEmpty()) {
            return true;
        }
        return acceptProperties.stream().map(pattern -> pattern.matcher(paramName)).anyMatch(Matcher::matches);
    }

    protected boolean isExcluded(String paramName) {
        return excludeProperties.stream().map(pattern -> pattern.matcher(paramName)).anyMatch(Matcher::matches);
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

    public void useEnforceAllowlistEnabled(boolean enforceAllowlistEnabled) {
        this.enforceAllowlistEnabled = enforceAllowlistEnabled;
    }

    public void useAllowlistClasses(Set<String> allowlistClasses) {
        this.allowlistClasses = allowlistClasses;
    }

    public void useAllowlistPackageNames(Set<String> allowlistPackageNames) {
        this.allowlistPackageNames = allowlistPackageNames;
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
