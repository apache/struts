/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.interceptor;

import com.opensymphony.util.ClassLoaderUtil;
import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.webwork.WebWorkTestCase;
import com.opensymphony.webwork.dispatcher.multipart.MultiPartRequestWrapper;
import com.opensymphony.xwork.*;
import com.opensymphony.xwork.mock.MockActionInvocation;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * Test case for FileUploadInterceptor.
 *
 * @author tmjee
 * @author Claus Ibsen
 * @version $Date: 2006/03/17 22:46:24 $ $Id: FileUploadInterceptorTest.java,v 1.8 2006/03/17 22:46:24 mrdon Exp $
 */
public class FileUploadInterceptorTest extends WebWorkTestCase {

    private FileUploadInterceptor interceptor;

    public void testAcceptFileWithEmptyAllowedTypes() throws Exception {
        // when allowed type is empty
        ValidationAwareSupport validation = new ValidationAwareSupport();
        boolean ok = interceptor.acceptFile(new File(""), "text/plain", "inputName", validation, Locale.getDefault());

        assertTrue(ok);
        assertTrue(validation.getFieldErrors().isEmpty());
        assertFalse(validation.hasErrors());
    }

    public void testAcceptFileWithoutEmptyTypes() throws Exception {
        interceptor.setAllowedTypes("text/plain");

        // when file is of allowed types
        ValidationAwareSupport validation = new ValidationAwareSupport();
        boolean ok = interceptor.acceptFile(new File(""), "text/plain", "inputName", validation, Locale.getDefault());

        assertTrue(ok);
        assertTrue(validation.getFieldErrors().isEmpty());
        assertFalse(validation.hasErrors());

        // when file is not of allowed types
        validation = new ValidationAwareSupport();
        boolean notOk = interceptor.acceptFile(new File(""), "text/html", "inputName", validation, Locale.getDefault());

        assertFalse(notOk);
        assertFalse(validation.getFieldErrors().isEmpty());
        assertTrue(validation.hasErrors());
    }

    public void testAcceptFileWithNoFile() throws Exception {
        FileUploadInterceptor interceptor = new FileUploadInterceptor();
        interceptor.setAllowedTypes("text/plain");

        // when file is not of allowed types
        ValidationAwareSupport validation = new ValidationAwareSupport();
        boolean notOk = interceptor.acceptFile(null, "text/html", "inputName", validation, Locale.getDefault());

        assertFalse(notOk);
        assertFalse(validation.getFieldErrors().isEmpty());
        assertTrue(validation.hasErrors());
        List errors = (List) validation.getFieldErrors().get("inputName");
        assertEquals(1, errors.size());
        String msg = (String) errors.get(0);
        assertTrue(msg.startsWith("Error uploading:"));
        assertTrue(msg.indexOf("inputName") > 0);
    }

    public void testAcceptFileWithMaxSize() throws Exception {
        interceptor.setAllowedTypes("text/plain");
        interceptor.setMaximumSize(new Long(10));

        // when file is not of allowed types
        ValidationAwareSupport validation = new ValidationAwareSupport();

        URL url = ClassLoaderUtil.getResource("log4j.properties", FileUploadInterceptorTest.class);
        File file = new File(new URI(url.toString()));
        assertTrue("log4j.properties should be in src/test folder", file.exists());
        boolean notOk = interceptor.acceptFile(file, "text/html", "inputName", validation, Locale.getDefault());

        assertFalse(notOk);
        assertFalse(validation.getFieldErrors().isEmpty());
        assertTrue(validation.hasErrors());
        List errors = (List) validation.getFieldErrors().get("inputName");
        assertEquals(1, errors.size());
        String msg = (String) errors.get(0);
        // the error message shoul contain at least this test
        assertTrue(msg.startsWith("The file is to large to be uploaded"));
        assertTrue(msg.indexOf("inputName") > 0);
        assertTrue(msg.indexOf("log4j.properties") > 0);
    }

    public void testNoMultipartRequest() throws Exception {
        MyFileupAction action = new MyFileupAction();

        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("NoMultipart");
        mai.setInvocationContext(ActionContext.getContext());

        // if no multipart request it will bypass and execute it
        assertEquals("NoMultipart", interceptor.intercept(mai));
    }

    public void testInvalidContentTypeMultipartRequest() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();

        req.setCharacterEncoding("text/html");
        req.setContentType("text/xml"); // not a multipart contentype
        req.addHeader("Content-type", "multipart/form-data");

        MyFileupAction action = new MyFileupAction();
        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());

        Map param = new HashMap();
        ActionContext.getContext().setParameters(param);
        ActionContext.getContext().put(ServletActionContext.HTTP_REQUEST, createMultipartRequest((HttpServletRequest) req, 2000));

        interceptor.intercept(mai);

        assertTrue(action.hasErrors());
    }

    public void testNoContentMultipartRequest() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();

        req.setCharacterEncoding("text/html");
        req.setContentType("multipart/form-data; boundary=---1234");
        req.setContent(null); // there is no content

        MyFileupAction action = new MyFileupAction();
        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());

        Map param = new HashMap();
        ActionContext.getContext().setParameters(param);
        ActionContext.getContext().put(ServletActionContext.HTTP_REQUEST, createMultipartRequest((HttpServletRequest) req, 2000));

        interceptor.intercept(mai);

        assertTrue(action.hasErrors());
    }

    public void testSuccessUploadOfATextFileMultipartRequest() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCharacterEncoding("text/html");
        req.setContentType("multipart/form-data; boundary=---1234");
        req.addHeader("Content-type", "multipart/form-data");

        // inspired by the unit tests for jakarta commons fileupload
        String content = ("-----1234\r\n" +
            "Content-Disposition: form-data; name=\"file\"; filename=\"deleteme.txt\"\r\n" +
            "Content-Type: text/html\r\n" +
            "\r\n" +
            "Unit test of FileUploadInterceptor" +
            "\r\n" +
            "-----1234--\r\n");
        req.setContent(content.getBytes("US-ASCII"));

        MyFileupAction action = new MyFileupAction();

        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());
        Map param = new HashMap();
        ActionContext.getContext().setParameters(param);
        ActionContext.getContext().put(ServletActionContext.HTTP_REQUEST, createMultipartRequest((HttpServletRequest) req, 2000));

        interceptor.intercept(mai);

        assertTrue(! action.hasErrors());

        assertTrue(param.size() == 3);
        File[] files = (File[]) param.get("file");
        String[] fileContentTypes = (String[]) param.get("fileContentType");
        String[] fileRealFilenames = (String[]) param.get("fileFileName");

        assertNotNull(files);
        assertNotNull(fileContentTypes);
        assertNotNull(fileRealFilenames);
        assertTrue(files.length == 1);
        assertTrue(fileContentTypes.length == 1);
        assertTrue(fileRealFilenames.length == 1);
        assertEquals("text/html", fileContentTypes[0]);
        assertNotNull("deleteme.txt", fileRealFilenames[0]);
    }

    private MultiPartRequestWrapper createMultipartRequest(HttpServletRequest req, int maxsize) throws IOException {
       return new MultiPartRequestWrapper(req, "build" + File.separator + "test", maxsize);
    }

    protected void setUp() throws Exception {
        super.setUp();
        interceptor = new FileUploadInterceptor();
    }

    protected void tearDown() throws Exception {
        interceptor.destroy();
        super.tearDown();
    }

    private class MyFileupAction extends ActionSupport {
        // no methods
    }


}
