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
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.mock.DummyTextProvider;
import com.opensymphony.xwork2.mock.InjectableAction;
import org.apache.struts2.StrutsInternalTestCase;

import java.util.HashMap;

public class ObjectFactoryTest extends StrutsInternalTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.loadButAdd(TextProvider.class, new DummyTextProvider());
    }

    public void testCreatingActionsWithInjectableParametersInConstructor() throws Exception {
        // given
        ObjectFactory of = container.getInstance(ObjectFactory.class);

        // when
        InjectableAction action = (InjectableAction) of.buildBean(InjectableAction.class, new HashMap<String, Object>());

        // then
        assertNotNull(action.getTextProvider());
        assertTrue(action.getTextProvider() instanceof DummyTextProvider);
    }
}