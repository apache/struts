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

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.velocity.VelocityContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class StrutsVelocityContextTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private StrutsVelocityContext strutsVelocityContext;

    @Mock
    private VelocityContext chainedContext;

    @Mock
    private ValueStack stack;

    private Map<String, Object> stackContext;

    @Before
    public void setUp() throws Exception {
        stackContext = new HashMap<>();
        when(stack.getContext()).thenReturn(stackContext);
        strutsVelocityContext = new StrutsVelocityContext(singletonList(chainedContext), stack);
    }

    @Test
    public void getChainedValue() {
        when(chainedContext.internalGet("foo")).thenReturn("bar");
        assertEquals("bar", strutsVelocityContext.internalGet("foo"));
    }

    @Test
    public void getStackValue() {
        when(stack.findValue("foo")).thenReturn("bar");
        assertEquals("bar", strutsVelocityContext.internalGet("foo"));
    }

    @Test
    public void getStackContextValue() {
        stackContext.put("foo", "bar");
        assertEquals("bar", strutsVelocityContext.internalGet("foo"));
    }

    @Test
    public void getValuePrecedence() {
        when(chainedContext.internalGet("foo")).thenReturn("bar");
        assertEquals("bar", strutsVelocityContext.internalGet("foo"));

        when(stack.findValue("foo")).thenReturn("baz");
        assertEquals("bar", strutsVelocityContext.internalGet("foo"));

        stackContext.put("foo", "qux");
        assertEquals("bar", strutsVelocityContext.internalGet("foo"));
    }
}
