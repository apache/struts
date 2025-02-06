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
import com.opensymphony.xwork2.mock.MockActionProxy;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.action.UploadedFilesAware;
import org.apache.struts2.dispatcher.multipart.JakartaMultiPartRequest;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.dispatcher.multipart.StrutsUploadedFile;
import org.apache.struts2.dispatcher.multipart.UploadedFile;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test case for {@link ActionFileUploadInterceptor}.
 */
public class ActionFileUploadInterceptorTest extends StrutsInternalTestCase {

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

        @Override
        public String getOriginalName() {
            return null;
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public String getInputName() {
            return null;
        }
    };

    private ActionFileUploadInterceptor interceptor;
    private File tempDir;

    private final String htmlContent = "<html><head></head><body>html content</body></html>";
    private final String plainContent = "plain content";
    private final String boundary = "simple boundary";
    private final String endline = "\r\n";

    public void testAcceptFileWithEmptyAllowedTypesAndExtensions() {
        // when allowed type is empty
        ValidationAwareSupport validation = new ValidationAwareSupport();
        boolean ok = interceptor.acceptFile(validation, EMPTY_FILE, "filename", "text/plain", "inputName");

        assertTrue(ok);
        assertTrue(validation.getFieldErrors().isEmpty());
        assertFalse(validation.hasErrors());
    }

    public void testAcceptFileWithoutEmptyTypes() {
        interceptor.setAllowedTypes("text/plain");

        // when file is of allowed types
        ValidationAwareSupport validation = new ValidationAwareSupport();
        boolean ok = interceptor.acceptFile(validation, EMPTY_FILE, "filename.txt", "text/plain", "inputName");

        assertTrue(ok);
        assertTrue(validation.getFieldErrors().isEmpty());
        assertFalse(validation.hasErrors());

        // when file is not of allowed types
        validation = new ValidationAwareSupport();
        boolean notOk = interceptor.acceptFile(validation, EMPTY_FILE, "filename.html", "text/html", "inputName");

        assertFalse(notOk);
        assertFalse(validation.getFieldErrors().isEmpty());
        assertTrue(validation.hasErrors());
    }


    public void testAcceptFileWithWildcardContent() {
        interceptor.setAllowedTypes("text/*");

        ValidationAwareSupport validation = new ValidationAwareSupport();
        boolean ok = interceptor.acceptFile(validation, EMPTY_FILE, "filename.txt", "text/plain", "inputName");

        assertTrue(ok);
        assertTrue(validation.getFieldErrors().isEmpty());
        assertFalse(validation.hasErrors());

        interceptor.setAllowedTypes("text/h*");
        validation = new ValidationAwareSupport();
        boolean notOk = interceptor.acceptFile(validation, EMPTY_FILE, "filename.html", "text/plain", "inputName");

        assertFalse(notOk);
        assertFalse(validation.getFieldErrors().isEmpty());
        assertTrue(validation.hasErrors());
    }

    public void testAcceptFileWithoutEmptyExtensions() {
        interceptor.setAllowedExtensions(".txt");

        // when file is of allowed extensions
        ValidationAwareSupport validation = new ValidationAwareSupport();
        boolean ok = interceptor.acceptFile(validation, EMPTY_FILE, "filename.txt", "text/plain", "inputName");

        assertTrue(ok);
        assertTrue(validation.getFieldErrors().isEmpty());
        assertFalse(validation.hasErrors());

        // when file is not of allowed extensions
        validation = new ValidationAwareSupport();
        boolean notOk = interceptor.acceptFile(validation, EMPTY_FILE, "filename.html", "text/html", "inputName");

        assertFalse(notOk);
        assertFalse(validation.getFieldErrors().isEmpty());
        assertTrue(validation.hasErrors());

        //test with multiple extensions
        interceptor.setAllowedExtensions(".txt,.lol");
        validation = new ValidationAwareSupport();
        ok = interceptor.acceptFile(validation, EMPTY_FILE, "filename.lol", "text/plain", "inputName");

        assertTrue(ok);
        assertTrue(validation.getFieldErrors().isEmpty());
        assertFalse(validation.hasErrors());
    }

