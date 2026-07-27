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
import org.apache.struts2.dispatcher.multipart.StrutsInMemoryUploadedFile;
import org.apache.struts2.dispatcher.multipart.StrutsUploadedFile;
import org.apache.struts2.dispatcher.multipart.UploadedFile;
import org.apache.struts2.locale.DefaultLocaleProvider;
import org.apache.struts2.mock.MockActionInvocation;
import org.apache.struts2.mock.MockActionProxy;
import org.apache.struts2.util.ClassLoaderUtil;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.util.ValueStackFactory;
import org.assertj.core.util.Files;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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

    public void testAcceptFileDoesNotMaterializeInMemoryUpload() {
        interceptor.setAllowedTypes("text/plain");

        ValidationAwareSupport validation = new ValidationAwareSupport();
        UploadedFile file = StrutsInMemoryUploadedFile.Builder
                .create("hello".getBytes(StandardCharsets.UTF_8), tempDir.toPath())
                .withContentType("text/plain")
                .withOriginalName("f.txt")
                .withInputName("inputName")
                .build();

        boolean ok = interceptor.acceptFile(validation, file, "f.txt", "text/plain", "inputName");

        assertThat(ok).isTrue();
        assertThat(validation.hasErrors()).isFalse();
        // The optimization: validation must NOT have written the in-memory upload to disk.
        assertThat(file.isFile()).isFalse();
    }

    public void testRejectedInMemoryUploadIsStillNotMaterialized() {
        interceptor.setAllowedTypes("text/plain");

        ValidationAwareSupport validation = new ValidationAwareSupport();
        UploadedFile file = StrutsInMemoryUploadedFile.Builder
                .create("hello".getBytes(StandardCharsets.UTF_8), tempDir.toPath())
                .withContentType("text/html")
                .withOriginalName("f.html")
                .withInputName("inputName")
                .build();

        // wrong content type -> rejected
        boolean ok = interceptor.acceptFile(validation, file, "f.html", "text/html", "inputName");

        assertThat(ok).isFalse();
        assertThat(validation.hasErrors()).isTrue();
        // Even on rejection, no disk write happened.
        assertThat(file.isFile()).isFalse();
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
        return createMultipartRequest(request, maxsize, maxfilesize, maxfiles, maxStringLength);
    }

    private MultiPartRequestWrapper createMultipartRequest(MockHttpServletRequest multipartRequest, int maxsize, int maxfilesize, int maxfiles, int maxStringLength) {
        JakartaMultiPartRequest jak = new JakartaMultiPartRequest();
        jak.setMaxSize(String.valueOf(maxsize));
        jak.setMaxFileSize(String.valueOf(maxfilesize));
        jak.setMaxFiles(String.valueOf(maxfiles));
        jak.setMaxStringLength(String.valueOf(maxStringLength));
        jak.setDefaultEncoding(StandardCharsets.UTF_8.name());

        return new MultiPartRequestWrapper(jak, multipartRequest, tempDir.getAbsolutePath(), new DefaultLocaleProvider());
    }

    private MockHttpServletRequest createUploadRequest(String filename, String contentType, String content) {
        MockHttpServletRequest uploadRequest = new MockHttpServletRequest();
        uploadRequest.setCharacterEncoding(StandardCharsets.UTF_8.name());
        uploadRequest.setMethod("POST");
        uploadRequest.addHeader("Content-type", "multipart/form-data; boundary=\"" + boundary + "\"");
        uploadRequest.setContent((encodeTextFile(filename, contentType, content) + endLine + "--" + boundary + "--")
                .getBytes(StandardCharsets.UTF_8));
        return uploadRequest;
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

    /**
     * Tests WithLazyParams functionality - verifies that the interceptor implements
     * the WithLazyParams interface for dynamic parameter evaluation
     */
    public void testImplementsWithLazyParams() {
        assertThat(interceptor).isInstanceOf(WithLazyParams.class);
    }

    /**
     * Tests dynamic parameter evaluation with ${...} expressions from ValueStack
     */
    public void testDynamicParameterEvaluation() throws Exception {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        request.setMethod("POST");
        request.addHeader("Content-type", "multipart/form-data; boundary=\"" + boundary + "\"");

        // Upload two files with different content types
        String content = encodeTextFile("test.txt", "text/plain", plainContent) +
                encodeTextFile("test.html", "text/html", htmlContent) +
                endLine + "--" + boundary + "--";
        request.setContent(content.getBytes());

        MyDynamicFileUploadAction action = new MyDynamicFileUploadAction();
        action.setAllowedMimeTypes("text/plain"); // Only text/plain allowed
        container.inject(action);

        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());

        // Push action to ValueStack so ${allowedMimeTypes} can be resolved
        ActionContext.getContext().getValueStack().push(action);
        ActionContext.getContext().withServletRequest(createMultipartRequestMaxFiles());

        // Simulate WithLazyParams injection by manually setting the parameters
        // In real execution, DefaultActionInvocation.invoke() would call LazyParamInjector
        injectDynamicUploadPolicy(interceptor, ActionContext.getContext(), true, false, false);

        interceptor.intercept(mai);

        List<UploadedFile> files = action.getUploadFiles();

        // Only the text/plain file should be accepted
        assertThat(files).isNotNull().hasSize(1);
        assertThat(files.get(0).getContentType()).isEqualTo("text/plain");
        assertThat(files.get(0).getOriginalName()).isEqualTo("test.txt");
    }

    /**
     * Tests that dynamic parameters can change between requests
     */
    public void testDynamicParametersChangePerRequest() throws Exception {
        // First request - allow only text/plain
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        request.setMethod("POST");
        request.addHeader("Content-type", "multipart/form-data; boundary=\"" + boundary + "\"");

        String content = encodeTextFile("test.txt", "text/plain", plainContent) +
                endLine + "--" + boundary + "--";
        request.setContent(content.getBytes());

        MyDynamicFileUploadAction action1 = new MyDynamicFileUploadAction();
        action1.setAllowedMimeTypes("text/plain");
        container.inject(action1);

        MockActionInvocation mai1 = new MockActionInvocation();
        mai1.setAction(action1);
        mai1.setResultCode("success");
        mai1.setInvocationContext(ActionContext.getContext());
        ActionContext.getContext().getValueStack().push(action1);
        ActionContext.getContext().withServletRequest(createMultipartRequestMaxFiles());

        injectDynamicUploadPolicy(interceptor, ActionContext.getContext(), true, false, false);
        interceptor.intercept(mai1);

        assertThat(action1.getUploadFiles()).isNotNull().hasSize(1);
        assertThat(action1.getUploadFiles().get(0).getContentType()).isEqualTo("text/plain");

        // Second request - allow only text/html
        request = new MockHttpServletRequest();
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        request.setMethod("POST");
        request.addHeader("Content-type", "multipart/form-data; boundary=\"" + boundary + "\"");

        content = encodeTextFile("test.html", "text/html", htmlContent) +
                endLine + "--" + boundary + "--";
        request.setContent(content.getBytes());

        MyDynamicFileUploadAction action2 = new MyDynamicFileUploadAction();
        action2.setAllowedMimeTypes("text/html");
        container.inject(action2);

        MockActionInvocation mai2 = new MockActionInvocation();
        mai2.setAction(action2);
        mai2.setResultCode("success");
        mai2.setInvocationContext(ActionContext.getContext());
        ActionContext.getContext().getValueStack().pop(); // Remove previous action
        ActionContext.getContext().getValueStack().push(action2);
        ActionContext.getContext().withServletRequest(createMultipartRequestMaxFiles());

        // Simulate new parameter evaluation for second request
        injectDynamicUploadPolicy(interceptor, ActionContext.getContext(), true, false, false);
        interceptor.intercept(mai2);

        assertThat(action2.getUploadFiles()).isNotNull().hasSize(1);
        assertThat(action2.getUploadFiles().get(0).getContentType()).isEqualTo("text/html");
    }

    /**
     * Tests dynamic extension validation
     */
    public void testDynamicExtensionValidation() throws Exception {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        request.setMethod("POST");
        request.addHeader("Content-type", "multipart/form-data; boundary=\"" + boundary + "\"");

        String content = encodeTextFile("test.pdf", "application/pdf", "PDF content") +
                encodeTextFile("test.doc", "application/msword", "DOC content") +
                endLine + "--" + boundary + "--";
        request.setContent(content.getBytes());

        MyDynamicFileUploadAction action = new MyDynamicFileUploadAction();
        action.setAllowedExtensions(".pdf");
        container.inject(action);

        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());
        ActionContext.getContext().getValueStack().push(action);
        ActionContext.getContext().withServletRequest(createMultipartRequestMaxFiles());

        injectDynamicUploadPolicy(interceptor, ActionContext.getContext(), false, true, false);
        interceptor.intercept(mai);

        List<UploadedFile> files = action.getUploadFiles();

        // Only the .pdf file should be accepted
        assertThat(files).isNotNull().hasSize(1);
        assertThat(files.get(0).getOriginalName()).isEqualTo("test.pdf");
    }

    /**
     * Tests dynamic maximum size validation
     */
    public void testDynamicMaximumSizeValidation() throws Exception {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        request.setMethod("POST");
        request.addHeader("Content-type", "multipart/form-data; boundary=---1234");

        String content = ("""
                -----1234\r
                Content-Disposition: form-data; name="file"; filename="test.txt"\r
                Content-Type: text/plain\r
                \r
                This is a test file with some content\r
                -----1234--\r
                """);
        request.setContent(content.getBytes(StandardCharsets.US_ASCII));

        MyDynamicFileUploadAction action = new MyDynamicFileUploadAction();
        action.setMaxFileSize(10L); // Very small size to trigger validation error
        container.inject(action);

        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());
        ActionContext.getContext().getValueStack().push(action);
        ActionContext.getContext().withServletRequest(createMultipartRequestMaxFiles());

        injectDynamicUploadPolicy(interceptor, ActionContext.getContext(), false, false, true);
        interceptor.intercept(mai);

        // File should be rejected due to size
        assertThat(action.hasFieldErrors()).isTrue();
        assertThat(action.getFieldErrors().get("file")).isNotEmpty();
    }

    /**
     * Tests that security validation still works correctly with dynamic parameters
     */
    public void testSecurityValidationWithDynamicParameters() throws Exception {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        request.setMethod("POST");
        request.addHeader("Content-type", "multipart/form-data; boundary=\"" + boundary + "\"");

        // Try to upload files with various potentially dangerous content types
        String content = encodeTextFile("script.js", "application/javascript", "alert('xss')") +
                encodeTextFile("test.pdf", "application/pdf", "PDF content") +
                endLine + "--" + boundary + "--";
        request.setContent(content.getBytes());

        MyDynamicFileUploadAction action = new MyDynamicFileUploadAction();
        action.setAllowedMimeTypes("application/pdf");
        action.setAllowedExtensions(".pdf");
        container.inject(action);

        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());
        ActionContext.getContext().getValueStack().push(action);
        ActionContext.getContext().withServletRequest(createMultipartRequestMaxFiles());

        injectDynamicUploadPolicy(interceptor, ActionContext.getContext(), true, true, false);
        interceptor.intercept(mai);

        List<UploadedFile> files = action.getUploadFiles();

        // Only the PDF should be accepted, JavaScript file should be rejected
        assertThat(files).isNotNull().hasSize(1);
        assertThat(files.get(0).getOriginalName()).isEqualTo("test.pdf");
        assertThat(files.get(0).getContentType()).isEqualTo("application/pdf");
    }

    /**
     * Tests wildcard matching with dynamic parameters
     */
    public void testWildcardMatchingWithDynamicParameters() throws Exception {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        request.setMethod("POST");
        request.addHeader("Content-type", "multipart/form-data; boundary=\"" + boundary + "\"");

        String content = encodeTextFile("test.jpg", "image/jpeg", "JPEG content") +
                encodeTextFile("test.png", "image/png", "PNG content") +
                encodeTextFile("test.html", "text/html", htmlContent) +
                endLine + "--" + boundary + "--";
        request.setContent(content.getBytes());

        MyDynamicFileUploadAction action = new MyDynamicFileUploadAction();
        action.setAllowedMimeTypes("image/*"); // Accept all image types
        container.inject(action);

        MockActionInvocation mai = new MockActionInvocation();
        mai.setAction(action);
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());
        ActionContext.getContext().getValueStack().push(action);
        ActionContext.getContext().withServletRequest(createMultipartRequestMaxFiles());

        injectDynamicUploadPolicy(interceptor, ActionContext.getContext(), true, false, false);
        interceptor.intercept(mai);

        List<UploadedFile> files = action.getUploadFiles();

        // Both image files should be accepted, HTML should be rejected
        assertThat(files).isNotNull().hasSize(2);
        assertThat(files.get(0).getContentType()).startsWith("image/");
        assertThat(files.get(1).getContentType()).startsWith("image/");
    }

    public void testConcurrentDynamicPoliciesStayIsolatedPerRequest() throws Exception {
        ActionFileUploadInterceptor controlInterceptor = new ActionFileUploadInterceptor();
        container.inject(controlInterceptor);

        MyDynamicFileUploadAction controlAction = new MyDynamicFileUploadAction();
        controlAction.setAllowedMimeTypes("text/plain");
        container.inject(controlAction);

        try {
            runUploadAttempt(
                    controlInterceptor,
                    controlAction,
                    createUploadRequest("control.html", "text/html", htmlContent)
            );

            assertThat(controlAction.getUploadFiles()).isNull();
            assertThat(controlAction.getFieldErrors()).containsKey("file");
        } finally {
            controlInterceptor.destroy();
        }

        CoordinatedActionFileUploadInterceptor sharedInterceptor = new CoordinatedActionFileUploadInterceptor();
        container.inject(sharedInterceptor);

        MyDynamicFileUploadAction plainPolicyAction = new MyDynamicFileUploadAction();
        plainPolicyAction.setAllowedMimeTypes("text/plain");
        container.inject(plainPolicyAction);

        MyDynamicFileUploadAction htmlPolicyAction = new MyDynamicFileUploadAction();
        htmlPolicyAction.setAllowedMimeTypes("text/html");
        container.inject(htmlPolicyAction);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Future<String> plainResult = executor.submit(() -> runUploadAttempt(
                    sharedInterceptor,
                    plainPolicyAction,
                    createUploadRequest("plain-policy.html", "text/html", htmlContent)
            ));

            assertThat(sharedInterceptor.awaitFirstValidation()).isTrue();

            Future<String> htmlResult = executor.submit(() -> runUploadAttempt(
                    sharedInterceptor,
                    htmlPolicyAction,
                    createUploadRequest("html-policy.html", "text/html", htmlContent)
            ));

            assertThat(htmlResult.get(10, TimeUnit.SECONDS)).isEqualTo("success");
            sharedInterceptor.releaseFirstValidation();
            assertThat(plainResult.get(10, TimeUnit.SECONDS)).isEqualTo("success");
        } finally {
            sharedInterceptor.releaseFirstValidation();
            executor.shutdownNow();
            sharedInterceptor.destroy();
        }

        assertThat(plainPolicyAction.getAllowedMimeTypes()).isEqualTo("text/plain");
        assertThat(plainPolicyAction.getUploadFiles()).isNull();
        assertThat(plainPolicyAction.getFieldErrors()).containsKey("file");

        assertThat(htmlPolicyAction.hasFieldErrors()).isFalse();
        assertThat(htmlPolicyAction.getUploadFiles()).isNotNull().hasSize(1);
        assertThat(htmlPolicyAction.getUploadFiles().get(0).getOriginalName()).isEqualTo("html-policy.html");
        assertThat(htmlPolicyAction.getUploadFiles().get(0).getContentType()).isEqualTo("text/html");
    }

    private String runUploadAttempt(ActionFileUploadInterceptor actionFileUploadInterceptor,
                                    MyDynamicFileUploadAction action,
                                    MockHttpServletRequest uploadRequest) throws Exception {
        MultiPartRequestWrapper multiPartRequest = createMultipartRequest(uploadRequest, -1, -1, 3, -1);
        ValueStack valueStack = container.getInstance(ValueStackFactory.class).createValueStack();
        valueStack.push(action);

        ActionContext context = ActionContext.of(valueStack.getContext())
                .withContainer(container)
                .withValueStack(valueStack)
                .withServletRequest(multiPartRequest)
                .bind();

        try {
            MockActionInvocation invocation = new MockActionInvocation();
            invocation.setAction(action);
            invocation.setResultCode("success");
            invocation.setInvocationContext(context);

            injectDynamicUploadPolicy(actionFileUploadInterceptor, context, true, false, false);
            return actionFileUploadInterceptor.intercept(invocation);
        } finally {
            ActionContext.clear();
        }
    }

    private void injectDynamicUploadPolicy(ActionFileUploadInterceptor actionFileUploadInterceptor,
                                           ActionContext context,
                                           boolean includeAllowedTypes,
                                           boolean includeAllowedExtensions,
                                           boolean includeMaximumSize) {
        Map<String, String> params = new HashMap<>();
        if (includeAllowedTypes) {
            params.put("allowedTypes", "${allowedMimeTypes}");
        }
        if (includeAllowedExtensions) {
            params.put("allowedExtensions", "${allowedExtensions}");
        }
        if (includeMaximumSize) {
            params.put("maximumSize", "${maxFileSize}");
        }

        WithLazyParams.LazyParamInjector lazyParamInjector =
                new WithLazyParams.LazyParamInjector(context.getValueStack());
        container.inject(lazyParamInjector);
        lazyParamInjector.injectParams(actionFileUploadInterceptor, params, context);
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

    /**
     * Test action class that demonstrates dynamic file upload configuration
     */
    public static class MyDynamicFileUploadAction extends ActionSupport implements UploadedFilesAware {
        private List<UploadedFile> uploadedFiles;
        private String allowedMimeTypes;
        private String allowedExtensions;
        private Long maxFileSize;

        @Override
        public void withUploadedFiles(List<UploadedFile> uploadedFiles) {
            this.uploadedFiles = uploadedFiles;
        }

        public List<UploadedFile> getUploadFiles() {
            return this.uploadedFiles;
        }

        public String getAllowedMimeTypes() {
            return allowedMimeTypes;
        }

        public void setAllowedMimeTypes(String allowedMimeTypes) {
            this.allowedMimeTypes = allowedMimeTypes;
        }

        public String getAllowedExtensions() {
            return allowedExtensions;
        }

        public void setAllowedExtensions(String allowedExtensions) {
            this.allowedExtensions = allowedExtensions;
        }

        public Long getMaxFileSize() {
            return maxFileSize;
        }

        public void setMaxFileSize(Long maxFileSize) {
            this.maxFileSize = maxFileSize;
        }
    }

    private static final class CoordinatedActionFileUploadInterceptor extends ActionFileUploadInterceptor {
        private final AtomicBoolean pauseFirstValidation = new AtomicBoolean(true);
        private final CountDownLatch firstValidationEntered = new CountDownLatch(1);
        private final CountDownLatch allowFirstValidationToContinue = new CountDownLatch(1);

        @Override
        protected boolean acceptFile(Object action, UploadedFile file, String originalFilename, String contentType, String inputName) {
            if (pauseFirstValidation.compareAndSet(true, false)) {
                firstValidationEntered.countDown();
                awaitUnchecked(allowFirstValidationToContinue);
            }
            return super.acceptFile(action, file, originalFilename, contentType, inputName);
        }

        private boolean awaitFirstValidation() throws InterruptedException {
            return firstValidationEntered.await(10, TimeUnit.SECONDS);
        }

        private void releaseFirstValidation() {
            allowFirstValidationToContinue.countDown();
        }

        private void awaitUnchecked(CountDownLatch latch) {
            try {
                if (!latch.await(10, TimeUnit.SECONDS)) {
                    throw new AssertionError("Timed out waiting for concurrent validation release");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new AssertionError("Interrupted while waiting for concurrent validation release", e);
            }
        }
    }

}
