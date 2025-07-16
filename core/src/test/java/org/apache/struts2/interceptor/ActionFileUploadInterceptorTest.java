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

import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletDiskFileUpload;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;
import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionSupport;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.ValidationAwareSupport;
import org.apache.struts2.action.UploadedFilesAware;
import org.apache.struts2.dispatcher.multipart.JakartaMultiPartRequest;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.dispatcher.multipart.StrutsUploadedFile;
import org.apache.struts2.dispatcher.multipart.UploadedFile;
import org.apache.struts2.locale.DefaultLocaleProvider;
import org.apache.struts2.mock.MockActionInvocation;
import org.apache.struts2.mock.MockActionProxy;
import org.apache.struts2.util.ClassLoaderUtil;
import org.assertj.core.util.Files;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test case for {@link ActionFileUploadInterceptor}.
 */
public class ActionFileUploadInterceptorTest extends StrutsInternalTestCase {

    private MockHttpServletRequest request;
    private ActionFileUploadInterceptor interceptor;
    private File tempDir;

    private final String htmlContent = "<html><head></head><body>html content</body></html>";
    private final String plainContent = "plain content";
    private final String boundary = "simple boundary";
    private final String endLine = "\r\n";

    public void testAcceptFileWithEmptyAllowedTypesAndExtensions() {
        // when allowed type is empty
        ValidationAwareSupport validation = new ValidationAwareSupport();
        boolean ok = interceptor.acceptFile(validation, createTestFile(Files.newTemporaryFile()), "filename", "text/plain", "inputName");

        assertThat(ok).isTrue();
        assertThat(validation.getFieldErrors()).isEmpty();
        assertThat(validation.hasErrors()).isFalse();
    }

    public void testAcceptFileWithoutEmptyTypes() {
        interceptor.setAllowedTypes("text/plain");

        // when file is of allowed types
        ValidationAwareSupport validation = new ValidationAwareSupport();
        boolean ok = interceptor.acceptFile(validation, createTestFile(Files.newTemporaryFile()), "filename.txt", "text/plain", "inputName");

        assertThat(ok).isTrue();
        assertThat(validation.getFieldErrors()).isEmpty();
        assertThat(validation.hasErrors()).isFalse();

        // when file is not of allowed types
        validation = new ValidationAwareSupport();
        boolean notOk = interceptor.acceptFile(validation, createTestFile(Files.newTemporaryFile()), "filename.html", "text/html", "inputName");

        assertThat(notOk).isFalse();
        assertThat(validation.getFieldErrors()).isNotEmpty();
        assertThat(validation.hasErrors()).isTrue();
    }

    public void testAcceptFileWithWildcardContent() {
        interceptor.setAllowedTypes("text/*");

        ValidationAwareSupport validation = new ValidationAwareSupport();
        boolean ok = interceptor.acceptFile(validation, createTestFile(Files.newTemporaryFile()), "filename.txt", "text/plain", "inputName");

        assertThat(ok).isTrue();
        assertThat(validation.getFieldErrors()).isEmpty();
        assertThat(validation.hasErrors()).isFalse();

        interceptor.setAllowedTypes("text/h*");
        validation = new ValidationAwareSupport();
        boolean notOk = interceptor.acceptFile(validation, createTestFile(Files.newTemporaryFile()), "filename.html", "text/plain", "inputName");

        assertThat(notOk).isFalse();
        assertThat(validation.getFieldErrors()).isNotEmpty();
        assertThat(validation.hasErrors()).isTrue();
    }