    public void testAcceptFileWithNoFile() {
        ActionFileUploadInterceptor interceptor = new ActionFileUploadInterceptor();
        interceptor.setContainer(container);

        interceptor.setAllowedTypes("text/plain");

        // when file is not of allowed types
        ValidationAwareSupport validation = new ValidationAwareSupport();
        boolean notOk = interceptor.acceptFile(validation, null, "filename.html", "text/html", "inputName");

        assertFalse(notOk);
        assertFalse(validation.getFieldErrors().isEmpty());
        assertTrue(validation.hasErrors());
        List<String> errors = validation.getFieldErrors().get("inputName");
        assertEquals(1, errors.size());
        String msg = errors.get(0);
        assertTrue(msg.startsWith("Error uploading:"));
        assertTrue(msg.indexOf("inputName") > 0);
    }

    public void testAcceptFileWithMaxSize() throws Exception {
        interceptor.setMaximumSize(10L);

        // when file is not of allowed types
        ValidationAwareSupport validation = new ValidationAwareSupport();

        URL url = ClassLoaderUtil.getResource("log4j2.xml", ActionFileUploadInterceptorTest.class);
        File file = new File(new URI(url.toString()));
        assertTrue("log4j2.xml should be in src/test folder", file.exists());
        UploadedFile uploadedFile = StrutsUploadedFile.Builder.create(file).withContentType("text/html").withOriginalName("filename").build();
        boolean notOk = interceptor.acceptFile(validation, uploadedFile, "filename", "text/html", "inputName");

        assertFalse(notOk);
        assertFalse(validation.getFieldErrors().isEmpty());
        assertTrue(validation.hasErrors());
        List<String> errors = validation.getFieldErrors().get("inputName");
        assertEquals(1, errors.size());
        String msg = errors.get(0);
        // the error message should contain at least this test
        assertThat(msg).contains(
            "The file is too large to be uploaded",
            "inputName",
            "log4j2.xml",
            "allowed mx size is 10"
        );
    }

    public void testNoMultipartRequest() throws Exception {
        MyFileUploadAction action = new MyFileUploadAction();

        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("NoMultipart");
        MockActionProxy proxy = new MockActionProxy();
        proxy.setNamespace("/test");
        proxy.setActionName("myFileUpload");
        mai.setProxy(proxy);
        mai.setInvocationContext(ActionContext.getContext());

        // if no multipart request it will bypass and execute it
        assertEquals("NoMultipart", interceptor.intercept(mai));
    }

    public void testInvalidContentTypeMultipartRequest() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();

        req.setContentType("multipart/form-data"); // not a multipart contentype
        req.setMethod("post");

