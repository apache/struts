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
package org.apache.struts2.ognl;

import ognl.MemberAccess;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.util.ProxyUtil;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static java.text.MessageFormat.format;
import static java.util.Collections.emptySet;
import static org.apache.struts2.StrutsConstants.STRUTS_ALLOWLIST_CLASSES;
import static org.apache.struts2.StrutsConstants.STRUTS_ALLOWLIST_PACKAGE_NAMES;
import static org.apache.struts2.util.ConfigParseUtil.toClassObjectsSet;
import static org.apache.struts2.util.ConfigParseUtil.toClassesSet;
import static org.apache.struts2.util.ConfigParseUtil.toNewClassesSet;
import static org.apache.struts2.util.ConfigParseUtil.toNewPackageNamesSet;
import static org.apache.struts2.util.ConfigParseUtil.toNewPatternsSet;
import static org.apache.struts2.util.ConfigParseUtil.toPackageNamesSet;
import static org.apache.struts2.util.DebugUtils.logWarningForFirstOccurrence;

/**
 * Allows access decisions to be made on the basis of whether a member is static or not.
 * Also blocks or allows access to properties.
 */
public class SecurityMemberAccess implements MemberAccess {

    private static final Logger LOG = LogManager.getLogger(SecurityMemberAccess.class);

    private static final Set<String> ALLOWLIST_REQUIRED_PACKAGES = Set.of(
            "org.apache.struts2.validator.validators",
            "org.apache.struts2.components",
            "org.apache.struts2.views.jsp"
    );

    private static final Set<Class<?>> ALLOWLIST_REQUIRED_CLASSES = Set.of(
            java.lang.Enum.class,
            java.lang.String.class,
            java.util.Date.class,
            java.util.HashMap.class,
            java.util.Map.class,
            java.util.Map.Entry.class
    );

    private final ProviderAllowlist providerAllowlist;
    private final ThreadAllowlist threadAllowlist;

    private boolean allowStaticFieldAccess = true;

    private Set<Pattern> excludeProperties = emptySet();
    private Set<Pattern> acceptProperties = emptySet();

    private Set<String> excludedClasses = Set.of(Object.class.getName());
    private Set<Pattern> excludedPackageNamePatterns = emptySet();
    private Set<String> excludedPackageNames = emptySet();
    private Set<String> excludedPackageExemptClasses = emptySet();

    private volatile boolean isDevModeInit;
    private boolean isDevMode;
    private Set<String> devModeExcludedClasses = Set.of(Object.class.getName());
    private Set<Pattern> devModeExcludedPackageNamePatterns = emptySet();
    private Set<String> devModeExcludedPackageNames = emptySet();
    private Set<String> devModeExcludedPackageExemptClasses = emptySet();

    private boolean enforceAllowlistEnabled = false;
    private Set<Class<?>> allowlistClasses = emptySet();
    private Set<String> allowlistPackageNames = emptySet();

    private boolean disallowProxyObjectAccess = false;
    private boolean disallowProxyMemberAccess = false;
    private boolean disallowDefaultPackageAccess = false;

    @Inject
    public SecurityMemberAccess(@Inject ProviderAllowlist providerAllowlist, @Inject ThreadAllowlist threadAllowlist) {
        this.providerAllowlist = providerAllowlist;
        this.threadAllowlist = threadAllowlist;
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

        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null!");
        }
        if (target != null) {
            // Special case: Target is a Class object but not Class.class
            if (Class.class.equals(target.getClass()) && !Class.class.equals(target)) {
                if (!isStatic(member) && !Constructor.class.equals(member.getClass())) {
                    throw new IllegalArgumentException("Member expected to be static or constructor!");
                }
                if (!member.getDeclaringClass().equals(target)) {
                    throw new IllegalArgumentException("Target class does not match member!");
                }
                target = null; // This information is not useful to us and conflicts with following logic which expects target to be null or an instance containing the member
            // Standard case: Member should exist on target
            } else if (!member.getDeclaringClass().isAssignableFrom(target.getClass())) {
                throw new IllegalArgumentException("Member does not exist on target!");
            }
        }

