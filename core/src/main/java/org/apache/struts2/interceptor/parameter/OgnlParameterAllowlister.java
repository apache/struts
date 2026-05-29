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
import org.apache.struts2.inject.Inject;
import org.apache.struts2.ognl.OgnlUtil;
import org.apache.struts2.ognl.ThreadAllowlist;
import org.apache.struts2.util.ProxyService;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.indexOfAny;
import static org.apache.struts2.security.DefaultAcceptedPatternsChecker.NESTING_CHARS;
import static org.apache.struts2.security.DefaultAcceptedPatternsChecker.NESTING_CHARS_STR;

/**
 * Default {@link ParameterAllowlister}. Registers the root property's class (and generic type args for {@code depth >= 2})
 * into the OGNL {@link ThreadAllowlist} so OGNL may introspect and traverse a nested path on the value stack. Logic is
 * extracted verbatim from {@code ParametersInterceptor.performOgnlAllowlisting} so the OGNL parameter and cookie
 * channels share a single implementation.
 *
 * <p>No-ops when:
 * <ul>
 *   <li>{@code paramDepth == 0} — shallow setter; OGNL does not need to traverse</li>
 *   <li>the root property has no {@code @StrutsParameter} annotation reachable via {@link java.beans.PropertyDescriptor}
 *       or as a public field (e.g. a {@code ModelDriven} model whose properties are not individually annotated). A
 *       {@code LOG.debug} surfaces this case so the gap between authorization and OGNL traversal is observable.</li>
 * </ul>
 *
 * @since 7.2.0
 */
public class OgnlParameterAllowlister implements ParameterAllowlister {

    private static final Logger LOG = LogManager.getLogger(OgnlParameterAllowlister.class);

    private OgnlUtil ognlUtil;
    private ProxyService proxyService;
    private ThreadAllowlist threadAllowlist;

    @Inject
    public void setOgnlUtil(OgnlUtil ognlUtil) {
        this.ognlUtil = ognlUtil;
    }

    @Inject
    public void setProxyService(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    @Inject
    public void setThreadAllowlist(ThreadAllowlist threadAllowlist) {
        this.threadAllowlist = threadAllowlist;
    }

    @Override
    public void primeAllowlistForPath(String parameterName, Object target) {
        if (parameterName == null || parameterName.isEmpty() || target == null) {
            return;
        }
        long paramDepth = parameterName.codePoints().mapToObj(c -> (char) c).filter(NESTING_CHARS::contains).count();
        if (paramDepth == 0) {
            return;
        }

        int nestingIndex = indexOfAny(parameterName, NESTING_CHARS_STR);
        String rootProperty = nestingIndex == -1 ? parameterName : parameterName.substring(0, nestingIndex);
        String normalisedRootProperty = Character.toLowerCase(rootProperty.charAt(0)) + rootProperty.substring(1);

        if (allowlistViaPropertyDescriptor(target, normalisedRootProperty, paramDepth)) {
            return;
        }
        if (allowlistViaPublicField(target, normalisedRootProperty, paramDepth)) {
            return;
        }
        // Authorization passed but no @StrutsParameter on the root property — e.g. ModelDriven model with no
        // per-property annotations. OGNL won't be able to walk this nested path; surface the gap in logs.
        LOG.debug("Parameter [{}] authorized but no @StrutsParameter on root property [{}] of [{}]; "
                + "OGNL allowlist not primed and nested traversal may be blocked",
                parameterName, normalisedRootProperty, ultimateClass(target).getSimpleName());
    }

    private boolean allowlistViaPropertyDescriptor(Object target, String rootProperty, long paramDepth) {
        BeanInfo beanInfo = getBeanInfo(target);
        if (beanInfo == null) {
            return false;
        }
        Optional<PropertyDescriptor> propDescOpt = Arrays.stream(beanInfo.getPropertyDescriptors())
                .filter(desc -> desc.getName().equals(rootProperty)).findFirst();
        if (propDescOpt.isEmpty()) {
            return false;
        }
        PropertyDescriptor propDesc = propDescOpt.get();
        Method relevantMethod = propDesc.getReadMethod();
        if (relevantMethod == null || getPermittedInjectionDepth(relevantMethod) < paramDepth) {
            return false;
        }
        allowlistClass(propDesc.getPropertyType());
        if (paramDepth >= 2) {
            allowlistParameterizedTypeArg(relevantMethod.getGenericReturnType());
        }
        return true;
    }

    private boolean allowlistViaPublicField(Object target, String rootProperty, long paramDepth) {
        Class<?> targetClass = ultimateClass(target);
        Field field;
        try {
            field = targetClass.getDeclaredField(rootProperty);
        } catch (NoSuchFieldException e) {
            return false;
        }
        if (!Modifier.isPublic(field.getModifiers()) || getPermittedInjectionDepth(field) < paramDepth) {
            return false;
        }
        allowlistClass(field.getType());
        if (paramDepth >= 2) {
            allowlistParameterizedTypeArg(field.getGenericType());
        }
        return true;
    }

    private void allowlistClass(Class<?> clazz) {
        threadAllowlist.allowClassHierarchy(clazz);
    }

    private void allowlistParameterizedTypeArg(Type genericType) {
        if (!(genericType instanceof ParameterizedType pType)) {
            return;
        }
        Type[] paramTypes = pType.getActualTypeArguments();
        allowlistParamType(paramTypes[0]);
        if (paramTypes.length > 1) {
            allowlistParamType(paramTypes[1]);
        }
    }

    private void allowlistParamType(Type paramType) {
        if (paramType instanceof Class<?> clazz) {
            allowlistClass(clazz);
        }
    }

    private int getPermittedInjectionDepth(AnnotatedElement element) {
        StrutsParameter annotation = element.getAnnotation(StrutsParameter.class);
        return annotation == null ? -1 : annotation.depth();
    }

    private Class<?> ultimateClass(Object target) {
        if (proxyService.isProxy(target)) {
            return proxyService.ultimateTargetClass(target);
        }
        return target.getClass();
    }

    private BeanInfo getBeanInfo(Object target) {
        Class<?> targetClass = ultimateClass(target);
        try {
            return ognlUtil.getBeanInfo(targetClass);
        } catch (IntrospectionException e) {
            LOG.warn("Error introspecting target {} for OGNL allowlisting", targetClass, e);
            return null;
        }
    }
}