        MyFileUploadAction action = container.inject(MyFileUploadAction.class);
        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());

        ActionContext.getContext().put(ServletActionContext.HTTP_REQUEST, createMultipartRequestMaxSize(req, 2000));

        interceptor.intercept(mai);

        assertTrue(action.hasErrors());
    }

    public void testNoContentMultipartRequest() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();

        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        req.setMethod("post");
        req.addHeader("Content-type", "multipart/form-data");
        req.setContent(null); // there is no content

        MyFileUploadAction action = container.inject(MyFileUploadAction.class);
        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());

        ActionContext.getContext().put(ServletActionContext.HTTP_REQUEST, createMultipartRequestMaxSize(req, 2000));

        interceptor.intercept(mai);

        assertTrue(action.hasErrors());
    }

    public void testSuccessUploadOfATextFileMultipartRequest() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        req.setMethod("post");
        req.addHeader("Content-type", "multipart/form-data; boundary=---1234");

        // inspired by the unit tests for jakarta commons fileupload
        String content = ("-----1234\r\n" +
            "Content-Disposition: form-data; name=\"file\"; filename=\"deleteme.txt\"\r\n" +
            "Content-Type: text/html\r\n" +
            "\r\n" +
            "Unit test of ActionFileUploadInterceptor" +
            "\r\n" +
            "-----1234--\r\n");
        req.setContent(content.getBytes(StandardCharsets.US_ASCII));

        MyFileUploadAction action = new MyFileUploadAction();

        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());
        ActionContext.getContext().put(ServletActionContext.HTTP_REQUEST, createMultipartRequestMaxSize(req, 2000));

        interceptor.intercept(mai);

        assertFalse(action.hasErrors());

        List<UploadedFile> files = action.getUploadFiles();

        assertNotNull(files);
        assertEquals(1, files.size());
        assertEquals("text/html", files.get(0).getContentType());
        assertNotNull("deleteme.txt", files.get(0).getOriginalName());
    }

    public void testSuccessUploadOfATextFileMultipartRequestNoMaxParamsSet() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        req.setMethod("post");
        req.addHeader("Content-type", "multipart/form-data; boundary=---1234");

        // inspired by the unit tests for jakarta commons fileupload
        String content = ("-----1234\r\n" +
            "Content-Disposition: form-data; name=\"file\"; filename=\"deleteme.txt\"\r\n" +
            "Content-Type: text/html\r\n" +
            "\r\n" +
            "Unit test of ActionFileUploadInterceptor" +
            "\r\n" +
            "-----1234--\r\n");
        req.setContent(content.getBytes(StandardCharsets.US_ASCII));

        MyFileUploadAction action = new MyFileUploadAction();

        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());
        ActionContext.getContext().withServletRequest(createMultipartRequestNoMaxParamsSet(req));

        interceptor.intercept(mai);

        assertFalse(action.hasErrors());

        List<UploadedFile> files = action.getUploadFiles();

        assertNotNull(files);
        assertEquals(1, files.size());
        assertEquals("text/html", files.get(0).getContentType());
        assertNotNull("deleteme.txt", files.get(0).getOriginalName());
    }

    public void testSuccessUploadOfATextFileMultipartRequestWithNormalFieldsMaxParamsSet() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        req.setMethod("post");
        req.addHeader("Content-type", "multipart/form-data; boundary=---1234");

        // inspired by the unit tests for jakarta commons fileupload
        String content = ("-----1234\r\n" +
            "Content-Disposition: form-data; name=\"file\"; filename=\"deleteme.txt\"\r\n" +
            "Content-Type: text/html\r\n" +
            "\r\n" +
            "Unit test of ActionFileUploadInterceptor" +
            "\r\n" +
            "-----1234\r\n" +
            "Content-Disposition: form-data; name=\"normalFormField1\"\r\n" +
            "\r\n" +
            "normal field 1" +
            "\r\n" +
            "-----1234\r\n" +
            "Content-Disposition: form-data; name=\"normalFormField2\"\r\n" +
            "\r\n" +
            "normal field 2" +
            "\r\n" +
            "-----1234--\r\n");
        req.setContent(content.getBytes(StandardCharsets.US_ASCII));

        MyFileUploadAction action = new MyFileUploadAction();

        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());
        ActionContext.getContext().withServletRequest(createMultipartRequest(req, 2000, 2000, 5, 100));

        interceptor.intercept(mai);

        assertFalse(action.hasErrors());

        List<UploadedFile> files = action.getUploadFiles();

        assertNotNull(files);
        assertEquals(1, files.size());
        assertEquals("text/html", files.get(0).getContentType());
        assertNotNull("deleteme.txt", files.get(0).getOriginalName());

        // Confirm normalFormField1, normalFormField2 were processed by the MultiPartRequestWrapper.
        HttpServletRequest invocationServletRequest = mai.getInvocationContext().getServletRequest();
        assertTrue("invocation servelt request is not a MultiPartRequestWrapper ?", invocationServletRequest instanceof MultiPartRequestWrapper);
        MultiPartRequestWrapper multipartRequestWrapper = (MultiPartRequestWrapper) invocationServletRequest;
        assertNotNull("normalFormField1 missing from MultiPartRequestWrapper parameters ?", multipartRequestWrapper.getParameter("normalFormField1"));
        assertNotNull("normalFormField2 missing from MultiPartRequestWrapper parameters ?", multipartRequestWrapper.getParameter("normalFormField2"));
    }

    public void testSuccessUploadOfATextFileMultipartRequestWithNormalFieldsNoMaxParamsSet() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        req.setMethod("post");
        req.addHeader("Content-type", "multipart/form-data; boundary=---1234");

        // inspired by the unit tests for jakarta commons fileupload
        String content = ("-----1234\r\n" +
            "Content-Disposition: form-data; name=\"file\"; filename=\"deleteme.txt\"\r\n" +
            "Content-Type: text/html\r\n" +
            "\r\n" +
            "Unit test of ActionFileUploadInterceptor" +
            "\r\n" +
            "-----1234\r\n" +
            "Content-Disposition: form-data; name=\"normalFormField1\"\r\n" +
            "\r\n" +
            "normal field 1" +
            "\r\n" +
            "-----1234\r\n" +
            "Content-Disposition: form-data; name=\"normalFormField2\"\r\n" +
            "\r\n" +
            "normal field 2" +
            "\r\n" +
            "-----1234--\r\n");
        req.setContent(content.getBytes(StandardCharsets.US_ASCII));

        MyFileUploadAction action = new MyFileUploadAction();

        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());
        ActionContext.getContext().withServletRequest(createMultipartRequestNoMaxParamsSet(req));

        interceptor.intercept(mai);

        assertFalse(action.hasErrors());

        List<UploadedFile> files = action.getUploadFiles();

        assertNotNull(files);
        assertEquals(1, files.size());
        assertEquals("text/html", files.get(0).getContentType());
        assertNotNull("deleteme.txt", files.get(0).getOriginalName());

        // Confirm normalFormField1, normalFormField2 were processed by the MultiPartRequestWrapper.
        HttpServletRequest invocationServletRequest = mai.getInvocationContext().getServletRequest();
        assertTrue("invocation servelt request is not a MultiPartRequestWrapper ?", invocationServletRequest instanceof MultiPartRequestWrapper);
        MultiPartRequestWrapper multipartRequestWrapper = (MultiPartRequestWrapper) invocationServletRequest;
        assertNotNull("normalFormField1 missing from MultiPartRequestWrapper parameters ?", multipartRequestWrapper.getParameter("normalFormField1"));
        assertNotNull("normalFormField2 missing from MultiPartRequestWrapper parameters ?", multipartRequestWrapper.getParameter("normalFormField2"));
    }

    /**
     * tests whether with multiple files sent with the same name, the ones with forbiddenTypes (see
     * ActionFileUploadInterceptor.setAllowedTypes(...) ) are sorted out.
     */
    public void testMultipleAccept() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        req.setMethod("POST");
        req.addHeader("Content-type", "multipart/form-data; boundary=" + boundary);
        String content = encodeTextFile("test.html", "text/plain", plainContent) +
            encodeTextFile("test1.html", "text/html", htmlContent) +
            encodeTextFile("test2.html", "text/html", htmlContent) +
            endline +
            endline +
            endline +
            "--" +
            boundary +
            "--" +
            endline;
        req.setContent(content.getBytes());

        assertTrue(ServletFileUpload.isMultipartContent(req));

        MyFileUploadAction action = new MyFileUploadAction();
        container.inject(action);
        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());
        ActionContext.getContext().put(ServletActionContext.HTTP_REQUEST, createMultipartRequestMaxSize(req, 2000));

        interceptor.setAllowedTypes("text/html");
        interceptor.intercept(mai);

        List<UploadedFile> files = action.getUploadFiles();

        assertNotNull(files);
        assertEquals("files accepted ", 2, files.size());
        assertEquals("text/html", files.get(0).getContentType());
        assertNotNull("test1.html", files.get(0).getOriginalName());
    }

    public void testUnacceptedNumberOfFiles() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        req.setMethod("POST");
        req.addHeader("Content-type", "multipart/form-data; boundary=" + boundary);
        String content = encodeTextFile("test.html", "text/plain", plainContent) +
            encodeTextFile("test1.html", "text/html", htmlContent) +
            encodeTextFile("test2.html", "text/html", htmlContent) +
            encodeTextFile("test3.html", "text/html", htmlContent) +
            endline +
            "--" +
            boundary +
            "--" +
            endline;
        req.setContent(content.getBytes());

        assertTrue(ServletFileUpload.isMultipartContent(req));

        MyFileUploadAction action = new MyFileUploadAction();
        container.inject(action);
        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());
        ActionContext.getContext().withServletRequest(createMultipartRequestMaxFiles(req));

        interceptor.setAllowedTypes("text/html");
        interceptor.intercept(mai);

        assertNull(action.getUploadFiles());
        assertEquals(1, action.getActionErrors().size());
        assertEquals("Request exceeded allowed number of files! Max allowed files number is: 3!", action.getActionErrors().iterator().next());
    }

    public void testMultipartRequestMaxFileSize() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        req.setMethod("post");
        req.addHeader("Content-type", "multipart/form-data; boundary=---1234");

        // inspired by the unit tests for jakarta commons fileupload
        String content = ("-----1234\r\n" +
            "Content-Disposition: form-data; name=\"file\"; filename=\"deleteme.txt\"\r\n" +
            "Content-Type: text/html\r\n" +
            "\r\n" +
            "Unit test of ActionFileUploadInterceptor" +
            "\r\n" +
            "-----1234--\r\n");
        req.setContent(content.getBytes(StandardCharsets.US_ASCII));

        MyFileUploadAction action = container.inject(MyFileUploadAction.class);

        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());
        ActionContext.getContext()
            .withServletRequest(createMultipartRequestMaxFileSize(req));

        interceptor.intercept(mai);

        assertTrue(action.hasActionErrors());

        Collection<String> errors = action.getActionErrors();
        assertEquals(1, errors.size());
        String msg = errors.iterator().next();
        assertEquals(
            "File in request exceeded allowed file size limit! Max file size allowed is: 10 but file deleteme.txt was: 40!",
            msg);
    }

    public void testMultipartRequestMaxStringLength() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        req.setMethod("post");
        req.addHeader("Content-type", "multipart/form-data; boundary=---1234");

        // inspired by the unit tests for jakarta commons fileupload
        String content = ("-----1234\r\n" +
            "Content-Disposition: form-data; name=\"file\"; filename=\"deleteme.txt\"\r\n" +
            "Content-Type: text/html\r\n" +
            "\r\n" +
            "Unit test of ActionFileUploadInterceptor" +
            "\r\n" +
            "-----1234\r\n" +
            "Content-Disposition: form-data; name=\"normalFormField1\"\r\n" +
            "\r\n" +
            "it works" +
            "\r\n" +
            "-----1234\r\n" +
            "Content-Disposition: form-data; name=\"normalFormField2\"\r\n" +
            "\r\n" +
            "long string should not work" +
            "\r\n" +
            "-----1234--\r\n");
        req.setContent(content.getBytes(StandardCharsets.US_ASCII));

        MyFileUploadAction action = container.inject(MyFileUploadAction.class);

        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());
        ActionContext.getContext()
            .withServletRequest(createMultipartRequestMaxStringLength(req));

        interceptor.intercept(mai);

        assertTrue(action.hasActionErrors());

        Collection<String> errors = action.getActionErrors();
        assertEquals(1, errors.size());
        String msg = errors.iterator().next();
        assertEquals(
            "The request parameter \"normalFormField2\" was too long.  Max length allowed is 20, but found 27!",
            msg);
    }

    public void testMultipartRequestLocalizedError() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        req.setMethod("post");
        req.addHeader("Content-type", "multipart/form-data; boundary=---1234");

        // inspired by the unit tests for jakarta commons fileupload
        String content = ("-----1234\r\n" +
            "Content-Disposition: form-data; name=\"file\"; filename=\"deleteme.txt\"\r\n" +
            "Content-Type: text/html\r\n" +
            "\r\n" +
            "Unit test of ActionFileUploadInterceptor" +
            "\r\n" +
            "-----1234--\r\n");
        req.setContent(content.getBytes(StandardCharsets.US_ASCII));

        MyFileUploadAction action = container.inject(MyFileUploadAction.class);

        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());
        ActionContext.getContext()
            .withLocale(Locale.GERMAN)
            .withServletRequest(createMultipartRequestMaxSize(req, 10));

        interceptor.intercept(mai);

        assertTrue(action.hasActionErrors());

        Collection<String> errors = action.getActionErrors();
        assertEquals(1, errors.size());
        String msg = errors.iterator().next();
        // the error message should contain at least this test
        assertTrue(msg.startsWith("Der Request übertraf die maximal erlaubte Größe"));
    }

    public void testUnacceptedFieldName() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        req.setMethod("post");
        req.addHeader("Content-type", "multipart/form-data; boundary=---1234");

        // inspired by the unit tests for jakarta commons fileupload
        String content = ("-----1234\r\n" +
            "Content-Disposition: form-data; name=\"top.file\"; filename=\"deleteme.txt\"\r\n" +
            "Content-Type: text/html\r\n" +
            "\r\n" +
            "Unit test of ActionFileUploadInterceptor" +
            "\r\n" +
            "-----1234--\r\n");
        req.setContent(content.getBytes(StandardCharsets.US_ASCII));

        MyFileUploadAction action = container.inject(MyFileUploadAction.class);

        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());
        ActionContext.getContext()
            .withServletRequest(createMultipartRequestMaxSize(req, 2000));

        interceptor.intercept(mai);

        assertThat(action.getActionErrors())
                .containsExactly("The multipart upload field name \"top.file\" contains illegal characters!");
        assertNull(action.getUploadFiles());
    }

    public void testUnacceptedFileName() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        req.setMethod("post");
        req.addHeader("Content-type", "multipart/form-data; boundary=---1234");

        // inspired by the unit tests for jakarta commons fileupload
        String content = ("-----1234\r\n" +
            "Content-Disposition: form-data; name=\"file\"; filename=\"../deleteme.txt\"\r\n" +
            "Content-Type: text/html\r\n" +
            "\r\n" +
            "Unit test of ActionFileUploadInterceptor" +
            "\r\n" +
            "-----1234--\r\n");
        req.setContent(content.getBytes(StandardCharsets.US_ASCII));

        MyFileUploadAction action = container.inject(MyFileUploadAction.class);

        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());
        ActionContext.getContext()
            .withServletRequest(createMultipartRequestMaxSize(req, 2000));

        interceptor.intercept(mai);

        assertThat(action.getActionErrors())
                .containsExactly("The multipart upload filename \"../deleteme.txt\" contains illegal characters!");
        assertNull(action.getUploadFiles());
    }

    private String encodeTextFile(String filename, String contentType, String content) {
        return "\r\n" +
            "--" +
            "simple boundary" +
            "\r\n" +
            "Content-Disposition: form-data; name=\"" +
            "file" +
            "\"; filename=\"" +
            filename +
            "\"\r\n" +
            "Content-Type: " +
            contentType +
            "\r\n" +
            "\r\n" +
            content;
    }

    private MultiPartRequestWrapper createMultipartRequestMaxFileSize(HttpServletRequest req) {
        return createMultipartRequest(req, -1, 10, -1, -1);
    }

    private MultiPartRequestWrapper createMultipartRequestMaxFiles(HttpServletRequest req) {
        return createMultipartRequest(req, -1, -1, 3, -1);
    }

    private MultiPartRequestWrapper createMultipartRequestMaxSize(HttpServletRequest req, int maxsize) {
        return createMultipartRequest(req, maxsize, -1, -1, -1);
    }

    private MultiPartRequestWrapper createMultipartRequestMaxStringLength(HttpServletRequest req) {
        return createMultipartRequest(req, -1, -1, -1, 20);
    }

    private MultiPartRequestWrapper createMultipartRequest(HttpServletRequest req, int maxsize, int maxfilesize, int maxfiles, int maxStringLength) {
        JakartaMultiPartRequest jak = new JakartaMultiPartRequest();
        jak.setMaxSize(String.valueOf(maxsize));
        jak.setMaxFileSize(String.valueOf(maxfilesize));
        jak.setMaxFiles(String.valueOf(maxfiles));
        jak.setMaxStringLength(String.valueOf(maxStringLength));
        return new MultiPartRequestWrapper(jak, req, tempDir.getAbsolutePath(), new DefaultLocaleProvider());
    }

    private MultiPartRequestWrapper createMultipartRequestNoMaxParamsSet(HttpServletRequest req) {
        JakartaMultiPartRequest jak = new JakartaMultiPartRequest();
        return new MultiPartRequestWrapper(jak, req, tempDir.getAbsolutePath(), new DefaultLocaleProvider());
    }

    protected void setUp() throws Exception {
        super.setUp();

        interceptor = new ActionFileUploadInterceptor();
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

    public static class MyFileUploadAction extends ActionSupport implements UploadedFilesAware {
        private List<UploadedFile> uploadedFiles;

        // Note: We do not currently need fields/getters/setters for normalFormField1, normalFormField2 since
        //       the upload interceptor only prepares the normal field parameters.

        @Override
        public void withUploadedFiles(List<UploadedFile> uploadedFiles) {
            this.uploadedFiles = uploadedFiles;
        }

        public List<UploadedFile> getUploadFiles() {
            return this.uploadedFiles;
        }
    }

}