        if (!checkProxyObjectAccess(target)) {
            LOG.warn("Access to proxy is blocked! Target [{}], proxy class [{}]", target, target.getClass().getName());
            return false;
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
        if (!enforceAllowlistEnabled) {
            return true;
        }

        Class<?> targetClass = target != null ? target.getClass() : null;

        if (!disallowProxyObjectAccess && ProxyUtil.isProxy(target)) {
            // If `disallowProxyObjectAccess` is not set, allow resolving Hibernate entities and Spring proxies to their
            // underlying classes/members. This allows the allowlist capability to continue working and still offer
            // protection in applications where the developer has accepted the risk of allowing OGNL access to Hibernate
            // entities and Spring proxies. This is preferred to having to disable the allowlist capability entirely.
            Class<?> newTargetClass = ProxyUtil.ultimateTargetClass(target);
            if (newTargetClass != targetClass) {
                targetClass = newTargetClass;
                member = ProxyUtil.resolveTargetMember(member, newTargetClass);
            }
        }

        Class<?> memberClass = member.getDeclaringClass();
        if (!isClassAllowlisted(memberClass)) {
            LOG.warn("Declaring class [{}] of member type [{}] is not allowlisted! Add to '{}' or '{}' configuration.",
                    memberClass, member, STRUTS_ALLOWLIST_CLASSES, STRUTS_ALLOWLIST_PACKAGE_NAMES);
            return false;
        }

        if (targetClass == null || targetClass == memberClass) {
            return true;
        }
        if (!isClassAllowlisted(targetClass)) {
            LOG.warn("Target class [{}] of target [{}] is not allowlisted! Add to '{}' or '{}' configuration.",
                    targetClass, target, STRUTS_ALLOWLIST_CLASSES, STRUTS_ALLOWLIST_PACKAGE_NAMES);
            return false;
        }
        return true;
    }

    protected boolean isClassAllowlisted(Class<?> clazz) {
        return allowlistClasses.contains(clazz)
                || ALLOWLIST_REQUIRED_CLASSES.contains(clazz)
                || (providerAllowlist != null && providerAllowlist.getProviderAllowlist().contains(clazz))
                || (threadAllowlist != null && threadAllowlist.getAllowlist().contains(clazz))
                || isClassBelongsToPackages(clazz, ALLOWLIST_REQUIRED_PACKAGES)
                || isClassBelongsToPackages(clazz, allowlistPackageNames);
    }

