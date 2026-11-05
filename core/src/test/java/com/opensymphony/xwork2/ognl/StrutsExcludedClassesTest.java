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

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.providers.StrutsDefaultConfigurationProvider;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.test.StubConfigurationProvider;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import ognl.Ognl;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.config.DefaultPropertiesProvider;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;

import java.beans.Expression;
import java.beans.Statement;
import java.beans.beancontext.BeanContextSupport;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Pins the exclusion lists shipped in {@code struts-excluded-classes.xml}. The allowlist is switched off so
 * that the exclusion lists are the only layer under test, which is the default on this line.
 */
public class StrutsExcludedClassesTest extends XWorkTestCase {

    private SecurityMemberAccess sma;
    private Map<String, Object> context;

    private static Map<String, String> overrides(String... keyValues) {
        Map<String, String> overrides = new HashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            overrides.put(keyValues[i], keyValues[i + 1]);
        }
        return overrides;
    }

    private void loadShippedExclusions(Map<String, String> overrides) {
        loadConfigurationProviders(
                new StrutsDefaultConfigurationProvider(),
                new DefaultPropertiesProvider(),
                new StrutsXmlConfigurationProvider("struts-excluded-classes.xml"),
                new StubConfigurationProvider() {
                    @Override
                    public void register(ContainerBuilder builder, LocatableProperties props) {
                        props.setProperty(StrutsConstants.STRUTS_ALLOWLIST_ENABLE, "false");
                        overrides.forEach(props::setProperty);
                    }
                });
        sma = ((OgnlValueStack) container.getInstance(ValueStackFactory.class).createValueStack()).securityMemberAccess;
        context = Ognl.createDefaultContext(null, sma);
    }

    private boolean accessible(Class<?> declaringClass, String methodName, Class<?>... parameterTypes) throws Exception {
        return accessibleOn(null, declaringClass, methodName, parameterTypes);
    }

    private boolean accessibleOn(Object target, Class<?> declaringClass, String methodName, Class<?>... parameterTypes) throws Exception {
        Member member = declaringClass.getMethod(methodName, parameterTypes);
        return sma.isAccessible(context, target, member, null);
    }

    public void testJavaBeansIsExcluded() throws Exception {
        loadShippedExclusions(overrides());

        assertThat(accessible(Expression.class, "getValue")).isFalse();
        assertThat(accessible(Statement.class, "execute")).isFalse();
        assertThat(accessible(String.class, "length")).isTrue();
    }

    public void testJavaBeansTargetIsExcludedWhenTheMemberIsDeclaredElsewhere() throws Exception {
        loadShippedExclusions(overrides());

        assertThat(accessibleOn(new BeanContextSupport(), Collection.class, "size")).isFalse();
        assertThat(accessibleOn(new ArrayList<>(), Collection.class, "size")).isTrue();
    }

    public void testReflectionPackagesAreExcluded() throws Exception {
        loadShippedExclusions(overrides());

        assertThat(accessible(Method.class, "invoke", Object.class, Object[].class)).isFalse();
        assertThat(accessible(MethodHandle.class, "invokeWithArguments", Object[].class)).isFalse();
    }

    public void testExcludedPackageNamesSettingReplacesTheShippedList() throws Exception {
        loadShippedExclusions(overrides(StrutsConstants.STRUTS_EXCLUDED_PACKAGE_NAMES, "antlr.build"));

        assertThat(accessible(Expression.class, "getValue")).isTrue();
    }

    /**
     * The production list is overridden to a single harmless entry, so only the dev-mode list can be the
     * one denying access here.
     */
    public void testDevModeListExcludesTheSamePackages() throws Exception {
        loadShippedExclusions(overrides(
                StrutsConstants.STRUTS_DEVMODE, "true",
                StrutsConstants.STRUTS_EXCLUDED_PACKAGE_NAMES, "antlr.build"));

        assertThat(accessible(Expression.class, "getValue")).isFalse();
        assertThat(accessible(Method.class, "invoke", Object.class, Object[].class)).isFalse();
        assertThat(accessible(MethodHandle.class, "invokeWithArguments", Object[].class)).isFalse();
        assertThat(accessible(String.class, "length")).isTrue();
    }

    public void testExpressionConstructedInOgnlIsRejected() throws Exception {
        loadShippedExclusions(overrides());
        ValueStack stack = ActionContext.getContext().getValueStack();

        Member constructor = Expression.class.getConstructor(Object.class, String.class, Object[].class);
        assertThat(sma.isAccessible(context, Expression.class, constructor, null)).isFalse();

        assertThat(stack.findValue("'abc'.length()", true)).isEqualTo(3);
        assertThatThrownBy(() -> stack.findValue("new java.beans.Expression('abc','length',null).getValue()", true))
                .isNotNull();
    }
}
