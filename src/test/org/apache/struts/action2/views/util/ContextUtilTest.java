/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.util;

import org.apache.struts.action2.config.Configuration;
import org.apache.struts.action2.StrutsConstants;
import com.opensymphony.xwork.util.OgnlValueStack;
import junit.framework.TestCase;

/**
 * Test case for ContextUtil
 * 
 * @author tm_jee
 * @version $Date: 2005/12/06 01:10:41 $ $Id: ContextUtilTest.java,v 1.4 2005/12/06 01:10:41 digi9ten Exp $
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