    /**
     * @return {@code true} if member access is allowed
     */
    protected boolean checkExclusionList(Object target, Member member) {
        useDevModeConfiguration();
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
     * @return {@code true} if proxy object access is allowed
     */
    protected boolean checkProxyObjectAccess(Object target) {
        return !(disallowProxyObjectAccess && ProxyUtil.isProxy(target));
    }

    /**
     * @return {@code true} if proxy member access is allowed
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
        List<String> packageParts = List.of(toPackageName(clazz).split("\\."));
        return IntStream.range(0, packageParts.size())
                .mapToObj(i -> String.join(".", packageParts.subList(0, i + 1)))
                .anyMatch(matchingPackages::contains);
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

    public void useExcludeProperties(Set<Pattern> excludeProperties) {
        this.excludeProperties = excludeProperties;
    }

    public void useAcceptProperties(Set<Pattern> acceptedProperties) {
        this.acceptProperties = acceptedProperties;
    }

    @Inject(value = StrutsConstants.STRUTS_ALLOW_STATIC_FIELD_ACCESS, required = false)
    public void useAllowStaticFieldAccess(String allowStaticFieldAccess) {
        this.allowStaticFieldAccess = BooleanUtils.toBoolean(allowStaticFieldAccess);
        if (!this.allowStaticFieldAccess) {
            useExcludedClasses(Class.class.getName());
        }
    }

    @Inject(value = StrutsConstants.STRUTS_EXCLUDED_CLASSES, required = false)
    public void useExcludedClasses(String commaDelimitedClasses) {
       this.excludedClasses = toNewClassesSet(excludedClasses, commaDelimitedClasses);
    }

    @Inject(value = StrutsConstants.STRUTS_EXCLUDED_PACKAGE_NAME_PATTERNS, required = false)
    public void useExcludedPackageNamePatterns(String commaDelimitedPackagePatterns) {
        this.excludedPackageNamePatterns = toNewPatternsSet(excludedPackageNamePatterns, commaDelimitedPackagePatterns);
    }

    @Inject(value = StrutsConstants.STRUTS_EXCLUDED_PACKAGE_NAMES, required = false)
    public void useExcludedPackageNames(String commaDelimitedPackageNames) {
        this.excludedPackageNames = toNewPackageNamesSet(excludedPackageNames, commaDelimitedPackageNames);
    }

    @Inject(value = StrutsConstants.STRUTS_EXCLUDED_PACKAGE_EXEMPT_CLASSES, required = false)
    public void useExcludedPackageExemptClasses(String commaDelimitedClasses) {
        this.excludedPackageExemptClasses = toClassesSet(commaDelimitedClasses);
    }

    @Inject(value = StrutsConstants.STRUTS_ALLOWLIST_ENABLE, required = false)
    public void useEnforceAllowlistEnabled(String enforceAllowlistEnabled) {
        this.enforceAllowlistEnabled = BooleanUtils.toBoolean(enforceAllowlistEnabled);
        if (!this.enforceAllowlistEnabled) {
            String msg = "OGNL allowlist is disabled!" +
                    " We strongly recommend keeping it enabled to protect against critical vulnerabilities." +
                    " Set the configuration `{}=true` to enable it." +
                    " Please refer to the Struts 7.0 migration guide and security documentation for further information.";
            logWarningForFirstOccurrence("allowlist", LOG, msg, StrutsConstants.STRUTS_ALLOWLIST_ENABLE);
        }
    }

    @Inject(value = STRUTS_ALLOWLIST_CLASSES, required = false)
    public void useAllowlistClasses(String commaDelimitedClasses) {
        this.allowlistClasses = toClassObjectsSet(commaDelimitedClasses);
    }

    @Inject(value = STRUTS_ALLOWLIST_PACKAGE_NAMES, required = false)
    public void useAllowlistPackageNames(String commaDelimitedPackageNames) {
        this.allowlistPackageNames = toPackageNamesSet(commaDelimitedPackageNames);
    }

    @Inject(value = StrutsConstants.STRUTS_DISALLOW_PROXY_OBJECT_ACCESS, required = false)
    public void useDisallowProxyObjectAccess(String disallowProxyObjectAccess) {
        this.disallowProxyObjectAccess = BooleanUtils.toBoolean(disallowProxyObjectAccess);
    }

    @Inject(value = StrutsConstants.STRUTS_DISALLOW_PROXY_MEMBER_ACCESS, required = false)
    public void useDisallowProxyMemberAccess(String disallowProxyMemberAccess) {
        this.disallowProxyMemberAccess = BooleanUtils.toBoolean(disallowProxyMemberAccess);
    }

    @Inject(value = StrutsConstants.STRUTS_DISALLOW_DEFAULT_PACKAGE_ACCESS, required = false)
    public void useDisallowDefaultPackageAccess(String disallowDefaultPackageAccess) {
        this.disallowDefaultPackageAccess = BooleanUtils.toBoolean(disallowDefaultPackageAccess);
    }

    @Inject(StrutsConstants.STRUTS_DEVMODE)
    protected void useDevMode(String devMode) {
        this.isDevMode = BooleanUtils.toBoolean(devMode);
    }

    @Inject(value = StrutsConstants.STRUTS_DEV_MODE_EXCLUDED_CLASSES, required = false)
    public void useDevModeExcludedClasses(String commaDelimitedClasses) {
        this.devModeExcludedClasses = toNewClassesSet(devModeExcludedClasses, commaDelimitedClasses);
    }

    @Inject(value = StrutsConstants.STRUTS_DEV_MODE_EXCLUDED_PACKAGE_NAME_PATTERNS, required = false)
    public void useDevModeExcludedPackageNamePatterns(String commaDelimitedPackagePatterns) {
        this.devModeExcludedPackageNamePatterns = toNewPatternsSet(devModeExcludedPackageNamePatterns, commaDelimitedPackagePatterns);
    }

    @Inject(value = StrutsConstants.STRUTS_DEV_MODE_EXCLUDED_PACKAGE_NAMES, required = false)
    public void useDevModeExcludedPackageNames(String commaDelimitedPackageNames) {
        this.devModeExcludedPackageNames = toNewPackageNamesSet(devModeExcludedPackageNames, commaDelimitedPackageNames);
    }

    @Inject(value = StrutsConstants.STRUTS_DEV_MODE_EXCLUDED_PACKAGE_EXEMPT_CLASSES, required = false)
    public void useDevModeExcludedPackageExemptClasses(String commaDelimitedClasses) {
        this.devModeExcludedPackageExemptClasses = toClassesSet(commaDelimitedClasses);
    }

    private void useDevModeConfiguration() {
        if (!isDevMode || isDevModeInit) {
            return;
        }
        logWarningForFirstOccurrence("devMode", LOG,
                "DevMode enabled, using DevMode excluded classes and packages for OGNL security enforcement!");
        isDevModeInit = true;
        excludedClasses = devModeExcludedClasses;
        excludedPackageNamePatterns = devModeExcludedPackageNamePatterns;
        excludedPackageNames = devModeExcludedPackageNames;
        excludedPackageExemptClasses = devModeExcludedPackageExemptClasses;
    }
}
