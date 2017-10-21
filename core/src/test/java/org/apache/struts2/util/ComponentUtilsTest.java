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
package org.apache.struts2.util;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsInternalTestCase;

public class ComponentUtilsTest extends StrutsInternalTestCase {

    public void testStripExpression() throws Exception {
        // given
        ValueStack stack = container.getInstance(ValueStackFactory.class).createValueStack();
        String anExpression = "%{foo}";

        // when
        String actual = ComponentUtils.stripExpressionIfAltSyntax(stack, anExpression);

        // then
        assertEquals(actual, "foo");
    }

    public void testNoStripExpressionIfNoAltSyntax() throws Exception {
        // given
        loadConfigurationProviders(new MockConfigurationProvider());
        ValueStack stack = container.getInstance(ValueStackFactory.class).createValueStack();
        String anExpression = "%{foo}";

        // when
        String actual = ComponentUtils.stripExpressionIfAltSyntax(stack, anExpression);

        // then
        assertEquals(actual, "%{foo}");
    }

    public void testAltSyntaxIsTrue() throws Exception {
        // given
        ValueStack stack = container.getInstance(ValueStackFactory.class).createValueStack();

        // when
        boolean actual = ComponentUtils.altSyntax(stack);

        // then
        assertTrue(actual);
    }

    public void testAltSyntaxIsFalse() throws Exception {
        // given
        loadConfigurationProviders(new MockConfigurationProvider());
        ValueStack stack = container.getInstance(ValueStackFactory.class).createValueStack();

        // when
        boolean actual = ComponentUtils.altSyntax(stack);

        // then
        assertFalse(actual);
    }

    public void testIsExpressionIsTrue() throws Exception {
        // given
        String anExpression = "%{foo}";

        // when
        boolean actual = ComponentUtils.isExpression(anExpression);

        // then
        assertTrue(actual);
    }

    public void testIsExpressionIsFalseWhenCombined() throws Exception {
        // given
        String anExpression = "bar%{foo}";

        // when
        boolean actual = ComponentUtils.isExpression(anExpression);

        // then
        assertFalse(actual);
    }

    public void testIsExpressionIsFalse() throws Exception {
        // given
        String anExpression = "foo";

        // when
        boolean actual = ComponentUtils.isExpression(anExpression);

        // then
        assertFalse(actual);
    }

    public void testIsExpressionIsFalseWhenNull() throws Exception {
        assertFalse(ComponentUtils.isExpression(null));
    }

    public void testContainsExpressionIsTrue() throws Exception {
        // given
        String anExpression = "%{foo}";

        // when
        boolean actual = ComponentUtils.containsExpression(anExpression);

        // then
        assertTrue(actual);
    }

    public void testIsContainsIsTrueWhenCombined() throws Exception {
        // given
        String anExpression = "bar%{foo}";

        // when
        boolean actual = ComponentUtils.containsExpression(anExpression);

        // then
        assertTrue(actual);
    }

    public void testContainsExpressionIsFalse() throws Exception {
        // given
        String anExpression = "foo";

        // when
        boolean actual = ComponentUtils.containsExpression(anExpression);

        // then
        assertFalse(actual);
    }

    public void testContainsExpressionIsFalseWhenNull() throws Exception {
        assertFalse(ComponentUtils.containsExpression(null));
    }
}

class MockConfigurationProvider implements ConfigurationProvider {

    public void destroy() {
    }

    public void init(Configuration configuration) throws ConfigurationException {
    }

    public boolean needsReload() {
        return false;
    }

    public void loadPackages() throws ConfigurationException {
    }

    public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
        builder.constant(StrutsConstants.STRUTS_TAG_ALTSYNTAX, "false");
    }
}
