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

import com.opensymphony.xwork2.XWorkConstants;
import com.opensymphony.xwork2.XWorkTestCase;

public class OgnlValueStackFactoryTest extends XWorkTestCase {

    public void testAllowAccessStaticMethods() {
        loadButAdd(String.class, XWorkConstants.ALLOW_STATIC_METHOD_ACCESS, "true");
        OgnlValueStackFactory factory = new OgnlValueStackFactory();
        container.inject(factory);

        assertTrue(factory.isAllowStaticMethodAccess());
    }

    public void testDisallowAccessStaticMethods() {
        loadButAdd(String.class, XWorkConstants.ALLOW_STATIC_METHOD_ACCESS, "false");
        OgnlValueStackFactory factory = new OgnlValueStackFactory();
        container.inject(factory);

        assertFalse(factory.isAllowStaticMethodAccess());
    }
}