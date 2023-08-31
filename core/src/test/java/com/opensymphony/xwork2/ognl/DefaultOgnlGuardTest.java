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

import com.opensymphony.xwork2.config.ConfigurationException;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class DefaultOgnlGuardTest {

    private DefaultOgnlGuard defaultOgnlGuard;

    @Before
    public void setUp() throws Exception {
        defaultOgnlGuard = new DefaultOgnlGuard();
    }

    @Test
    public void notConfigured() throws Exception {
        String expr = "1+1";
        assertFalse(defaultOgnlGuard.isBlocked(expr));
    }

    @Test
    public void nonNodeTree() throws Exception {
        assertFalse(defaultOgnlGuard.isParsedTreeBlocked("String"));
    }

    @Test
    public void nonExistentClass() throws Exception {
        ConfigurationException e = assertThrows(ConfigurationException.class, () -> defaultOgnlGuard.useExcludedNodeTypes("ognl.Kusal"));
        assertThat(e).hasMessageContaining("does not exist or cannot be loaded");
    }

    @Test
    public void invalidClass() throws Exception {
        ConfigurationException e = assertThrows(ConfigurationException.class, () -> defaultOgnlGuard.useExcludedNodeTypes("ognl.ElementsAccessor"));
        assertThat(e).hasMessageContaining("is not a subclass of ognl.Node");
    }

    @Test
    public void addBlocked() throws Exception {
        String expr = "1+1";
        assertFalse(defaultOgnlGuard.isBlocked(expr));

        defaultOgnlGuard.useExcludedNodeTypes("ognl.ASTAdd");
        assertTrue(defaultOgnlGuard.isBlocked(expr));
    }

    @Test
    public void nestedAddBlocked() throws Exception {
        String expr = "{'a',1+1}";
        assertFalse(defaultOgnlGuard.isBlocked(expr));

        defaultOgnlGuard.useExcludedNodeTypes("ognl.ASTAdd");
        assertTrue(defaultOgnlGuard.isBlocked(expr));
    }
}
