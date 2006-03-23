/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.xslt;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.mock.MockActionInvocation;
import com.opensymphony.xwork.util.OgnlValueStack;
import junit.framework.TestCase;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

import javax.xml.transform.TransformerException;
import java.util.List;
import java.util.ArrayList;

/**
 * Unit test for {@link XSLTResult}.
 *
 * @author Claus Ibsen
 */
public class XSLTResultTest extends TestCase {

    private XSLTResult result;
    private MockHttpServletResponse response;
    private MockHttpServletRequest request;
    private MockServletContext servletContext;
    private MockActionInvocation mai;
    private OgnlValueStack stack;

    public void testNoLocation() throws Exception {
        try {
            result.setParse(false);
            result.setLocation(null);
            result.execute(mai);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    public void testNoFileFound() throws Exception {
        try {
            result.setParse(false);
            result.setLocation("nofile.xsl");
            result.execute(mai);
            fail("Should have thrown a TransformerException");
        } catch (TransformerException e) {
            // success
        }
    }

    public void testSimpleTransform() throws Exception {
        // TODO: XSLTResult does not work with JDK1.5

        /*result.setParse(false);
        result.setLocation("XSLTResultTest.xsl");
        result.execute(mai);

        String out = response.getContentAsString();
        assertTrue(out.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        assertTrue(out.indexOf("<result xmlns=\"http://www.w3.org/TR/xhtml1/strict\"") > -1);*/
    }

    public void testSimpleTransformParse() throws Exception {
        /*result.setParse(true);
        result.setLocation("${top.myLocation}");
        result.execute(mai);

        String out = response.getContentAsString();
        assertTrue(out.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        assertTrue(out.indexOf("<result xmlns=\"http://www.w3.org/TR/xhtml1/strict\"") > -1);*/
    }

    public void testTransform2() throws Exception {
        /*result.setParse(false);
        result.setLocation("XSLTResultTest2.xsl");
        result.execute(mai);

        String out = response.getContentAsString();
        assertTrue(out.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        assertTrue(out.indexOf("<html xmlns=\"http://www.w3.org/TR/xhtml1/strict\"") > -1);
        assertTrue(out.indexOf("Hello Santa Claus how are you?") > -1);*/
    }

    public void testTransform3() throws Exception {
        /*result.setParse(false);
        result.setLocation("XSLTResultTest3.xsl");
        result.execute(mai);

        String out = response.getContentAsString();
        assertTrue(out.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        assertTrue(out.indexOf("<html xmlns=\"http://www.w3.org/TR/xhtml1/strict\"") > -1);
        assertTrue(out.indexOf("Hello Santa Claus how are you?") > -1);
        assertTrue(out.indexOf("WebWork in Action by Patrick and Jason") > -1);*/
        // TODO: There is a bug in XSLTResult and having collections
        //assertTrue(out.indexOf("XWork not in Action by Superman") > -1);
    }

    protected void setUp() throws Exception {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        servletContext = new MockServletContext(new DefaultResourceLoader());

        result = new XSLTResult();
        stack = new OgnlValueStack();
        ActionContext.getContext().setValueStack(stack);

        MyAction action = new MyAction();

        mai = new com.opensymphony.xwork.mock.MockActionInvocation();
        mai.setAction(action);
        mai.setStack(stack);
        mai.setInvocationContext(ActionContext.getContext());
        stack.push(action);

        ActionContext.getContext().put(ServletActionContext.HTTP_REQUEST, request);
        ActionContext.getContext().put(ServletActionContext.HTTP_RESPONSE, response);
        ActionContext.getContext().put(ServletActionContext.SERVLET_CONTEXT, servletContext);
    }

    protected void tearDown() {
        request = null;
        response = null;
        servletContext = null;
        result = null;
        stack = null;
        mai = null;
    }

    private class MyAction implements Action {

        public String execute() throws Exception {
            return SUCCESS;
        }

        public String getMyLocation() {
            return ("XSLTResultTest.xsl");
        }

        public String getUsername() {
            return "Santa Claus";
        }

        public List getBooks() {
            List list = new ArrayList();
            list.add(new Book("WebWork in Action", "Patrick and Jason"));
            list.add(new Book("XWork not in Action", "Superman"));
            return list;
        }

    }

    public class Book {

        private String title;
        private String author;

        public Book(String title, String author) {
            this.title = title;
            this.author = author;
        }

        public String getTitle() {
            return title;
        }

        public String getAuthor() {
            return author;
        }
    }

}
