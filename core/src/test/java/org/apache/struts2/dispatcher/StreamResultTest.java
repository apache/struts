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

package org.apache.struts2.dispatcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.springframework.mock.web.MockHttpServletResponse;

import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * Unit test for {@link StreamResult}.
 *
 */
public class StreamResultTest extends StrutsInternalTestCase {

    private StreamResult result;
    private MockHttpServletResponse response;

    private MockActionInvocation mai;
    private ValueStack stack;
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

        assertEquals(String.valueOf(contentLength), result.getContentLength());
        assertEquals("text/plain", result.getContentType());
        assertEquals("streamForImage", result.getInputName());
        assertEquals(1024, result.getBufferSize()); // 1024 is default
        assertEquals("inline", result.getContentDisposition());

        assertEquals("text/plain", response.getContentType());
        assertEquals(contentLength, response.getContentLength());
        assertEquals("inline", response.getHeader("Content-disposition"));
    }

    public void testStreamResultWithCharSet() throws Exception {
        result.setInputName("streamForImage");
        result.setContentCharSet("ISO-8859-1");
        result.doExecute("helloworld", mai);

        assertEquals(String.valueOf(contentLength), result.getContentLength());
        assertEquals("text/plain", result.getContentType());
        assertEquals("streamForImage", result.getInputName());
        assertEquals(1024, result.getBufferSize()); // 1024 is default
        assertEquals("inline", result.getContentDisposition());
        assertEquals("text/plain;charset=ISO-8859-1", response.getContentType());
        assertEquals(contentLength, response.getContentLength());
        assertEquals("inline", response.getHeader("Content-disposition"));
    }

    public void testStreamResultWithCharSet2() throws Exception {
        result.setParse(true);
        result.setInputName("streamForImage");
        result.setContentCharSet("${contentCharSetMethod}");

        result.doExecute("helloworld", mai);

        assertEquals(String.valueOf(contentLength), result.getContentLength());
        assertEquals("text/plain", result.getContentType());
        assertEquals("streamForImage", result.getInputName());
        assertEquals(1024, result.getBufferSize()); // 1024 is default
        assertEquals("inline", result.getContentDisposition());
        assertEquals("text/plain;charset=UTF-8", response.getContentType());
        assertEquals(contentLength, response.getContentLength());
        assertEquals("inline", response.getHeader("Content-disposition"));
    }

    public void testAllowCacheDefault() throws Exception {
        result.setInputName("streamForImage");

        result.doExecute("helloworld", mai);

        //check that that headers are not set by default        
        assertNull(response.getHeader("Pragma"));
        assertNull(response.getHeader("Cache-Control"));
    }

     public void testAllowCacheFalse() throws Exception {
        result.setInputName("streamForImage");
        result.setAllowCaching(false);
        result.doExecute("helloworld", mai);

        //check that that headers are not set by default
        assertEquals("no-cache", response.getHeader("Pragma"));
        assertEquals("no-cache", response.getHeader("Cache-Control"));
    }

    public void testStreamResultNoDefault() throws Exception {
        // it's not easy to test using easymock as we use getOutputStream on HttpServletResponse.
        result.setParse(false);
        result.setInputName("streamForImage");
        result.setBufferSize(128);
        result.setContentLength(String.valueOf(contentLength));
        result.setContentDisposition("filename=\"logo.png\"");
        result.setContentType("image/jpeg");

        result.doExecute("helloworld", mai);

        assertEquals(String.valueOf(contentLength), result.getContentLength());
        assertEquals("image/jpeg", result.getContentType());
        assertEquals("streamForImage", result.getInputName());
        assertEquals(128, result.getBufferSize());
        assertEquals("filename=\"logo.png\"", result.getContentDisposition());

        assertEquals("image/jpeg", response.getContentType());
        assertEquals(contentLength, response.getContentLength());
        assertEquals("filename=\"logo.png\"", response.getHeader("Content-disposition"));
    }

    public void testStreamResultParse1() throws Exception {
        ///////////////////
        result.setParse(true);
        // ${...} conditionalParse of Result, returns String,
        // which gets evaluated to the stack, that's how it works.
        // We use ${streamForImageAsString} that returns "streamForImage"
        // which is a property that returns an InputStream object.
        result.setInputName("${streamForImageAsString}");
        result.setBufferSize(128);
        result.setContentLength(String.valueOf(contentLength));
        result.setContentDisposition("filename=\"logo.png\"");
        result.setContentType("image/jpeg");

        result.doExecute("helloworld", mai);

        assertEquals(String.valueOf(contentLength), result.getContentLength());
        assertEquals("image/jpeg", result.getContentType());
        assertEquals("${streamForImageAsString}", result.getInputName());
        assertEquals(128, result.getBufferSize());
        assertEquals("filename=\"logo.png\"", result.getContentDisposition());

        assertEquals("image/jpeg", response.getContentType());
        assertEquals(contentLength, response.getContentLength());
        assertEquals("filename=\"logo.png\"", response.getHeader("Content-disposition"));
    }

    public void testStreamResultParse2() throws Exception {
        ///////////////////
        result.setParse(true);
        // This time we dun use ${...}, so streamForImage will
        // be evaluated to the stack, which should reaturn an
        // InputStream object, cause there's such a property in
        // the action object itself.
        result.setInputName("streamForImage");
        result.setBufferSize(128);
        result.setContentLength(String.valueOf(contentLength));
        result.setContentDisposition("filename=\"logo.png\"");
        result.setContentType("image/jpeg");

        result.doExecute("helloworld", mai);

        assertEquals(String.valueOf(contentLength), result.getContentLength());
        assertEquals("image/jpeg", result.getContentType());
        assertEquals("streamForImage", result.getInputName());
        assertEquals(128, result.getBufferSize());
        assertEquals("filename=\"logo.png\"", result.getContentDisposition());

        assertEquals("image/jpeg", response.getContentType());
        assertEquals(contentLength, response.getContentLength());
        assertEquals("filename=\"logo.png\"", response.getHeader("Content-disposition"));
    }

    protected void setUp() throws Exception {
        super.setUp();
        response = new MockHttpServletResponse();

        result = new StreamResult();
        stack = ActionContext.getContext().getValueStack();

        MyImageAction action = new MyImageAction();
        contentLength = (int) action.getContentLength();

        mai = new com.opensymphony.xwork2.mock.MockActionInvocation();
        mai.setAction(action);
        mai.setStack(stack);
        mai.setInvocationContext(ActionContext.getContext());
        stack.push(action);

        ActionContext.getContext().put(ServletActionContext.HTTP_RESPONSE, response);
    }



    protected void tearDown() throws Exception {
        super.tearDown();
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
            FileInputStream fis = new FileInputStream(file);
            return fis;
        }

        public String execute() throws Exception {
            return SUCCESS;
        }

        public long getContentLength() throws Exception {
            URL url = ClassLoaderUtil.getResource("log4j.properties", StreamResultTest.class);
            File file = new File(new URI(url.toString()));
            return file.length();
        }

        public String getStreamForImageAsString() {
            return "streamForImage";
        }

        public String getContentCharSetMethod() {
            return "UTF-8";
        }
    }

}
