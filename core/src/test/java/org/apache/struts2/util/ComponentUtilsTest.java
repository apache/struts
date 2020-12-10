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

import org.apache.struts2.StrutsInternalTestCase;

public class ComponentUtilsTest extends StrutsInternalTestCase {

    public void testStripExpression() {
        // given
        String anExpression = "%{foo}";

        // when
        String actual = ComponentUtils.stripExpression(anExpression);

        // then
        assertEquals(actual, "foo");
    }

    public void testIsExpressionIsTrue() {
        // given
        String anExpression = "%{foo}";

        // when
        boolean actual = ComponentUtils.isExpression(anExpression);

        // then
        assertTrue(actual);
    }

    public void testIsExpressionIsFalseWhenCombined() {
        // given
        String anExpression = "bar%{foo}";

        // when
        boolean actual = ComponentUtils.isExpression(anExpression);

        // then
        assertFalse(actual);
    }

    public void testIsExpressionIsFalse() {
        // given
        String anExpression = "foo";

        // when
        boolean actual = ComponentUtils.isExpression(anExpression);

        // then
        assertFalse(actual);
    }

    public void testIsExpressionIsFalseWhenNull() {
        assertFalse(ComponentUtils.isExpression(null));
    }

    public void testContainsExpressionIsTrue() {
        // given
        String anExpression = "%{foo}";

        // when
        boolean actual = ComponentUtils.containsExpression(anExpression);

        // then
        assertTrue(actual);
    }

    public void testIsContainsIsTrueWhenCombined() {
        // given
        String anExpression = "bar%{foo}";

        // when
        boolean actual = ComponentUtils.containsExpression(anExpression);

        // then
        assertTrue(actual);
    }

    public void testContainsExpressionIsFalse() {
        // given
        String anExpression = "foo";

        // when
        boolean actual = ComponentUtils.containsExpression(anExpression);

        // then
        assertFalse(actual);
    }

    public void testContainsExpressionIsFalseWhenNull() {
        assertFalse(ComponentUtils.containsExpression(null));
    }
}
