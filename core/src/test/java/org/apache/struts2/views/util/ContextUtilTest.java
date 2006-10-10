/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.views.util;

import junit.framework.TestCase;

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.config.Settings;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;

/**
 * Test case for ContextUtil
 * 
 */
public class ContextUtilTest extends TestCase {

    public void testAltSyntaxMethod1() throws Exception {
        ValueStack stack = ValueStackFactory.getFactory().createValueStack();
        stack.getContext().put("useAltSyntax", "true");
        
        Settings.reset();
        Settings.set(StrutsConstants.STRUTS_TAG_ALTSYNTAX, "true");
        
        assertEquals(Settings.get(StrutsConstants.STRUTS_TAG_ALTSYNTAX), "true");
        assertTrue(ContextUtil.isUseAltSyntax(stack.getContext()));
    }
    
    public void testAltSyntaxMethod2() throws Exception {
        ValueStack stack = ValueStackFactory.getFactory().createValueStack();
        stack.getContext().put("useAltSyntax", "false");
        
        Settings.reset();
        Settings.set(StrutsConstants.STRUTS_TAG_ALTSYNTAX, "true");
        
        assertEquals(Settings.get(StrutsConstants.STRUTS_TAG_ALTSYNTAX), "true");
        assertTrue(ContextUtil.isUseAltSyntax(stack.getContext()));
    }
    
    public void testAltSyntaxMethod3() throws Exception {
        ValueStack stack = ValueStackFactory.getFactory().createValueStack();
        stack.getContext().put("useAltSyntax", "true");
        
        Settings.reset();
        Settings.set(StrutsConstants.STRUTS_TAG_ALTSYNTAX, "false");
        
        assertEquals(Settings.get(StrutsConstants.STRUTS_TAG_ALTSYNTAX), "false");
        assertTrue(ContextUtil.isUseAltSyntax(stack.getContext()));
    }
    
    public void testAltSyntaxMethod4() throws Exception {
        ValueStack stack = ValueStackFactory.getFactory().createValueStack();
        stack.getContext().put("useAltSyntax", "false");
        
        Settings.reset();
        Settings.set(StrutsConstants.STRUTS_TAG_ALTSYNTAX, "false");
        
        assertEquals(Settings.get(StrutsConstants.STRUTS_TAG_ALTSYNTAX), "false");
        assertFalse(ContextUtil.isUseAltSyntax(stack.getContext()));
    }
    
    //========================================================
    
    public void testAltSyntaxMethod5() throws Exception {
        ValueStack stack = ValueStackFactory.getFactory().createValueStack();
        stack.getContext().put("useAltSyntax", Boolean.TRUE);
        
        Settings.reset();
        Settings.set(StrutsConstants.STRUTS_TAG_ALTSYNTAX, "true");
        
        assertEquals(Settings.get(StrutsConstants.STRUTS_TAG_ALTSYNTAX), "true");
        assertTrue(ContextUtil.isUseAltSyntax(stack.getContext()));
    }
    public void testAltSyntaxMethod6() throws Exception {
        ValueStack stack = ValueStackFactory.getFactory().createValueStack();
        stack.getContext().put("useAltSyntax", Boolean.FALSE);
        
        Settings.reset();
        Settings.set(StrutsConstants.STRUTS_TAG_ALTSYNTAX, "true");
        
        assertEquals(Settings.get(StrutsConstants.STRUTS_TAG_ALTSYNTAX), "true");
        assertTrue(ContextUtil.isUseAltSyntax(stack.getContext()));
    }
    public void testAltSyntaxMethod7() throws Exception {
        ValueStack stack = ValueStackFactory.getFactory().createValueStack();
        stack.getContext().put("useAltSyntax", Boolean.TRUE);
        
        Settings.reset();
        Settings.set(StrutsConstants.STRUTS_TAG_ALTSYNTAX, "false");
        
        assertEquals(Settings.get(StrutsConstants.STRUTS_TAG_ALTSYNTAX), "false");
        assertTrue(ContextUtil.isUseAltSyntax(stack.getContext()));
    }
    public void testAltSyntaxMethod8() throws Exception {
        ValueStack stack = ValueStackFactory.getFactory().createValueStack();
        stack.getContext().put("useAltSyntax", Boolean.FALSE);
        
        Settings.reset();
        Settings.set(StrutsConstants.STRUTS_TAG_ALTSYNTAX, "false");
        
        assertEquals(Settings.get(StrutsConstants.STRUTS_TAG_ALTSYNTAX), "false");
        assertFalse(ContextUtil.isUseAltSyntax(stack.getContext()));
    }
    
    // ==========================================
    public void testAltSyntaxMethod9() throws Exception {
        ValueStack stack = ValueStackFactory.getFactory().createValueStack();
        stack.getContext().put("useAltSyntax", null);
        
        Settings.reset();
        Settings.set(StrutsConstants.STRUTS_TAG_ALTSYNTAX, "true");
        
        assertEquals(Settings.get(StrutsConstants.STRUTS_TAG_ALTSYNTAX), "true");
        assertTrue(ContextUtil.isUseAltSyntax(stack.getContext()));
    }
}
