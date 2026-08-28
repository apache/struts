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

import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ActionContext;
import org.apache.struts2.ModelDriven;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.ognl.OgnlUtil;
import org.apache.struts2.util.ProxyService;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Optional;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.indexOfAny;
import static org.apache.struts2.security.DefaultAcceptedPatternsChecker.NESTING_CHARS;
import static org.apache.struts2.security.DefaultAcceptedPatternsChecker.NESTING_CHARS_STR;
import static org.apache.struts2.util.DebugUtils.notifyDeveloperOfError;

/**
 * Default implementation of {@link ParameterAuthorizer} that checks {@link StrutsParameter} annotations on the target
 * object's members to determine whether a parameter is authorized for injection.
 *
 * <p>This implementation extracts the authorization logic from {@link ParametersInterceptor} so that it can be shared
 * with other input channels (JSON plugin, REST plugin) without duplicating code.</p>
 *
 * <p>Unlike {@link ParametersInterceptor}, this implementation does NOT perform OGNL ThreadAllowlist side effects.
 * Those remain specific to the OGNL-based parameter injection path.</p>
 *
 * @since 7.2.0
 */
public class StrutsParameterAuthorizer implements ParameterAuthorizer {

    private static final Logger LOG = LogManager.getLogger(StrutsParameterAuthorizer.class);

    /**
     * {@link OgnlUtil#getBeanInfo(Class)} introspects with {@link Object} as the stop class, so this one never
     * appears among the property descriptors and cannot be told apart from a genuinely unknown name by evidence
     * alone. It is not unknown, though: it resolves to {@link Object#getClass()} on every object alike.
     */
    private static final String CLASS_PROPERTY = "class";

    private boolean requireAnnotations = false;
    private boolean requireAnnotationsTransitionMode = false;
    private boolean devMode = false;

    private OgnlUtil ognlUtil;
    private ProxyService proxyService;

    @Inject
    public void setOgnlUtil(OgnlUtil ognlUtil) {
        this.ognlUtil = ognlUtil;
    }

