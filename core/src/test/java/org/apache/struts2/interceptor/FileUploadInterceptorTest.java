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
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.DefaultLocaleProvider;
import com.opensymphony.xwork2.ValidationAwareSupport;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.TestAction;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.multipart.JakartaMultiPartRequest;
import org.apache.struts2.dispatcher.multipart.StrutsUploadedFile;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.dispatcher.multipart.UploadedFile;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * Test case for FileUploadInterceptor.
 */
public class FileUploadInterceptorTest extends StrutsInternalTestCase {

    public static final UploadedFile EMPTY_FILE = new UploadedFile() {
        @Override
        public Long length() {
            return 0L;
        }

        @Override
        public String getName() {
            return "";
        }

        @Override
        public boolean isFile() {
            return false;
        }

        @Override
        public boolean delete() {
            return false;
        }

        @Override
        public String getAbsolutePath() {
            return null;
        }

        @Override
        public byte[] getContent() {
            return new byte[0];
        }
    };

    private FileUploadInterceptor interceptor;
    private File tempDir;
    private TestAction action;

    public void testAcceptFileWithEmptyAllowedTypesAndExtensions() throws Exception {
        // when allowed type is empty
        ValidationAwareSupport validation = new ValidationAwareSupport();
        boolean ok = interceptor.acceptFile(action, EMPTY_FILE, "filename", "text/plain", "inputName", validation);

        assertTrue(ok);
        assertTrue(validation.getFieldErrors().isEmpty());
        assertFalse(validation.hasErrors());
    }

    public void testAcceptFileWithoutEmptyTypes() throws Exception {
        interceptor.setAllowedTypes("text/plain");

        // when file is of allowed types
        ValidationAwareSupport validation = new ValidationAwareSupport();
        boolean ok = interceptor.acceptFile(action, EMPTY_FILE, "filename.txt", "text/plain", "inputName", validation);

        assertTrue(ok);
        assertTrue(validation.getFieldErrors().isEmpty());
        assertFalse(validation.hasErrors());

        // when file is not of allowed types
        validation = new ValidationAwareSupport();
        boolean notOk = interceptor.acceptFile(action, EMPTY_FILE, "filename.html", "text/html", "inputName", validation);

        assertFalse(notOk);
        assertFalse(validation.getFieldErrors().isEmpty());
        assertTrue(validation.hasErrors());
    }


    public void testAcceptFileWithWildcardContent() throws Exception {
        interceptor.setAllowedTypes("text/*");

        ValidationAwareSupport validation = new ValidationAwareSupport();
        boolean ok = interceptor.acceptFile(action, EMPTY_FILE, "filename.txt", "text/plain", "inputName", validation);

        assertTrue(ok);
        assertTrue(validation.getFieldErrors().isEmpty());
        assertFalse(validation.hasErrors());

        interceptor.setAllowedTypes("text/h*");
        validation = new ValidationAwareSupport();
        boolean notOk = interceptor.acceptFile(action, EMPTY_FILE, "filename.html", "text/plain", "inputName", validation);

        assertFalse(notOk);
        assertFalse(validation.getFieldErrors().isEmpty());
        assertTrue(validation.hasErrors());
    }

    public void testAcceptFileWithoutEmptyExtensions() throws Exception {
        interceptor.setAllowedExtensions(".txt");

        // when file is of allowed extensions
        ValidationAwareSupport validation = new ValidationAwareSupport();
        boolean ok = interceptor.acceptFile(action, EMPTY_FILE, "filename.txt", "text/plain", "inputName", validation);

        assertTrue(ok);
        assertTrue(validation.getFieldErrors().isEmpty());
        assertFalse(validation.hasErrors());

        // when file is not of allowed extensions
        validation = new ValidationAwareSupport();
        boolean notOk = interceptor.acceptFile(action, EMPTY_FILE, "filename.html", "text/html", "inputName", validation);

        assertFalse(notOk);
        assertFalse(validation.getFieldErrors().isEmpty());
        assertTrue(validation.hasErrors());

        //test with multiple extensions
        interceptor.setAllowedExtensions(".txt,.lol");
        validation = new ValidationAwareSupport();
        ok = interceptor.acceptFile(action, EMPTY_FILE, "filename.lol", "text/plain", "inputName", validation);

        assertTrue(ok);
        assertTrue(validation.getFieldErrors().isEmpty());
        assertFalse(validation.hasErrors());
    }