    public void testAcceptFileWithoutEmptyExtensions() {
        interceptor.setAllowedExtensions(".txt");

        // when file is of allowed extensions
        ValidationAwareSupport validation = new ValidationAwareSupport();
        boolean ok = interceptor.acceptFile(validation, createTestFile(Files.newTemporaryFile()), "filename.txt", "text/plain", "inputName");

        assertThat(ok).isTrue();
        assertThat(validation.getFieldErrors()).isEmpty();
        assertThat(validation.hasErrors()).isFalse();

        // when file is not of allowed extensions
        validation = new ValidationAwareSupport();
        boolean notOk = interceptor.acceptFile(validation, createTestFile(Files.newTemporaryFile()), "filename.html", "text/html", "inputName");

        assertThat(notOk).isFalse();
        assertThat(validation.getFieldErrors()).isNotEmpty();
        assertThat(validation.hasErrors()).isTrue();

        interceptor.setAllowedExtensions(".txt,.lol");
        validation = new ValidationAwareSupport();
        ok = interceptor.acceptFile(validation, createTestFile(Files.newTemporaryFile()), "filename.lol", "text/plain", "inputName");

        assertThat(ok).isTrue();
        assertThat(validation.getFieldErrors()).isEmpty();
        assertThat(validation.hasErrors()).isFalse();
    }

    public void testAcceptFileWithNoFile() {
        interceptor.setAllowedTypes("text/plain");

        // when file is not of allowed types
        ValidationAwareSupport validation = new ValidationAwareSupport();
        boolean notOk = interceptor.acceptFile(validation, null, "filename.html", "text/html", "inputName");

        assertThat(notOk).isFalse();
        assertThat(validation.getFieldErrors()).isNotEmpty();
        assertThat(validation.hasErrors()).isTrue();
        assertThat(validation.getFieldErrors().get("inputName"))
                .hasSize(1)
                .first()
                .asString()
                .startsWith("Error uploading:")
                .contains("inputName");
    }

    public void testAcceptFileWithNoContent() {
        interceptor.setAllowedTypes("text/plain");

        ValidationAwareSupport validation = new ValidationAwareSupport();
        boolean notOk = interceptor.acceptFile(validation, createTestFile(null), "filename.html", "text/plain", "inputName");

        assertThat(notOk).isFalse();
        assertThat(validation.getFieldErrors()).isNotEmpty();
        assertThat(validation.hasErrors()).isTrue();
        assertThat(validation.getFieldErrors().get("inputName"))
                .hasSize(1)
                .first()
                .asString()
                .startsWith("Error uploading:")
                .contains("inputName");
    }

    public void testAcceptFileWithMaxSize() throws Exception {
        interceptor.setMaximumSize(10L);

        // when file is not of allowed types
        ValidationAwareSupport validation = new ValidationAwareSupport();

        URL url = ClassLoaderUtil.getResource("log4j2.xml", ActionFileUploadInterceptorTest.class);
        File file = new File(new URI(url.toString()));
        assertThat(file).exists();
        UploadedFile uploadedFile = StrutsUploadedFile.Builder.create(file).withContentType("text/html").withOriginalName("filename").build();
        boolean notOk = interceptor.acceptFile(validation, uploadedFile, "filename", "text/html", "inputName");

        assertThat(notOk).isFalse();
        assertThat(validation.getFieldErrors()).isNotEmpty();
        assertThat(validation.hasErrors()).isTrue();
        assertThat(validation.getFieldErrors().get("inputName"))
                .hasSize(1)
                .first()
                .asString()
                .contains("The file is too large to be uploaded", "inputName", "log4j2.xml", "allowed mx size is 10");
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
        assertThat(interceptor.intercept(mai)).isEqualTo("NoMultipart");
    }

    public void testInvalidContentTypeMultipartRequest() throws Exception {
        request.setContentType("multipart/form-data"); // not a multipart Content-Type
        request.setMethod("post");

        MyFileUploadAction action = container.inject(MyFileUploadAction.class);
        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());

        ActionContext.getContext().withServletRequest(createMultipartRequestMaxSize(2000));

        interceptor.intercept(mai);

        assertThat(action.hasErrors()).isTrue();
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

        ActionContext.getContext().withServletRequest(createMultipartRequestMaxSize(2000));

        interceptor.intercept(mai);

