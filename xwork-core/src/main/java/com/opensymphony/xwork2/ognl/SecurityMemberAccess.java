/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2.ognl;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import ognl.DefaultMemberAccess;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
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
public class SecurityMemberAccess extends DefaultMemberAccess {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityMemberAccess.class);

    private final boolean allowStaticMethodAccess;
    private Set<Pattern> excludeProperties = Collections.emptySet();
    private Set<Pattern> acceptProperties = Collections.emptySet();
    private Set<Class<?>> excludedClasses = Collections.emptySet();
    private Set<Pattern> excludedPackageNamePatterns = Collections.emptySet();

    public SecurityMemberAccess(boolean method) {
        super(false);
        allowStaticMethodAccess = method;
    }

    public boolean getAllowStaticMethodAccess() {
        return allowStaticMethodAccess;
    }

    @Override
    public boolean isAccessible(Map context, Object target, Member member, String propertyName) {
        if (checkEnumAccess(target, member)) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Allowing access to enum #0", target);
            }
            return true;
        }

        if (isPackageExcluded(target.getClass().getPackage(), member.getDeclaringClass().getPackage())) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Package of target [#0] or package of member [#1] are excluded!", target, member);
            }
            return false;
        }

        if (isClassExcluded(target.getClass(), member.getDeclaringClass())) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Target class [#0] or declaring class of member type [#1] are excluded!", target, member);
            }
            return false;
        }

        boolean allow = true;
        if (!checkStaticMethodAccess(member)) {
            if (LOG.isTraceEnabled()) {
                LOG.warn("Access to static [#0] is blocked!", member);
            }
            allow = false;
        }

        //failed static test
        if (!allow)
            return false;

        // Now check for standard scope rules
        return super.isAccessible(context, target, member, propertyName)
                && isAcceptableProperty(propertyName);
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
            if (Enum.class.isAssignableFrom(clazz) && member.getName().equals("values"))
                return true;
        }
        return false;
    }

    protected boolean isPackageExcluded(Package targetPackage, Package memberPackage) {
        for (Pattern pattern : excludedPackageNamePatterns) {
            if (pattern.matcher(targetPackage.getName()).matches() || pattern.matcher(memberPackage.getName()).matches()) {
                return true;
            }
        }
        return false;
    }

    protected boolean isClassExcluded(Class<?> targetClass, Class<?> declaringClass) {
        if (targetClass == Object.class || declaringClass == Object.class) {
            return true;
        }
        for (Class<?> excludedClass : excludedClasses) {
            if (targetClass.isAssignableFrom(excludedClass) || declaringClass.isAssignableFrom(excludedClass)) {
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
}