    public void testAcceptFileWithNoFile() throws Exception {
        FileUploadInterceptor interceptor = new FileUploadInterceptor();
        interceptor.setAllowedTypes("text/plain");

        // when file is not of allowed types
        ValidationAwareSupport validation = new ValidationAwareSupport();
        boolean notOk = interceptor.acceptFile(action, null, "filename.html", "text/html", "inputName", validation);

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

        URL url = ClassLoaderUtil.getResource("log4j2.xml", FileUploadInterceptorTest.class);
        File file = new File(new URI(url.toString()));
        assertTrue("log4j2.xml should be in src/test folder", file.exists());
        boolean notOk = interceptor.acceptFile(action, new StrutsUploadedFile(file), "filename", "text/html", "inputName", validation);

        assertFalse(notOk);
        assertFalse(validation.getFieldErrors().isEmpty());
        assertTrue(validation.hasErrors());
        List errors = (List) validation.getFieldErrors().get("inputName");
        assertEquals(1, errors.size());
        String msg = (String) errors.get(0);
        // the error message should contain at least this test
        assertTrue(msg.startsWith("The file is too large to be uploaded"));
        assertTrue(msg.indexOf("inputName") > 0);
        assertTrue(msg.indexOf("log4j2.xml") > 0);
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

        req.setContentType("multipart/form-data"); // not a multipart contentype
        req.setMethod("post");

        MyFileupAction action = container.inject(MyFileupAction.class);
        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());

        ActionContext.getContext().setParameters(HttpParameters.create().build());
        ActionContext.getContext().put(ServletActionContext.HTTP_REQUEST, createMultipartRequest(req, 2000));

        interceptor.intercept(mai);