        assertThat(action.hasErrors()).isTrue();
    }

    public void testSuccessUploadOfATextFileMultipartRequest() throws Exception {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        request.setMethod("post");
        request.addHeader("Content-type", "multipart/form-data; boundary=---1234");

        // inspired by the unit tests for Jakarta Commons FileUpload
        String content = ("""
                -----1234\r
                Content-Disposition: form-data; name="file"; filename="deleteme.txt"\r
                Content-Type: text/html\r
                \r
                Unit test of ActionFileUploadInterceptor\r
                -----1234--\r
                """);
        request.setContent(content.getBytes(StandardCharsets.US_ASCII));

        MyFileUploadAction action = new MyFileUploadAction();

        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());
        ActionContext.getContext().withServletRequest(createMultipartRequestMaxSize(2000));

        interceptor.intercept(mai);

        assertThat(action.hasErrors()).isFalse();

        List<UploadedFile> files = action.getUploadFiles();

        assertThat(files).isNotNull().hasSize(1);
        assertThat(files.get(0).getContentType()).isEqualTo("text/html");
        assertThat(files.get(0).getOriginalName()).isEqualTo("deleteme.txt");
    }

    /**
     * Tests whether with multiple files sent with the same name, the ones with forbiddenTypes (see
     * ActionFileUploadInterceptor.setAllowedTypes(...) ) are sorted out.
     */
    public void testMultipleAccept() throws Exception {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        request.setMethod("POST");
        request.addHeader("Content-type", "multipart/form-data; boundary=\"" + boundary + "\"");
        String content = encodeTextFile("test.html", "text/plain", plainContent) +
                encodeTextFile("test1.html", "text/html", htmlContent) +
                encodeTextFile("test2.html", "text/html", htmlContent) +
                endLine + "--" + boundary + "--";
        request.setContent(content.getBytes());

        assertThat(JakartaServletDiskFileUpload.isMultipartContent(request)).isTrue();

        MyFileUploadAction action = new MyFileUploadAction();
        container.inject(action);
        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());
        ActionContext.getContext().withServletRequest(createMultipartRequestMaxFiles());

        interceptor.setAllowedTypes("text/html");
        interceptor.intercept(mai);

        List<UploadedFile> files = action.getUploadFiles();

        assertThat(files).isNotNull().hasSize(2);
        assertThat(files.get(0).getContentType()).isEqualTo("text/html");
        assertThat(files.get(0).getOriginalName()).isEqualTo("test1.html");
        assertThat(files.get(1).getContentType()).isEqualTo("text/html");
        assertThat(files.get(1).getOriginalName()).isEqualTo("test2.html");
    }

    public void testUnacceptedNumberOfFiles() throws Exception {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        request.setMethod("POST");
        request.addHeader("Content-type", "multipart/form-data; boundary=" + boundary);
        String content = encodeTextFile("test.html", "text/plain", plainContent) +
                encodeTextFile("test1.html", "text/html", htmlContent) +
                encodeTextFile("test2.html", "text/html", htmlContent) +
                encodeTextFile("test3.html", "text/html", htmlContent) +
                endLine +
                "--" +
                boundary +
                "--" +
                endLine;
        request.setContent(content.getBytes());

        assertThat(JakartaServletFileUpload.isMultipartContent(request)).isTrue();

        MyFileUploadAction action = new MyFileUploadAction();
        container.inject(action);
        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());
        ActionContext.getContext().withServletRequest(createMultipartRequestMaxFiles());

        interceptor.setAllowedTypes("text/html");
        interceptor.intercept(mai);

        assertThat(action.getUploadFiles()).isNull();
        assertThat(action.getActionErrors())
                .hasSize(1)
                .first()
                .isEqualTo("Request exceeded allowed number of files! Permitted number of files is: 3!");
    }

    public void testMultipartRequestMaxFileSize() throws Exception {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        request.setMethod("post");
        request.addHeader("Content-type", "multipart/form-data; boundary=---1234");

        // inspired by the unit tests for Jakarta Commons FileUpload
        String content = ("""
                -----1234\r
                Content-Disposition: form-data; name="file"; filename="deleteme.txt"\r
                Content-Type: text/html\r
                \r
                Unit test of ActionFileUploadInterceptor\r
                -----1234--\r
                """);
        request.setContent(content.getBytes(StandardCharsets.US_ASCII));

        MyFileUploadAction action = container.inject(MyFileUploadAction.class);

        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());
        ActionContext.getContext()
                .withServletRequest(createMultipartRequestMaxFileSize());

        interceptor.intercept(mai);

        assertThat(action.hasActionErrors()).isTrue();

        assertThat(action.getActionErrors())
                .hasSize(1)
                .first()
                .isEqualTo("File deleteme.txt assigned to file exceeded allowed size limit! Max size allowed is: 10 but file was: 11!");
    }

    public void testMultipartRequestMaxStringLength() throws Exception {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        request.setMethod("post");
        request.addHeader("Content-type", "multipart/form-data; boundary=---1234");

        // inspired by the unit tests for Jakarta Commons FileUpload
        String content = ("""
                -----1234\r
                Content-Disposition: form-data; name="file"; filename="deleteme.txt"\r
                Content-Type: text/html\r
                \r
                Unit test of ActionFileUploadInterceptor\r
                -----1234\r
                Content-Disposition: form-data; name="normalFormField1"\r
                \r
                it works\r
                -----1234\r
                Content-Disposition: form-data; name="normalFormField2"\r
                \r
                long string should not work\r
                -----1234--\r
                """);
        request.setContent(content.getBytes(StandardCharsets.US_ASCII));

        MyFileUploadAction action = container.inject(MyFileUploadAction.class);

        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());
        ActionContext.getContext()
                .withServletRequest(createMultipartRequestMaxStringLength());

        interceptor.intercept(mai);

        assertThat(action.hasActionErrors()).isTrue();

        assertThat(action.getActionErrors())
                .hasSize(1)
                .first()
                .isEqualTo("The request parameter \"normalFormField2\" was too long. Max length allowed is 20, but found 27!");
    }

    public void testMultipartRequestLocalizedError() throws Exception {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        request.setMethod("post");
        request.addHeader("Content-type", "multipart/form-data; boundary=---1234");

        String content = ("""
                -----1234\r
                Content-Disposition: form-data; name="file"; filename="deleteme.txt"\r
                Content-Type: text/html\r
                \r
                Unit test of ActionFileUploadInterceptor\r
                -----1234--\r
                """);
        request.setContent(content.getBytes(StandardCharsets.US_ASCII));

        MyFileUploadAction action = container.inject(MyFileUploadAction.class);

        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());
        ActionContext.getContext()
                .withLocale(Locale.GERMAN)
                .withServletRequest(createMultipartRequestMaxSize(10));

        interceptor.intercept(mai);

        assertThat(action.hasActionErrors()).isTrue();

        assertThat(action.getActionErrors())
                .hasSize(1)
                .first()
                .asString()
                .startsWith("Der Request übertraf die maximal erlaubte Größe");
    }

    private String encodeTextFile(String filename, String contentType, String content) {
        return endLine +
                "--" + boundary +
                endLine +
                "Content-Disposition: form-data; name=\"file\"; filename=\"" + filename + "\"" +
                endLine +
                "Content-Type: " + contentType +
                endLine +
                endLine +
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

    protected void setUp() throws Exception {
        super.setUp();
        request = new MockHttpServletRequest();
        interceptor = new ActionFileUploadInterceptor();
        container.inject(interceptor);
        tempDir = File.createTempFile("struts", "fileupload");
        assertThat(tempDir.delete()).isTrue();
        assertThat(tempDir.mkdirs()).isTrue();
    }

    protected void tearDown() throws Exception {
        interceptor.destroy();
        super.tearDown();
    }

    private UploadedFile createTestFile(File content) {
        return new UploadedFile() {
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
                return content;
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
