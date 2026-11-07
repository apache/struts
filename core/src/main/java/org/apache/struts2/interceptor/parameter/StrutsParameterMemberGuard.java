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
package org.apache.struts2.interceptor.parameter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;

import static java.lang.String.format;
import static org.apache.struts2.util.DebugUtils.notifyDeveloperOfError;

/**
 * The guard {@link StrutsParameterAuthorizer#memberGuard} creates for one parameter. Only objects standing in the
 * value stack root are judged, since a deeper object is reached through a root member whose annotation depth already
 * covers it, and the {@link org.apache.struts2.ModelDriven} model is exempt by identity as it is in
 * {@link StrutsParameterAuthorizer#isAuthorized}.
 *
 * @since 7.6.0
 */
final class StrutsParameterMemberGuard implements ParameterMemberGuard {

    private static final Logger LOG = LogManager.getLogger(StrutsParameterMemberGuard.class);

    private final StrutsParameterAuthorizer authorizer;
    private final long paramDepth;
    private final Object exemptModel;
    private final Collection<?> root;
    private final boolean devMode;
    private Object authorizedOwner;
    private String authorizedProperty;

    StrutsParameterMemberGuard(StrutsParameterAuthorizer authorizer, long paramDepth, Object exemptModel,
                               Collection<?> root, boolean devMode) {
        this.authorizer = authorizer;
        this.paramDepth = paramDepth;
        this.exemptModel = exemptModel;
        this.root = root;
        this.devMode = devMode;
    }

    @Override
    public boolean permits(Object owner, Member member, String propertyName) {
        if (owner == null || owner == exemptModel || root.stream().noneMatch(candidate -> candidate == owner)) {
            return true;
        }
        if (isCreatingMissingRoot(owner, member, propertyName)) {
            return true;
        }
        if (!isResolvedMemberAuthorized(owner, member, propertyName)) {
            String logMessage = format(
                    "Parameter injection through member [%s] on target [%s] rejected. Ensure it is annotated with @StrutsParameter with an appropriate 'depth'.",
                    member.getName(),
                    member.getDeclaringClass().getName());
            if (devMode) {
                notifyDeveloperOfError(LOG, owner, logMessage);
            } else {
                LOG.debug(logMessage);
            }
            return false;
        }
        authorizedOwner = owner;
        authorizedProperty = propertyName;
        return true;
    }

    /**
     * A nested parameter whose root getter returned {@code null} makes the null handler create that object and store
     * it through the setter. That setter takes a new instance, never the request value, and the annotation of a nested
     * parameter belongs on the getter, so having passed the getter is what authorizes it.
     */
    private boolean isCreatingMissingRoot(Object owner, Member member, String propertyName) {
        return paramDepth > 0
                && owner == authorizedOwner
                && propertyName != null
                && propertyName.equals(authorizedProperty)
                && member instanceof Method method
                && method.getParameterCount() == 1;
    }

    /**
     * Whether {@code member}, which OGNL resolved on {@code owner}, may be used for a parameter of this depth. Three
     * things authorize it, matching {@link StrutsParameterAuthorizer#hasValidAnnotatedMember}: its own annotation, the
     * annotation of an overload or of the proxied class's method of that name, and the annotation of a public field of
     * that property.
     */
    private boolean isResolvedMemberAuthorized(Object owner, Member member, String propertyName) {
        if (member instanceof AnnotatedElement element && authorizer.getPermittedInjectionDepth(element) >= paramDepth) {
            return true;
        }
        Class<?> ownerClass = authorizer.ultimateClass(owner);
        if (member instanceof Method method && Arrays.stream(ownerClass.getMethods())
                .anyMatch(candidate -> candidate.getName().equals(method.getName())
                        && candidate.getParameterCount() == method.getParameterCount()
                        && !Modifier.isStatic(candidate.getModifiers())
                        && authorizer.getPermittedInjectionDepth(candidate) >= paramDepth)) {
            return true;
        }
        return propertyName != null && Arrays.stream(ownerClass.getFields())
                .anyMatch(field -> field.getName().equals(propertyName)
                        && !Modifier.isStatic(field.getModifiers())
                        && authorizer.getPermittedInjectionDepth(field) >= paramDepth);
    }
}