        assertTrue(action.hasErrors());
    }

    public void testNoContentMultipartRequest() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();

        req.setCharacterEncoding("text/html");
        req.setMethod("post");
        req.addHeader("Content-type", "multipart/form-data");
        req.setContent(null); // there is no content

        MyFileupAction action = container.inject(MyFileupAction.class);
        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());

        ActionContext.getContext().setParameters(HttpParameters.create().build());
        ActionContext.getContext().put(ServletActionContext.HTTP_REQUEST, createMultipartRequest(req, 2000));

        interceptor.intercept(mai);

        assertTrue(action.hasErrors());
    }

    public void testSuccessUploadOfATextFileMultipartRequest() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCharacterEncoding("text/html");
        req.setMethod("post");
        req.addHeader("Content-type", "multipart/form-data; boundary=---1234");

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
        Map<String, Object> param = new HashMap<>();
        ActionContext.getContext().setParameters(HttpParameters.create(param).build());
        ActionContext.getContext().put(ServletActionContext.HTTP_REQUEST, createMultipartRequest(req, 2000));

        interceptor.intercept(mai);

        assertTrue(!action.hasErrors());

        HttpParameters parameters = mai.getInvocationContext().getParameters();
        assertTrue(parameters.keySet().size() == 3);
        UploadedFile[] files = (UploadedFile[]) parameters.get("file").getObject();
        String[] fileContentTypes = parameters.get("fileContentType").getMultipleValues();
        String[] fileRealFilenames = parameters.get("fileFileName").getMultipleValues();

        assertNotNull(files);
        assertNotNull(fileContentTypes);
        assertNotNull(fileRealFilenames);
        assertTrue(files.length == 1);
        assertTrue(fileContentTypes.length == 1);
        assertTrue(fileRealFilenames.length == 1);
        assertEquals("text/html", fileContentTypes[0]);
        assertNotNull("deleteme.txt", fileRealFilenames[0]);
    }

    /**
     * tests whether with multiple files sent with the same name, the ones with forbiddenTypes (see
     * FileUploadInterceptor.setAllowedTypes(...) ) are sorted out.
     *
     * @throws Exception
     */
    public void testMultipleAccept() throws Exception {
        final String htmlContent = "<html><head></head><body>html content</body></html>";
        final String plainContent = "plain content";
        final String bondary = "simple boundary";
        final String endline = "\r\n";

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCharacterEncoding("text/html");
        req.setMethod("POST");
        req.addHeader("Content-type", "multipart/form-data; boundary=" + bondary);
        StringBuilder content = new StringBuilder(128);
        content.append(encodeTextFile(bondary, endline, "file", "test.html", "text/plain", plainContent));
        content.append(encodeTextFile(bondary, endline, "file", "test1.html", "text/html", htmlContent));
        content.append(encodeTextFile(bondary, endline, "file", "test2.html", "text/html", htmlContent));
        content.append(endline);
        content.append(endline);
        content.append(endline);
        content.append("--");
        content.append(bondary);
        content.append("--");
        content.append(endline);
        req.setContent(content.toString().getBytes());

        assertTrue(ServletFileUpload.isMultipartContent(req));

        MyFileupAction action = new MyFileupAction();
        container.inject(action);
        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());
        Map<String, Object> param = new HashMap<String, Object>();
        ActionContext.getContext().setParameters(HttpParameters.create(param).build());
        ActionContext.getContext().put(ServletActionContext.HTTP_REQUEST, createMultipartRequest(req, 2000));

        interceptor.setAllowedTypes("text/html");
        interceptor.intercept(mai);

        HttpParameters parameters = mai.getInvocationContext().getParameters();
        assertEquals(3, parameters.keySet().size());
        UploadedFile[] files = (UploadedFile[]) parameters.get("file").getObject();
        String[] fileContentTypes = parameters.get("fileContentType").getMultipleValues();
        String[] fileRealFilenames = parameters.get("fileFileName").getMultipleValues();

        assertNotNull(files);
        assertNotNull(fileContentTypes);
        assertNotNull(fileRealFilenames);
        assertEquals("files accepted ", 2, files.length);
        assertEquals(2, fileContentTypes.length);
        assertEquals(2, fileRealFilenames.length);
        assertEquals("text/html", fileContentTypes[0]);
        assertNotNull("test1.html", fileRealFilenames[0]);
    }

    public void testMultipartRequestLocalizedError() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCharacterEncoding("text/html");
        req.setMethod("post");
        req.addHeader("Content-type", "multipart/form-data; boundary=---1234");

        // inspired by the unit tests for jakarta commons fileupload
        String content = ("-----1234\r\n" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"deleteme.txt\"\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                "Unit test of FileUploadInterceptor" +
                "\r\n" +
                "-----1234--\r\n");
        req.setContent(content.getBytes("US-ASCII"));

        MyFileupAction action = container.inject(MyFileupAction.class);

        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());
        Map<String, Object> param = new HashMap<>();
        ActionContext.getContext().setParameters(HttpParameters.create(param).build());
        // set German locale
        ActionContext.getContext().setLocale(Locale.GERMAN);
        ActionContext.getContext().put(ServletActionContext.HTTP_REQUEST, createMultipartRequest(req, 10));

        interceptor.intercept(mai);

        assertTrue(action.hasActionErrors());

        Collection<String> errors = action.getActionErrors();
        assertEquals(1, errors.size());
        String msg = errors.iterator().next();
        // the error message should contain at least this test
        assertTrue(msg.startsWith("Der Request übertraf die maximal erlaubte Größe"));
    }

    private String encodeTextFile(String bondary, String endline, String name, String filename, String contentType, String content) {
        final StringBuilder sb = new StringBuilder(64);
        sb.append(endline);
        sb.append("--");
        sb.append(bondary);
        sb.append(endline);
        sb.append("Content-Disposition: form-data; name=\"");
        sb.append(name);
        sb.append("\"; filename=\"");
        sb.append(filename);
        sb.append(endline);
        sb.append("Content-Type: ");
        sb.append(contentType);
        sb.append(endline);
        sb.append(endline);
        sb.append(content);

        return sb.toString();
    }

    private MultiPartRequestWrapper createMultipartRequest(HttpServletRequest req, int maxsize) throws IOException {
        JakartaMultiPartRequest jak = new JakartaMultiPartRequest();
        jak.setMaxSize(String.valueOf(maxsize));
        return new MultiPartRequestWrapper(jak, req, tempDir.getAbsolutePath(), new DefaultLocaleProvider());
    }

    protected void setUp() throws Exception {
        super.setUp();
        action = new TestAction();
        container.inject(action);

        interceptor = new FileUploadInterceptor();
        container.inject(interceptor);
        tempDir = File.createTempFile("struts", "fileupload");
        tempDir.delete();
        tempDir.mkdirs();
    }

    protected void tearDown() throws Exception {
        tempDir.delete();
        interceptor.destroy();
        super.tearDown();
    }

    public static class MyFileupAction extends ActionSupport {

        private static final long serialVersionUID = 6255238895447968889L;

        // no methods
    }


}
