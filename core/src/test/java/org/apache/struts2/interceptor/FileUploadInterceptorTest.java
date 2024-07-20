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
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.action.UploadedFilesAware;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.multipart.JakartaMultiPartRequest;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.dispatcher.multipart.StrutsUploadedFile;
import org.apache.struts2.dispatcher.multipart.UploadedFile;
import org.assertj.core.util.Files;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test case for FileUploadInterceptor.
 */
public class FileUploadInterceptorTest extends StrutsInternalTestCase {

    private static final UploadedFile EMPTY_FILE = new UploadedFile() {
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
        public File getContent() {
            return Files.newTemporaryFile();
        }

        @Override
        public String getOriginalName() {
            return null;
        }

        @Override
        public String getContentType() {
            return null;
        }
    };

    private FileUploadInterceptor interceptor;
    private File tempDir;
    private MockHttpServletRequest request;

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

        URL url = ClassLoaderUtil.getResource("log4j2.xml", FileUploadInterceptorTest.class);
        File file = new File(new URI(url.toString()));
        assertTrue("log4j2.xml should be in src/test folder", file.exists());
        UploadedFile uploadedFile = StrutsUploadedFile.Builder.create(file)
                .withContentType("text/html")
                .withOriginalName("filename")
                .build();

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
        MyFileupAction action = new MyFileupAction();

        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("NoMultipart");
        mai.setInvocationContext(ActionContext.getContext());

