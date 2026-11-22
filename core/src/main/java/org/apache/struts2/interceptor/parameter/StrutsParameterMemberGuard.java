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
import org.apache.struts2.ognl.ParameterMemberGuard;

import java.beans.Introspector;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;

import static java.lang.String.format;
import static org.apache.struts2.util.DebugUtils.notifyDeveloperOfError;

/**
 * The guard {@link StrutsParameterAuthorizer#memberGuard} creates for one parameter.
 * <p>
 * Only the root segment of the parameter is judged: the first member OGNL resolves on an object standing in the value
 * stack root. Once that member is authorized for the parameter's depth, OGNL goes on along the path through it, and
 * the objects it reaches are covered by that depth even if they also stand in the root. On the object the root segment
 * was resolved on, a later member is still judged, unless it is the same member again or the setter through which the
 * null handler stores a newly created root. The {@link org.apache.struts2.ModelDriven} model is exempt by identity as
 * it is in {@link StrutsParameterAuthorizer#isAuthorized}.
 * <p>
 * The first refused member refuses the whole parameter: every later member is refused and {@link #hasRefused()} tells
 * {@link org.apache.struts2.ognl.accessor.CompoundRootAccessor} to stop walking the root.
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
    private Object rootOwner;
    private Member rootMember;
    private String rootProperty;
    private boolean refused;

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
        if (refused) {
            return false;
        }
        if (rootOwner != null) {
            return permitsPastRootSegment(owner, member, propertyName);
        }
        if (owner == null || owner != exemptModel && root.stream().noneMatch(candidate -> candidate == owner)) {
            return true;
        }
        if (owner != exemptModel && !isResolvedMemberAuthorized(owner, member, propertyName)) {
            return refuse(owner, member);
        }
        rootOwner = owner;
        rootMember = member;
        rootProperty = propertyName;
        return true;
    }

    @Override
    public boolean hasRefused() {
        return refused;
    }

    private boolean permitsPastRootSegment(Object owner, Member member, String propertyName) {
        if (owner != rootOwner
                || owner == exemptModel
                || member.equals(rootMember)
                || isCreatingMissingRoot(member, propertyName)
                || isResolvedMemberAuthorized(owner, member, propertyName)) {
            return true;
        }
        return refuse(owner, member);
    }

    private boolean refuse(Object owner, Member member) {
        refused = true;
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

    /**
     * A nested parameter whose root getter returned {@code null} makes the null handler create that object and store
     * it through the setter. That setter takes a new instance, never the request value, and the annotation of a nested
     * parameter belongs on the getter, so having passed the getter is what authorizes it. It has to be the bean setter
     * of the property the getter was resolved by, taking what the getter returns.
     */
    private boolean isCreatingMissingRoot(Member member, String propertyName) {
        return paramDepth > 0
                && rootMember instanceof Method getter
                && propertyName != null
                && propertyName.equals(rootProperty)
                && member instanceof Method setter
                && setter.getName().startsWith("set")
                && setter.getParameterCount() == 1
                && setter.getParameterTypes()[0].isAssignableFrom(getter.getReturnType());
    }

    /**
     * Whether {@code member}, which OGNL resolved on {@code owner}, may be used for a parameter of this depth. Three
     * things authorize it, matching {@link StrutsParameterAuthorizer#hasValidAnnotatedMember}: its own annotation, the
     * annotation of an overload or of the proxied class's method of that name, and the annotation of a public field of
     * that property, whose name is decapitalized as {@link StrutsParameterAuthorizer#isAuthorized} does.
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
        if (propertyName == null) {
            return false;
        }
        String fieldName = Introspector.decapitalize(propertyName);
        return Arrays.stream(ownerClass.getFields())
                .anyMatch(field -> field.getName().equals(fieldName)
                        && !Modifier.isStatic(field.getModifiers())
                        && authorizer.getPermittedInjectionDepth(field) >= paramDepth);
    }
}
