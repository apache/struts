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
package org.apache.struts2.dispatcher;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.StubValueStack;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;

public class PrepareOperationsTest extends StrutsInternalTestCase {
    public void testCreateActionContextWhenRequestHasOne() {
        HttpServletRequest req = new MockHttpServletRequest();
        StubValueStack stack = new StubValueStack();
        req.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, stack);

        PrepareOperations prepare = new PrepareOperations(null);

        ActionContext.setContext(null);
        ActionContext actionContext = prepare.createActionContext(req, null);

        assertEquals(stack.getContext(), actionContext.getContextMap());
    }
}
