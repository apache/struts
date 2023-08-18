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
package org.apache.struts2.junit;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionProxy;
import org.apache.struts2.StrutsStatics;

import javax.portlet.PortletContext;

public class StrutsPortletTestCaseTest extends StrutsPortletTestCase {

    String KEY = "my-param";

    public void testShouldPortletContextBeAvailable() throws Exception {
        // given
        assertNull(ActionContext.getContext().get(StrutsStatics.STRUTS_PORTLET_CONTEXT));

        // when
        ActionProxy proxy = getActionProxy("/test/testAction.action");
        String result = proxy.execute();

        // then
        assertEquals(Action.SUCCESS, result);
        Object portletContext = ActionContext.getContext().get(StrutsStatics.STRUTS_PORTLET_CONTEXT);
        assertNotNull(portletContext);
        assertTrue(portletContext instanceof PortletContext);
    }

    public void testShouldAdditionalContextParamsBeAvailable() throws Exception {
        // given
        assertNull(ActionContext.getContext().get(KEY));

        // when
        ActionProxy proxy = getActionProxy("/test/testAction.action");
        String result = proxy.execute();

        // then
        assertEquals(Action.SUCCESS, result);
        assertNotNull(ActionContext.getContext().get(KEY));
    }

    @Override
    protected void applyAdditionalParams(ActionContext context) {
        context.put(KEY, new Object());
    }
}
