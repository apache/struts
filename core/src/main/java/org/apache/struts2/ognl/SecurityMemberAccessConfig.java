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

import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.inject.Initializable;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;
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
 * Holds the parsed OGNL security configuration for one container.
 * <p>
 * {@link SecurityMemberAccess} is a {@code Scope.PROTOTYPE} bean, constructed once per value stack and
 * again for each OGNL context. Parsing the roughly ninety configuration entries on every one of those
 * was the dominant cost identified by WW-5667. This bean is a {@code Scope.SINGLETON}, so the parsing
 * happens once per container and each {@code SecurityMemberAccess} merely copies immutable references.
 * <p>
 * Dev-mode is resolved in {@link #init()} rather than in a setter, because the container iterates
 * {@code getDeclaredMethods()}, whose order the JDK leaves unspecified. If {@code init()} never runs,
 * the normal production exclusions stay in force, which fails closed.
 *
 * @since Struts 7.4.0
 */
public class SecurityMemberAccessConfig implements Initializable {

    private static final Logger LOG = LogManager.getLogger(SecurityMemberAccessConfig.class);

    /**
     * Struts' own component packages, which must always be allowlisted regardless of what an
     * application configures via {@code struts.allowlist.packageNames}. Lives here, alongside
     * {@link #union(Set, Set)}, because this is the single place that computes
     * {@code allowlistPackageNamesUnion}; {@link SecurityMemberAccess} references both statically for
     * its default field value and its deprecated {@code useAllowlistPackageNames} setter, so the
     * computation is never duplicated.
     */
    static final Set<String> ALLOWLIST_REQUIRED_PACKAGES = Set.of(
            "org.apache.struts2.validator.validators",
            "org.apache.struts2.components",
            "org.apache.struts2.views.jsp"
    );

    private boolean allowStaticFieldAccess = true;

    private Set<String> excludedClasses = Set.of(Object.class.getName());
    private Set<Pattern> excludedPackageNamePatterns = emptySet();
    private Set<String> excludedPackageNames = emptySet();
    private Set<String> excludedPackageExemptClasses = emptySet();

    private boolean isDevMode;
    private Set<String> devModeExcludedClasses = Set.of(Object.class.getName());
    private Set<Pattern> devModeExcludedPackageNamePatterns = emptySet();
    private Set<String> devModeExcludedPackageNames = emptySet();
    private Set<String> devModeExcludedPackageExemptClasses = emptySet();

    private boolean enforceAllowlistEnabled = false;
    private Set<Class<?>> allowlistClasses = emptySet();
    private Set<String> allowlistPackageNames = emptySet();
    private Set<String> allowlistPackageNamesUnion = ALLOWLIST_REQUIRED_PACKAGES;

    private boolean disallowProxyObjectAccess = false;
    private boolean disallowProxyMemberAccess = false;
    private boolean disallowDefaultPackageAccess = false;

    @Override
    public void init() {
        if (!isDevMode) {
            return;
        }
        logWarningForFirstOccurrence("devMode", LOG,
                "DevMode enabled, using DevMode excluded classes and packages for OGNL security enforcement!");
        excludedClasses = devModeExcludedClasses;
        excludedPackageNamePatterns = devModeExcludedPackageNamePatterns;
        excludedPackageNames = devModeExcludedPackageNames;
        excludedPackageExemptClasses = devModeExcludedPackageExemptClasses;
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
        this.allowlistPackageNamesUnion = union(ALLOWLIST_REQUIRED_PACKAGES, allowlistPackageNames);
    }

    /**
     * The only place in the codebase that computes the allowlist package union. Both
     * {@link #useAllowlistPackageNames(String)} above and {@link SecurityMemberAccess}'s deprecated
     * setter path call this method, so {@code ALLOWLIST_REQUIRED_PACKAGES} can never silently drop out
     * of the union through a second, drifted implementation.
     * <p>
     * The early return aliases {@code required} directly into the result, which is safe only because
     * every caller passes an immutable {@code Set.of(...)} for that argument; a mutable set must not be
     * passed as {@code required}.
     */
    static Set<String> union(Set<String> required, Set<String> configured) {
        if (configured.isEmpty()) {
            return required;
        }
        Set<String> union = new HashSet<>(required);
        union.addAll(configured);
        return unmodifiableSet(union);
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
    public void useDevMode(String devMode) {
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

    public boolean isAllowStaticFieldAccess() {
        return allowStaticFieldAccess;
    }

    public Set<String> getExcludedClasses() {
        return excludedClasses;
    }

    public Set<Pattern> getExcludedPackageNamePatterns() {
        return excludedPackageNamePatterns;
    }

    public Set<String> getExcludedPackageNames() {
        return excludedPackageNames;
    }

    public Set<String> getExcludedPackageExemptClasses() {
        return excludedPackageExemptClasses;
    }

    public boolean isEnforceAllowlistEnabled() {
        return enforceAllowlistEnabled;
    }

    public Set<Class<?>> getAllowlistClasses() {
        return allowlistClasses;
    }

    public Set<String> getAllowlistPackageNames() {
        return allowlistPackageNames;
    }

    public Set<String> getAllowlistPackageNamesUnion() {
        return allowlistPackageNamesUnion;
    }

    public boolean isDisallowProxyObjectAccess() {
        return disallowProxyObjectAccess;
    }

    public boolean isDisallowProxyMemberAccess() {
        return disallowProxyMemberAccess;
    }

    public boolean isDisallowDefaultPackageAccess() {
        return disallowDefaultPackageAccess;
    }
}
