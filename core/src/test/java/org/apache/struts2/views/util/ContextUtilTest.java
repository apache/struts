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

import org.apache.struts2.config.Configuration;
import org.apache.struts2.StrutsConstants;
import com.opensymphony.xwork.util.OgnlValueStack;
import junit.framework.TestCase;

/**
 * Test case for ContextUtil
 * 
 */
public class ContextUtilTest extends TestCase {

    public void testAltSyntaxMethod1() throws Exception {
        OgnlValueStack stack = new OgnlValueStack();
        stack.getContext().put("useAltSyntax", "true");
        
        Configuration.reset();
        Configuration.set(StrutsConstants.STRUTS_TAG_ALTSYNTAX, "true");
        
        assertEquals(Configuration.getString(StrutsConstants.STRUTS_TAG_ALTSYNTAX), "true");
        assertTrue(ContextUtil.isUseAltSyntax(stack.getContext()));
    }
    
    public void testAltSyntaxMethod2() throws Exception {
        OgnlValueStack stack = new OgnlValueStack();
        stack.getContext().put("useAltSyntax", "false");
        
        Configuration.reset();
        Configuration.set(StrutsConstants.STRUTS_TAG_ALTSYNTAX, "true");
        
        assertEquals(Configuration.getString(StrutsConstants.STRUTS_TAG_ALTSYNTAX), "true");
        assertTrue(ContextUtil.isUseAltSyntax(stack.getContext()));
    }
    
    public void testAltSyntaxMethod3() throws Exception {
        OgnlValueStack stack = new OgnlValueStack();
        stack.getContext().put("useAltSyntax", "true");
        
        Configuration.reset();
        Configuration.set(StrutsConstants.STRUTS_TAG_ALTSYNTAX, "false");
        
        assertEquals(Configuration.getString(StrutsConstants.STRUTS_TAG_ALTSYNTAX), "false");
        assertTrue(ContextUtil.isUseAltSyntax(stack.getContext()));
    }
    
    public void testAltSyntaxMethod4() throws Exception {
        OgnlValueStack stack = new OgnlValueStack();
        stack.getContext().put("useAltSyntax", "false");
        
        Configuration.reset();
        Configuration.set(StrutsConstants.STRUTS_TAG_ALTSYNTAX, "false");
        
        assertEquals(Configuration.getString(StrutsConstants.STRUTS_TAG_ALTSYNTAX), "false");
        assertFalse(ContextUtil.isUseAltSyntax(stack.getContext()));
    }
    
    //========================================================
    
    public void testAltSyntaxMethod5() throws Exception {
        OgnlValueStack stack = new OgnlValueStack();
        stack.getContext().put("useAltSyntax", Boolean.TRUE);
        
        Configuration.reset();
        Configuration.set(StrutsConstants.STRUTS_TAG_ALTSYNTAX, "true");
        
        assertEquals(Configuration.getString(StrutsConstants.STRUTS_TAG_ALTSYNTAX), "true");
        assertTrue(ContextUtil.isUseAltSyntax(stack.getContext()));
    }
    public void testAltSyntaxMethod6() throws Exception {
        OgnlValueStack stack = new OgnlValueStack();
        stack.getContext().put("useAltSyntax", Boolean.FALSE);
        
        Configuration.reset();
        Configuration.set(StrutsConstants.STRUTS_TAG_ALTSYNTAX, "true");
        
        assertEquals(Configuration.getString(StrutsConstants.STRUTS_TAG_ALTSYNTAX), "true");
        assertTrue(ContextUtil.isUseAltSyntax(stack.getContext()));
    }
    public void testAltSyntaxMethod7() throws Exception {
        OgnlValueStack stack = new OgnlValueStack();
        stack.getContext().put("useAltSyntax", Boolean.TRUE);
        
        Configuration.reset();
        Configuration.set(StrutsConstants.STRUTS_TAG_ALTSYNTAX, "false");
        
        assertEquals(Configuration.getString(StrutsConstants.STRUTS_TAG_ALTSYNTAX), "false");
        assertTrue(ContextUtil.isUseAltSyntax(stack.getContext()));
    }
    public void testAltSyntaxMethod8() throws Exception {
        OgnlValueStack stack = new OgnlValueStack();
        stack.getContext().put("useAltSyntax", Boolean.FALSE);
        
        Configuration.reset();
        Configuration.set(StrutsConstants.STRUTS_TAG_ALTSYNTAX, "false");
        
        assertEquals(Configuration.getString(StrutsConstants.STRUTS_TAG_ALTSYNTAX), "false");
        assertFalse(ContextUtil.isUseAltSyntax(stack.getContext()));
    }
    
    // ==========================================
    public void testAltSyntaxMethod9() throws Exception {
        OgnlValueStack stack = new OgnlValueStack();
        stack.getContext().put("useAltSyntax", null);
        
        Configuration.reset();
        Configuration.set(StrutsConstants.STRUTS_TAG_ALTSYNTAX, Boolean.TRUE);
        
        assertEquals(Configuration.get(StrutsConstants.STRUTS_TAG_ALTSYNTAX), Boolean.TRUE);
        assertTrue(ContextUtil.isUseAltSyntax(stack.getContext()));
    }
}