    @Inject
    public void setProxyService(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    @Inject(StrutsConstants.STRUTS_DEVMODE)
    public void setDevMode(String mode) {
        this.devMode = BooleanUtils.toBoolean(mode);
    }

    @Inject(value = StrutsConstants.STRUTS_PARAMETERS_REQUIRE_ANNOTATIONS, required = false)
    public void setRequireAnnotations(String requireAnnotations) {
        this.requireAnnotations = BooleanUtils.toBoolean(requireAnnotations);
    }

    @Inject(value = StrutsConstants.STRUTS_PARAMETERS_REQUIRE_ANNOTATIONS_TRANSITION, required = false)
    public void setRequireAnnotationsTransitionMode(String transitionMode) {
        this.requireAnnotationsTransitionMode = BooleanUtils.toBoolean(transitionMode);
    }

    @Override
    public Object resolveTarget(Object action) {
        if (action instanceof ModelDriven<?>) {
            Object stackTop = ActionContext.getContext().getValueStack().peek();
            if (!stackTop.equals(action)) {
                return stackTop;
            }
        }
        return action;
    }

    @Override
    public boolean isAuthorized(String parameterName, Object target, Object action) {
        if (parameterName == null || parameterName.isEmpty()) {
            return false;
        }

        if (!requireAnnotations) {
            return true;
        }

        long paramDepth = parameterName.codePoints().mapToObj(c -> (char) c).filter(NESTING_CHARS::contains).count();

        int nestingIndex = indexOfAny(parameterName, NESTING_CHARS_STR);
        String rootProperty = nestingIndex == -1 ? parameterName : parameterName.substring(0, nestingIndex);
        if (rootProperty.isEmpty()) {
            LOG.debug("Parameter [{}] begins with a nesting character, so it names no root property to authorize; rejecting",
                    parameterName);
            return false;
        }
        String normalisedRootProperty = Character.toLowerCase(rootProperty.charAt(0)) + rootProperty.substring(1);

        // Transition mode: depth-0 (non-nested) parameters are exempt. Checked before the ModelDriven
        // exemption so that it also covers a ModelDriven action's own members, which would otherwise
        // have no migration path once the exemption is scoped to the model.
        if (requireAnnotationsTransitionMode && paramDepth == 0) {
            LOG.debug("Annotation transition mode enabled, exempting non-nested parameter [{}] from @StrutsParameter annotation requirement",
                    parameterName);
            return true;
        }

        // ModelDriven exemption: only exempt when the action explicitly implements ModelDriven
        // and the target is its model object. This prevents non-ModelDriven root objects
        // (e.g. JSONInterceptor's configurable rootObject) from bypassing annotation checks.
        if (target != action && action instanceof ModelDriven) {
            return isAuthorizedOnModelDrivenAction(normalisedRootProperty, target, action, paramDepth);
        }

        return hasValidAnnotatedMember(normalisedRootProperty, target, paramDepth);
    }

    /**
     * Decides authorization for a {@link ModelDriven} action, whose model is on top of the value stack.
     * <p>
     * Returning an object from {@code getModel()} declares that object to be request surface, so anything the
     * model itself can take is exempt from the {@link StrutsParameter} requirement. The exemption stops there:
     * OGNL resolves the parameter name against the whole stack, which also holds the action, so a property
     * declared on the action is still subject to the annotation requirement. Without that distinction a
     * ModelDriven action would silently expose its own members.
     * <p>
     * A property declared on neither is allowed, since it cannot be reaching a member of the action - typically
     * it is bound by a custom OGNL property accessor on the model, such as a Map-backed model. {@code class} is
     * the exception to that: it is invisible to introspection here rather than absent, so it is rejected instead
     * of taking the fallback, which keeps a ModelDriven action from handing OGNL a {@code class} path that the
     * ordinary non-ModelDriven path would have rejected for want of an annotation.
     */
    protected boolean isAuthorizedOnModelDrivenAction(String rootProperty, Object model, Object action, long paramDepth) {
        if (declaresProperty(model, rootProperty, paramDepth)) {
            LOG.debug("Property [{}] belongs to the ModelDriven model, exempting from @StrutsParameter annotation requirement",
                    rootProperty);
            return true;
        }
        if (!declaresProperty(action, rootProperty, paramDepth)) {
            if (CLASS_PROPERTY.equals(rootProperty)) {
                LOG.debug("Property [class] is not an unknown property but Object.getClass() on every object alike, so the fallback for a custom accessor does not apply; rejecting");
                return false;
            }
            LOG.debug("Property [{}] is declared on neither the model nor the action, exempting from @StrutsParameter annotation requirement",
                    rootProperty);
            return true;
        }
        LOG.debug("Property [{}] is declared on the ModelDriven action itself, applying the @StrutsParameter annotation requirement",
                rootProperty);
        return hasValidAnnotatedMember(rootProperty, action, paramDepth);
    }

    /**
     * Whether {@code target} can itself take {@code property} at this depth - as a bean property whose relevant
     * accessor exists, the setter for a depth-0 parameter and the getter for a nested one, or as a public instance
     * field. Any {@link StrutsParameter} annotation is irrelevant here; this asks only what the object can absorb.
     * <p>
     * It has to be bindability rather than the name alone, because OGNL walks the stack until an object actually
     * accepts the assignment. A model which merely names the property without being able to take it - a getter-only
     * property under a depth-0 parameter, say - does not absorb that parameter: OGNL moves on to the action, and an
     * exemption granted on the name alone would hand over the action's own member, which is the very thing this
     * scoping exists to prevent. Inherited public fields count for the same reason, that OGNL can set them.
     */
    protected boolean declaresProperty(Object target, String property, long paramDepth) {
        BeanInfo beanInfo = getBeanInfo(target);
        if (beanInfo != null && Arrays.stream(beanInfo.getPropertyDescriptors())
                .filter(desc -> desc.getName().equals(property))
                .anyMatch(desc -> (paramDepth == 0 ? desc.getWriteMethod() : desc.getReadMethod()) != null)) {
            return true;
        }
        return declaresBindablePublicField(target, property, paramDepth);
    }

    /**
     * Whether {@code target} exposes {@code property} as a public instance field that this parameter could bind
     * through. {@link Class#getField} rather than {@code getDeclaredField}, since an inherited public field is just
     * as settable as a declared one. Static fields are not per-instance request surface, and a final field cannot
     * take a depth-0 assignment, so neither counts as absorbing the parameter.
     */
    protected boolean declaresBindablePublicField(Object target, String property, long paramDepth) {
        Field field;
        try {
            field = ultimateClass(target).getField(property);
        } catch (NoSuchFieldException e) {
            return false;
        }
        if (Modifier.isStatic(field.getModifiers())) {
            return false;
        }
        return paramDepth > 0 || !Modifier.isFinal(field.getModifiers());
    }

    protected boolean hasValidAnnotatedMember(String rootProperty, Object target, long paramDepth) {
        LOG.debug("Checking target [{}] for a matching, correctly annotated member for property [{}]",
                target.getClass().getSimpleName(), rootProperty);
        BeanInfo beanInfo = getBeanInfo(target);
        if (beanInfo == null) {
            return hasValidAnnotatedField(target, rootProperty, paramDepth);
        }

        Optional<PropertyDescriptor> propDescOpt = Arrays.stream(beanInfo.getPropertyDescriptors())
                .filter(desc -> desc.getName().equals(rootProperty)).findFirst();
        if (propDescOpt.isEmpty()) {
            return hasValidAnnotatedField(target, rootProperty, paramDepth);
        }

        if (hasValidAnnotatedPropertyDescriptor(target, propDescOpt.get(), paramDepth)) {
            return true;
        }

        return hasValidAnnotatedField(target, rootProperty, paramDepth);
    }

    protected boolean hasValidAnnotatedPropertyDescriptor(Object target, PropertyDescriptor propDesc, long paramDepth) {
        Class<?> targetClass = ultimateClass(target);
        Method relevantMethod = paramDepth == 0 ? propDesc.getWriteMethod() : propDesc.getReadMethod();
        if (relevantMethod == null) {
            return false;
        }
        if (getPermittedInjectionDepth(relevantMethod) < paramDepth) {
            String logMessage = format(
                    "Parameter injection for method [%s] on target [%s] rejected. Ensure it is annotated with @StrutsParameter with an appropriate 'depth'.",
                    relevantMethod.getName(),
                    relevantMethod.getDeclaringClass().getName());
            if (devMode) {
                notifyDeveloperOfError(LOG, target, logMessage);
            } else {
                LOG.debug(logMessage);
            }
            return false;
        }
        LOG.debug("Success: Matching annotated method [{}] found for property [{}] of depth [{}] on target [{}]",
                relevantMethod.getName(), propDesc.getName(), paramDepth, targetClass.getSimpleName());
        return true;
    }

    protected boolean hasValidAnnotatedField(Object target, String fieldName, long paramDepth) {
        Class<?> targetClass = ultimateClass(target);
        LOG.debug("No matching annotated method found for property [{}] of depth [{}] on target [{}], now also checking for public field",
                fieldName, paramDepth, targetClass.getSimpleName());
        Field field;
        try {
            field = targetClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            LOG.debug("Matching field for property [{}] not found on target [{}]", fieldName, targetClass.getSimpleName());
            return false;
        }
        if (!Modifier.isPublic(field.getModifiers())) {
            LOG.debug("Matching field [{}] is not public on target [{}]", field.getName(), targetClass.getSimpleName());
            return false;
        }
        if (getPermittedInjectionDepth(field) < paramDepth) {
            String logMessage = format(
                    "Parameter injection for field [%s] on target [%s] rejected. Ensure it is annotated with @StrutsParameter with an appropriate 'depth'.",
                    field.getName(),
                    targetClass.getName());
            if (devMode) {
                notifyDeveloperOfError(LOG, target, logMessage);
            } else {
                LOG.debug(logMessage);
            }
            return false;
        }
        LOG.debug("Success: Matching annotated public field [{}] found for property of depth [{}] on target [{}]",
                field.getName(), paramDepth, targetClass.getSimpleName());
        return true;
    }

    protected int getPermittedInjectionDepth(AnnotatedElement element) {
        StrutsParameter annotation = getParameterAnnotation(element);
        if (annotation == null) {
            return -1;
        }
        return annotation.depth();
    }

    protected StrutsParameter getParameterAnnotation(AnnotatedElement element) {
        return element.getAnnotation(StrutsParameter.class);
    }

    protected Class<?> ultimateClass(Object target) {
        if (proxyService.isProxy(target)) {
            return proxyService.ultimateTargetClass(target);
        }
        return target.getClass();
    }

    protected BeanInfo getBeanInfo(Object target) {
        Class<?> targetClass = ultimateClass(target);
        try {
            return ognlUtil.getBeanInfo(targetClass);
        } catch (IntrospectionException e) {
            LOG.warn("Error introspecting target {} for parameter authorization", targetClass, e);
            return null;
        }
    }
}
