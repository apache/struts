/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp;

import com.mockobjects.servlet.MockJspWriter;
import com.mockobjects.servlet.MockPageContext;
import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.webwork.components.If;
import com.opensymphony.xwork.util.OgnlValueStack;
import junit.framework.TestCase;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;


/**
 * @author $Author: rainerh $
 * @version $Revision: 1.10 $
 */
public class ElseTagTest extends TestCase {

    ElseTag elseTag;
    MockPageContext pageContext;
    OgnlValueStack stack;


    public void testTestFalse() {
    	stack.getContext().put(If.ANSWER, new Boolean(false));

        int result = 0;

        try {
        	elseTag.setPageContext(pageContext);
            result = elseTag.doStartTag();
            elseTag.doEndTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }
        assertEquals(TagSupport.EVAL_BODY_INCLUDE, result);
    }

    public void testTestNull() {
        elseTag.setPageContext(pageContext);

        int result = 0;

        try {
            result = elseTag.doStartTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(TagSupport.SKIP_BODY, result);
    }

    public void testTestTrue() {
    	stack.getContext().put(If.ANSWER, new Boolean(true));
        elseTag.setPageContext(pageContext);

        int result = 0;

        try {
            result = elseTag.doStartTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(TagSupport.SKIP_BODY, result);
    }

    protected void setUp() throws Exception {
        // create the needed objects
        elseTag = new ElseTag();
        stack = new OgnlValueStack();

        // create the mock http servlet request
        WebWorkMockHttpServletRequest request = new WebWorkMockHttpServletRequest();
        
        // NOTE: in WW Tag library, TagUtil gets stack from request, which will be set
        //       when request going through the FilterDispatcher --> DispatcherUtil etc. route
        request.setAttribute(ServletActionContext.WEBWORK_VALUESTACK_KEY, stack);

        WebWorkMockServletContext servletContext = new WebWorkMockServletContext();
        servletContext.setServletInfo("not-weblogic");
        
        // create the mock page context
        pageContext = new WebWorkMockPageContext();
        pageContext.setRequest(request);
        pageContext.setServletContext(servletContext);
        pageContext.setJspWriter(new MockJspWriter());
    }


    class Foo {
        int num;

        public void setNum(int num) {
            this.num = num;
        }

        public int getNum() {
            return num;
        }
    }
}
