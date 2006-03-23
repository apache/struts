/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.dispatcher;

import com.opensymphony.util.ClassLoaderUtil;
import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.mock.MockActionInvocation;
import com.opensymphony.xwork.util.OgnlValueStack;
import junit.framework.TestCase;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 * Unit test for {@link StreamResult}.
 *
 * @author Claus Ibsen
 */
public class StreamResultTest extends TestCase {

    private StreamResult result;
    private MockHttpServletResponse response;

    private MockActionInvocation mai;
    private OgnlValueStack stack;
    private int contentLength = 0;

    public void testStreamResultNoInputName() throws Exception {
        result.setParse(false);
        result.setInputName(null);

        try {
            result.doExecute("helloworld", mai);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    public void testStreamResultParseNoInputName() throws Exception {
        result.setParse(true);
        result.setInputName("${top}");

        try {
            result.doExecute("helloworld", mai);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    public void testStreamResultDefault() throws Exception {
        result.setInputName("streamForImage");

        result.doExecute("helloworld", mai);

        assertEquals(0, result.getContentLength());
        assertEquals("text/plain", result.getContentType());
        assertEquals("streamForImage", result.getInputName());
        assertEquals(1024, result.getBufferSize()); // 1024 is default
        assertEquals("inline", result.getContentDisposition());

        assertEquals("text/plain", response.getContentType());
        assertEquals(0, response.getContentLength());
        assertEquals("inline", response.getHeader("Content-disposition"));
    }

    public void testStreamResultNoDefault() throws Exception {
        // it's not easy to test using easymock as we use getOutputStream on HttpServletResponse.
        result.setParse(false);
        result.setInputName("streamForImage");
        result.setBufferSize(128);
        result.setContentLength(contentLength);
        result.setContentDisposition("filename=\"logo.png\"");
        result.setContentType("image/jpeg");

        result.doExecute("helloworld", mai);

        assertEquals(contentLength, result.getContentLength());
        assertEquals("image/jpeg", result.getContentType());
        assertEquals("streamForImage", result.getInputName());
        assertEquals(128, result.getBufferSize());
        assertEquals("filename=\"logo.png\"", result.getContentDisposition());

        assertEquals("image/jpeg", response.getContentType());
        assertEquals(contentLength, response.getContentLength());
        assertEquals("filename=\"logo.png\"", response.getHeader("Content-disposition"));
    }

    public void testStreamResultParse() throws Exception {
        // TODO: There is a bug in StreamResult with parse = true
/*
        result.setParse(true);
        result.setInputName("${top.streamForImage}");
        result.setBufferSize(128);
        result.setContentLength(contentLength);
        result.setContentDisposition("filename=\"logo.png\"");
        result.setContentType("image/jpeg");

        result.doExecute("helloworld", mai);

        assertEquals(contentLength, result.getContentLength());
        assertEquals("image/jpeg", result.getContentType());
        assertEquals("streamForImage", result.getInputName());
        assertEquals(128, result.getBufferSize());
        assertEquals("filename=\"logo.png\"", result.getContentDisposition());

        assertEquals("image/jpeg", response.getContentType());
        assertEquals(contentLength, response.getContentLength());
        assertEquals("filename=\"logo.png\"", response.getHeader("Content-disposition"));
*/
    }

    protected void setUp() throws Exception {
        response = new MockHttpServletResponse();

        result = new StreamResult();
        stack = new OgnlValueStack();
        ActionContext.getContext().setValueStack(stack);

        MyImageAction action = new MyImageAction();
        contentLength = (int) action.getContentLength();

        mai = new com.opensymphony.xwork.mock.MockActionInvocation();
        mai.setAction(action);
        mai.setStack(stack);
        mai.setInvocationContext(ActionContext.getContext());
        stack.push(action);

        ActionContext.getContext().put(ServletActionContext.HTTP_RESPONSE, response);
    }

    protected void tearDown() {
        response = null;
        result = null;
        stack = null;
        contentLength = 0;
        mai = null;
    }

    public class MyImageAction implements Action {

        public InputStream getStreamForImage() throws Exception {
            // just use src/test/log4j.properties as test file 
            URL url = ClassLoaderUtil.getResource("log4j.properties", StreamResultTest.class);
            File file = new File(new URI(url.toString()));
            return new FileInputStream(file);
        }

        public String execute() throws Exception {
            return SUCCESS;
        }

        public long getContentLength() throws Exception {
            URL url = ClassLoaderUtil.getResource("log4j.properties", StreamResultTest.class);
            File file = new File(new URI(url.toString()));
            return file.length();
        }
    }

}
