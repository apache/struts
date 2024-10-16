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
package org.apache.struts2.views.velocity;

import org.apache.struts2.util.ValueStack;
import org.apache.velocity.VelocityContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

public class StrutsVelocityContextTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private StrutsVelocityContext strutsVelocityContext;

    @Mock
    private VelocityContext chainedContext;

    @Mock
    private ValueStack stack;

    @Before
    public void setUp() throws Exception {
        strutsVelocityContext = new StrutsVelocityContext(singletonList(chainedContext), stack);
    }

    @Test
    public void getChainedValue() {
        when(chainedContext.get("foo")).thenReturn("bar");
        assertEquals("bar", strutsVelocityContext.internalGet("foo"));
    }

    @Test
    public void getStackValue() {
        when(stack.findValue("foo")).thenReturn("bar");
        assertEquals("bar", strutsVelocityContext.internalGet("foo"));
    }

    @Test
    public void getSuperValue() {
        strutsVelocityContext.put("foo", "bar");
        assertEquals("bar", strutsVelocityContext.internalGet("foo"));
    }

    @Test
    public void getValuePrecedence() {
        when(stack.findValue("foo")).thenReturn("qux");
        assertEquals("qux", strutsVelocityContext.internalGet("foo"));

        when(chainedContext.get("foo")).thenReturn("baz");
        assertEquals("baz", strutsVelocityContext.internalGet("foo"));

        strutsVelocityContext.put("foo", "bar");
        assertEquals("bar", strutsVelocityContext.internalGet("foo"));
    }

    @Test
    public void nullArgs() {
        strutsVelocityContext = new StrutsVelocityContext((List<VelocityContext>) null, null);
        assertNull(strutsVelocityContext.internalGet("foo"));
    }
}