        // if no multipart request it will bypass and execute it
        assertEquals("NoMultipart", interceptor.intercept(mai));
    }

    public void testInvalidContentTypeMultipartRequest() throws Exception {
        request.setContentType("multipart/form-data"); // not a multipart contentype
        request.setMethod("post");

        MyFileupAction action = container.inject(MyFileupAction.class);
        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());

        ActionContext.getContext().withParameters(HttpParameters.create().build());
        ActionContext.getContext().withServletRequest(createMultipartRequestMaxSize(2000));

        interceptor.intercept(mai);

        assertTrue(action.hasErrors());
    }

    public void testNoContentMultipartRequest() throws Exception {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        request.setMethod("post");
        request.addHeader("Content-type", "multipart/form-data");
        request.setContent(null); // there is no content

        MyFileupAction action = container.inject(MyFileupAction.class);
        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());

        ActionContext.getContext().withParameters(HttpParameters.create().build());
        ActionContext.getContext().withServletRequest(createMultipartRequestMaxSize(2000));

        interceptor.intercept(mai);

        assertTrue(action.hasErrors());
    }

    public void testSuccessUploadOfATextFileMultipartRequest() throws Exception {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        request.setMethod("post");
        request.addHeader("Content-type", "multipart/form-data; boundary=---1234");

        // inspired by the unit tests for jakarta commons fileupload
        String content = ("-----1234\r\n" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"deleteme.txt\"\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                "Unit test of FileUploadInterceptor" +
                "\r\n" +
                "-----1234--\r\n");
        request.setContent(content.getBytes(StandardCharsets.US_ASCII));

        MyFileupAction action = new MyFileupAction();

        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());
        Map<String, Object> param = new HashMap<>();
        ActionContext.getContext().withParameters(HttpParameters.create(param).build());
        ActionContext.getContext().withServletRequest(createMultipartRequestMaxSize(2000));

        interceptor.intercept(mai);

        assertFalse(action.hasErrors());

        HttpParameters parameters = mai.getInvocationContext().getParameters();
        assertEquals(3, parameters.keySet().size());
        UploadedFile[] files = (UploadedFile[]) parameters.get("file").getObject();
        String[] fileContentTypes = parameters.get("fileContentType").getMultipleValues();
        String[] fileRealFilenames = parameters.get("fileFileName").getMultipleValues();

        assertNotNull(files);
        assertNotNull(fileContentTypes);
        assertNotNull(fileRealFilenames);
        assertEquals(1, files.length);
        assertEquals(1, fileContentTypes.length);
        assertEquals(1, fileRealFilenames.length);
        assertEquals("text/html", fileContentTypes[0]);
        assertNotNull("deleteme.txt", fileRealFilenames[0]);
    }

    /**
     * tests whether with multiple files sent with the same name, the ones with forbiddenTypes (see
     * FileUploadInterceptor.setAllowedTypes(...) ) are sorted out.
     */
    public void testMultipleAccept() throws Exception {
        final String htmlContent = "<html><head></head><body>html content</body></html>";
        final String plainContent = "plain content";
        final String bondary = "simple boundary";
        final String endline = "\r\n";

        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        request.setMethod("POST");
        request.addHeader("Content-type", "multipart/form-data; boundary=" + bondary);
        String content = encodeTextFile("test.html", "text/plain", plainContent) +
                encodeTextFile("test1.html", "text/html", htmlContent) +
                encodeTextFile("test2.html", "text/html", htmlContent) +
                endline +
                endline +
                endline +
                "--" +
                bondary +
                "--" +
                endline;
        request.setContent(content.getBytes());

        assertTrue(JakartaServletFileUpload.isMultipartContent(request));

        MyFileupAction action = new MyFileupAction();
        container.inject(action);
        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());
        Map<String, Object> param = new HashMap<>();
        ActionContext.getContext().withParameters(HttpParameters.create(param).build());
        ActionContext.getContext().withServletRequest(createMultipartRequestMaxSize(2000));

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

    public void testUnacceptedNumberOfFiles() throws Exception {
        final String htmlContent = "<html><head></head><body>html content</body></html>";
        final String plainContent = "plain content";
        final String boundary = "simple boundary";
        final String endline = "\r\n";

        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        request.setMethod("POST");
        request.addHeader("Content-type", "multipart/form-data; boundary=" + boundary);
        String content = encodeTextFile("test.html", "text/plain", plainContent) +
                encodeTextFile("test1.html", "text/html", htmlContent) +
                encodeTextFile("test2.html", "text/html", htmlContent) +
                encodeTextFile("test3.html", "text/html", htmlContent) +
                endline +
                "--" +
                boundary +
                "--" +
                endline;
        request.setContent(content.getBytes());

        assertTrue(JakartaServletFileUpload.isMultipartContent(request));

        MyFileupAction action = new MyFileupAction();
        container.inject(action);
        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());
        Map<String, Object> param = new HashMap<>();
        ActionContext.getContext()
                .withParameters(HttpParameters.create(param).build())
                .withServletRequest(createMultipartRequestMaxFiles());

        interceptor.setAllowedTypes("text/html");
        interceptor.intercept(mai);

        HttpParameters parameters = mai.getInvocationContext().getParameters();
        assertEquals(0, parameters.keySet().size());
        assertEquals(1, action.getActionErrors().size());
        assertEquals(
                "Request exceeded allowed number of files! Permitted number of files is: 3!",
                action.getActionErrors().iterator().next()
        );
    }

    public void testMultipartRequestMaxFileSize() throws Exception {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        request.setMethod("post");
        request.addHeader("Content-type", "multipart/form-data; boundary=---1234");

        // inspired by the unit tests for jakarta commons fileupload
        String content = ("-----1234\r\n" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"deleteme.txt\"\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                "Unit test of FileUploadInterceptor" +
                "\r\n" +
                "-----1234--\r\n");
        request.setContent(content.getBytes(StandardCharsets.US_ASCII));

        MyFileupAction action = container.inject(MyFileupAction.class);

        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());
        Map<String, Object> param = new HashMap<>();
        ActionContext.getContext()
                .withParameters(HttpParameters.create(param).build())
                .withServletRequest(createMultipartRequestMaxFileSize());

        interceptor.intercept(mai);

        assertTrue(action.hasActionErrors());

        Collection<String> errors = action.getActionErrors();
        assertEquals(1, errors.size());
        String msg = errors.iterator().next();
        // FIXME: the expected size is 40 - length of the string
        assertEquals(
                "File deleteme.txt assigned to file exceeded allowed size limit! Max size allowed is: 10 but file was: 11!",
                msg);
    }

    public void testMultipartRequestMaxStringLength() throws Exception {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        request.setMethod("post");
        request.addHeader("Content-type", "multipart/form-data; boundary=---1234");

        // inspired by the unit tests for jakarta commons fileupload
        String content = ("-----1234\r\n" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"deleteme.txt\"\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                "Unit test of FileUploadInterceptor" +
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
        request.setContent(content.getBytes(StandardCharsets.US_ASCII));

        MyFileupAction action = container.inject(MyFileupAction.class);

        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());
        Map<String, Object> param = new HashMap<>();
        ActionContext.getContext()
                .withParameters(HttpParameters.create(param).build())
                .withServletRequest(createMultipartRequestMaxStringLength());

        interceptor.intercept(mai);

        assertTrue(action.hasActionErrors());

        Collection<String> errors = action.getActionErrors();
        assertEquals(1, errors.size());
        String msg = errors.iterator().next();
        assertEquals(
                "The request parameter \"normalFormField2\" was too long. Max length allowed is 20, but found 27!",
                msg);
    }

    public void testMultipartRequestLocalizedError() throws Exception {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        request.setMethod("post");
        request.addHeader("Content-type", "multipart/form-data; boundary=---1234");

        // inspired by the unit tests for jakarta commons fileupload
        String content = ("-----1234\r\n" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"deleteme.txt\"\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                "Unit test of FileUploadInterceptor" +
                "\r\n" +
                "-----1234--\r\n");
        request.setContent(content.getBytes(StandardCharsets.US_ASCII));

        MyFileupAction action = container.inject(MyFileupAction.class);

        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());
        Map<String, Object> param = new HashMap<>();
        ActionContext.getContext()
                .withParameters(HttpParameters.create(param).build())
                .withLocale(Locale.GERMAN)
                .withServletRequest(createMultipartRequestMaxSize(10));

        interceptor.intercept(mai);

        assertTrue(action.hasActionErrors());

        Collection<String> errors = action.getActionErrors();
        assertEquals(1, errors.size());
        String msg = errors.iterator().next();
        // the error message should contain at least this test
        assertTrue(msg.startsWith("Der Request übertraf die maximal erlaubte Größe"));
    }

    public void testSkippingUploadedFileAware() throws Exception {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        request.setMethod("post");
        request.addHeader("Content-type", "multipart/form-data; boundary=---1234");

        // inspired by the unit tests for jakarta commons fileupload
        String content = ("-----1234\r\n" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"deleteme.txt\"\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                "Unit test of FileUploadInterceptor" +
                "\r\n" +
                "-----1234--\r\n");
        request.setContent(content.getBytes(StandardCharsets.US_ASCII));

        MyFileUploadAction action = container.inject(MyFileUploadAction.class);

        MockActionInvocation mai = new MockActionInvocation();
        mai.setInvocationContext(ActionContext.getContext());
        mai.setAction(action);
        MockActionProxy map = new MockActionProxy();
        map.setActionName("uploadedFiles");
        mai.setProxy(map);
        ActionContext.getContext()
                .withParameters(HttpParameters.create(new HashMap<String, Object>()).build())
                .withServletRequest(createMultipartRequestMaxSize(10));

        interceptor.intercept(mai);

        assertFalse(action.hasActionErrors());
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
                "\r\n" +
                "Content-Type: " +
                contentType +
                "\r\n" +
                "\r\n" +
                content;
    }

    private MultiPartRequestWrapper createMultipartRequestMaxFileSize() {
        return createMultipartRequest(-1, 10, -1, -1);
    }

    private MultiPartRequestWrapper createMultipartRequestMaxFiles() {
        return createMultipartRequest(-1, -1, 3, -1);
    }

    private MultiPartRequestWrapper createMultipartRequestMaxSize(int maxsize) {
        return createMultipartRequest(maxsize, -1, -1, -1);
    }

    private MultiPartRequestWrapper createMultipartRequestMaxStringLength() {
        return createMultipartRequest(-1, -1, -1, 20);
    }

    private MultiPartRequestWrapper createMultipartRequest(int maxsize, int maxfilesize, int maxfiles, int maxStringLength) {
        JakartaMultiPartRequest jak = new JakartaMultiPartRequest();
        jak.setMaxSize(String.valueOf(maxsize));
        jak.setMaxFileSize(String.valueOf(maxfilesize));
        jak.setMaxFiles(String.valueOf(maxfiles));
        jak.setMaxStringLength(String.valueOf(maxStringLength));
        jak.setDefaultEncoding(StandardCharsets.UTF_8.name());
        return new MultiPartRequestWrapper(jak, request, tempDir.getAbsolutePath(), new DefaultLocaleProvider());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        request = new MockHttpServletRequest();
        interceptor = new FileUploadInterceptor();
        container.inject(interceptor);
        tempDir = File.createTempFile("struts", "fileupload");
        assertThat(tempDir.delete()).isTrue();
        assertThat(tempDir.mkdirs()).isTrue();
    }

    @Override
    protected void tearDown() throws Exception {
        interceptor.destroy();
        super.tearDown();
    }

    public static class MyFileupAction extends ActionSupport {
    }

    public static class MyFileUploadAction extends ActionSupport implements UploadedFilesAware {
        private List<UploadedFile> uploadedFiles;

        @Override
        public void withUploadedFiles(List<UploadedFile> uploadedFiles) {
            this.uploadedFiles = uploadedFiles;
        }

        public List<UploadedFile> getUploadFiles() {
            return this.uploadedFiles;
        }
    }

}
