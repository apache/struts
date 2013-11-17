/*
 * $Id$
 *
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

package org.apache.struts2.views.util;

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsInternalTestCase;

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * Test case for ContextUtil
 *
 */
public class ContextUtilTest extends StrutsInternalTestCase {

    private void setAltSyntax(ValueStack stack, String val) {
        Mock container = new Mock(Container.class);
        container.expectAndReturn("getInstance", C.args(C.eq(String.class), C.eq(StrutsConstants.STRUTS_TAG_ALTSYNTAX)), val);
        stack.getContext().put(ActionContext.CONTAINER, container.proxy());
    }
    
    public void testAltSyntaxMethod1() throws Exception {
        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.getContext().put("useAltSyntax", "true");

        setAltSyntax(stack, "true");
        assertTrue(ContextUtil.isUseAltSyntax(stack.getContext()));
    }

    public void testAltSyntaxMethod2() throws Exception {
        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.getContext().put("useAltSyntax", "false");

        setAltSyntax(stack, "true");
        assertTrue(ContextUtil.isUseAltSyntax(stack.getContext()));
    }

    public void testAltSyntaxMethod3() throws Exception {
        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.getContext().put("useAltSyntax", "true");

        setAltSyntax(stack, "false");
        assertTrue(ContextUtil.isUseAltSyntax(stack.getContext()));
    }

    public void testAltSyntaxMethod4() throws Exception {
        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.getContext().put("useAltSyntax", "false");

        setAltSyntax(stack, "false");
        assertFalse(ContextUtil.isUseAltSyntax(stack.getContext()));
    }

    //========================================================

    public void testAltSyntaxMethod5() throws Exception {
        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.getContext().put("useAltSyntax", Boolean.TRUE);

        setAltSyntax(stack, "true");
        assertTrue(ContextUtil.isUseAltSyntax(stack.getContext()));
    }
    public void testAltSyntaxMethod6() throws Exception {
        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.getContext().put("useAltSyntax", Boolean.FALSE);

        setAltSyntax(stack, "true");
        assertTrue(ContextUtil.isUseAltSyntax(stack.getContext()));
    }
    public void testAltSyntaxMethod7() throws Exception {
        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.getContext().put("useAltSyntax", Boolean.TRUE);

        setAltSyntax(stack, "false");
        assertTrue(ContextUtil.isUseAltSyntax(stack.getContext()));
    }
    public void testAltSyntaxMethod8() throws Exception {
        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.getContext().put("useAltSyntax", Boolean.FALSE);

        setAltSyntax(stack, "false");
        assertFalse(ContextUtil.isUseAltSyntax(stack.getContext()));
    }

    // ==========================================
    public void testAltSyntaxMethod9() throws Exception {
        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.getContext().put("useAltSyntax", null);

        setAltSyntax(stack, "true");
        assertTrue(ContextUtil.isUseAltSyntax(stack.getContext()));
    }
}
