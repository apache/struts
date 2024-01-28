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
package org.apache.struts2.dispatcher.multipart;

import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletDiskFileUpload;
import org.apache.struts2.dispatcher.LocalizedMessage;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

abstract class AbstractMultiPartRequestTest<T> {

    protected static Path tempDir;

    protected MockHttpServletRequest mockRequest;

    protected final String boundary = "_boundary_";
    protected final String endline = "\r\n";

    protected AbstractMultiPartRequest<T> multiPart;

    abstract protected AbstractMultiPartRequest<T> createMultipartRequest();

    @BeforeClass
    public static void beforeClass() {
        String dirProp = System.getProperty("java.io.tmpdir");
        if (Path.of(dirProp).toFile().exists()) {
            tempDir = Path.of(dirProp, "multi-part-test");
        } else {
            tempDir = Path.of("target", "multi-part-test");
        }
    }

    @Before
    public void before() {
        mockRequest = new MockHttpServletRequest();
        mockRequest.setCharacterEncoding(StandardCharsets.UTF_8.name());
        mockRequest.setMethod("post");
        mockRequest.setContentType("multipart/form-data; boundary=" + boundary);

        multiPart = createMultipartRequest();
    }

    @After
    public void after() {
        multiPart.cleanUp();
    }

    @Test
    public void uploadedFilesToDisk() throws IOException {
        // given
        String content = formFile("file1", "test1.csv", "1,2,3,4") +
                formFile("file2", "test2.csv", "5,6,7,8") +
                endline + "--" + boundary + "--";

        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));

        assertThat(JakartaServletDiskFileUpload.isMultipartContent(mockRequest)).isTrue();

        // when
        multiPart.setBufferSize("1"); // always write files into disk
        multiPart.parse(mockRequest, tempDir.toString());

        // then
        assertThat(multiPart.getErrors())
                .isEmpty();

        assertThat(multiPart.getFileParameterNames().asIterator()).toIterable()
                .asList()
                .containsOnly("file1", "file2");
        assertThat(multiPart.getFile("file1")).allSatisfy(file -> {
            assertThat(file.isFile())
                    .isTrue();
            assertThat(file.getOriginalName())
                    .isEqualTo("test1.csv");
            assertThat(file.getContentType())
                    .isEqualTo("text/csv");
            assertThat(file.getContent()).asInstanceOf(InstanceOfAssertFactories.FILE)
                    .exists()
                    .content()
                    .isEqualTo("1,2,3,4");
        });
        assertThat(multiPart.getFile("file2")).allSatisfy(file -> {
            assertThat(file.isFile())
                    .isTrue();
            assertThat(file.getOriginalName())
                    .isEqualTo("test2.csv");
            assertThat(file.getContentType())
                    .isEqualTo("text/csv");
            assertThat(file.getContent())
                    .asInstanceOf(InstanceOfAssertFactories.FILE)
                    .exists()
                    .content()
                    .isEqualTo("5,6,7,8");
        });
    }

    @Test
    public void uploadedFilesWithLargeBuffer() throws IOException {
        // given
        String content = formFile("file1", "test1.csv", "1,2,3,4") +
                formFile("file2", "test2.csv", "5,6,7,8") +
                endline + "--" + boundary + "--";

        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));

        assertThat(JakartaServletDiskFileUpload.isMultipartContent(mockRequest)).isTrue();

        // when
        multiPart.setBufferSize("8192"); // streams files into disk using larger buffer
        multiPart.parse(mockRequest, tempDir.toString());

        // then
        assertThat(multiPart.getErrors())
                .isEmpty();

        assertThat(multiPart.getFileParameterNames().asIterator()).toIterable()
                .asList()
                .containsOnly("file1", "file2");
        assertThat(multiPart.getFile("file1")).allSatisfy(file -> {
            assertThat(file.isFile())
                    .isTrue();
            assertThat(file.getOriginalName())
                    .isEqualTo("test1.csv");
            assertThat(file.getContentType())
                    .isEqualTo("text/csv");
            assertThat(file.getContent())
                    .asInstanceOf(InstanceOfAssertFactories.FILE)
                    .exists()
                    .content()
                    .isEqualTo("1,2,3,4");
        });
        assertThat(multiPart.getFile("file2")).allSatisfy(file -> {
            assertThat(file.isFile())
                    .isTrue();
            assertThat(file.getOriginalName())
                    .isEqualTo("test2.csv");
            assertThat(file.getContent())
                    .asInstanceOf(InstanceOfAssertFactories.FILE)
                    .exists()
                    .content()
                    .isEqualTo("5,6,7,8");
        });
    }

    @Test
    public void cleanUp() throws IOException {
        // given
        String content = formFile("file1", "test1.csv", "1,2,3,4") +
                formFile("file2", "test2.csv", "5,6,7,8") +
                endline + "--" + boundary + "--";

        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));

        assertThat(JakartaServletDiskFileUpload.isMultipartContent(mockRequest)).isTrue();

        // when
        multiPart.parse(mockRequest, tempDir.toString());

        // then
        assertThat(multiPart.getErrors())
                .isEmpty();

        assertThat(multiPart.getFileParameterNames().asIterator()).toIterable()
                .asList()
                .containsOnly("file1", "file2");
        assertThat(multiPart.getFile("file1")).allSatisfy(file -> {
            assertThat(file.isFile())
                    .isTrue();
            assertThat(file.getOriginalName())
                    .isEqualTo("test1.csv");
            assertThat(file.getContentType())
                    .isEqualTo("text/csv");
            assertThat(file.getContent()).asInstanceOf(InstanceOfAssertFactories.FILE)
                    .exists()
                    .content()
                    .isEqualTo("1,2,3,4");
        });
        assertThat(multiPart.getFile("file2")).allSatisfy(file -> {
            assertThat(file.isFile())
                    .isTrue();
            assertThat(file.getOriginalName())
                    .isEqualTo("test2.csv");
            assertThat(file.getContentType())
                    .isEqualTo("text/csv");
            assertThat(file.getContent())
                    .asInstanceOf(InstanceOfAssertFactories.FILE)
                    .exists()
                    .content()
                    .isEqualTo("5,6,7,8");
        });

        List<UploadedFile<T>> uploadedFiles = new ArrayList<>();
        for (Map.Entry<String, List<UploadedFile<T>>> entry : multiPart.uploadedFiles.entrySet()) {
            uploadedFiles.addAll(entry.getValue());
        }

        // when
        multiPart.cleanUp();

        // then
        assertThat(multiPart.uploadedFiles)
                .isEmpty();
        assertThat(multiPart.parameters)
                .isEmpty();
        assertThat(uploadedFiles).allSatisfy(file -> {
            assertThat(file.getContent()).asInstanceOf(InstanceOfAssertFactories.FILE)
                    .doesNotExist();
        });
    }

    @Test
    public void nonMultiPartUpload() throws IOException {
        String content = formFile("file1", "test1.csv", "1,2,3,4") +
                formFile("file2", "test2.csv", "5,6,7,8") +
                endline + "--" + boundary + "--";
        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));

        // given
        mockRequest.setContentType("");

        // when
        multiPart.parse(mockRequest, tempDir.toString());

        // then
        assertThat(multiPart.getErrors())
                .map(LocalizedMessage::getTextKey)
                .containsExactly("struts.messages.upload.error.FileUploadContentTypeException");

        assertThat(multiPart.getFileParameterNames().asIterator()).toIterable()
                .asList()
                .isEmpty();
    }

    @Test
    public void maxSize() throws IOException {
        String content = formFile("file1", "test1.csv", "1,2,3,4") +
                formFile("file2", "test2.csv", "5,6,7,8") +
                endline + "--" + boundary + "--";

        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));

        assertThat(JakartaServletDiskFileUpload.isMultipartContent(mockRequest)).isTrue();

        multiPart.setMaxSize("1");
        multiPart.parse(mockRequest, tempDir.toString());

        Arrays.stream(multiPart.getFile("file1")).findFirst().map(UploadedFile::length);

        assertThat(multiPart.getErrors())
                .map(LocalizedMessage::getTextKey)
                .containsExactly("struts.messages.upload.error.FileUploadSizeException");
    }

    @Test
    public void maxFilesSize() throws IOException {
        String content = formFile("file1", "test1.csv", "1,2,3,4") +
                formFile("file2", "test2.csv", "5,6,7,8") +
                endline + "--" + boundary + "--";

        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));

        assertThat(JakartaServletDiskFileUpload.isMultipartContent(mockRequest)).isTrue();

        multiPart.setMaxFileSize("1");
        multiPart.parse(mockRequest, tempDir.toString());

        assertThat(multiPart.getErrors())
                .map(LocalizedMessage::getTextKey)
                .containsExactly("struts.messages.upload.error.FileUploadByteCountLimitException");
    }

    @Test
    public void maxStringLength() throws IOException {
        String content = formFile("file1", "test1.csv", "1,2,3,4") +
                formFile("file2", "test2.csv", "5,6,7,8") +
                formField("longText", "very long text") +
                formField("shortText", "short text") +
                endline + "--" + boundary + "--";

        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));

        assertThat(JakartaServletDiskFileUpload.isMultipartContent(mockRequest)).isTrue();

        multiPart.setMaxStringLength("10");
        multiPart.parse(mockRequest, tempDir.toString());

        assertThat(multiPart.getErrors())
                .map(LocalizedMessage::getTextKey)
                .containsExactly("struts.messages.upload.error.parameter.too.long");
    }

    @Test
    public void mismatchCharset() throws IOException {
        // give
        String content = formFile("file1", "test1.csv", "Ł,Ś,Ż,Ó") +
                endline + "--" + boundary + "--";

        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));

        assertThat(JakartaServletDiskFileUpload.isMultipartContent(mockRequest)).isTrue();

        // when
        mockRequest.setCharacterEncoding(null);
        multiPart.setDefaultEncoding(StandardCharsets.ISO_8859_1.name());
        multiPart.parse(mockRequest, tempDir.toString());

        // then
        assertThat(multiPart.getErrors())
                .isEmpty();

        assertThat(multiPart.getFileParameterNames().asIterator()).toIterable()
                .asList()
                .containsOnly("file1");
        assertThat(multiPart.getFile("file1")).allSatisfy(file -> {
            assertThat(file.isFile())
                    .isTrue();
            assertThat(file.getOriginalName())
                    .isEqualTo("test1.csv");
            assertThat(file.getContentType())
                    .isEqualTo("text/csv");
            assertThat(file.getContent())
                    .asInstanceOf(InstanceOfAssertFactories.FILE)
                    .exists()
                    .content()
                    .isEqualTo("Ł,Ś,Ż,Ó");
        });
    }

    @Test
    public void normalFields() throws IOException {
        String content = formFile("file1", "test1.csv", "1,2,3,4") +
                formFile("file2", "test2.csv", "5,6,7,8") +
                formField("longText", "very long text") +
                formField("shortText", "short text") +
                endline + "--" + boundary + "--";

        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));

        assertThat(JakartaServletDiskFileUpload.isMultipartContent(mockRequest)).isTrue();

        multiPart.parse(mockRequest, tempDir.toString());

        assertThat(multiPart.getErrors())
                .isEmpty();

        assertThat(multiPart.getParameterNames().asIterator()).toIterable()
                .hasSize(2)
                .contains("longText", "shortText");
        assertThat(multiPart.getParameterValues("longText"))
                .contains("very long text");
        assertThat(multiPart.getParameterValues("shortText"))
                .contains("short text");
        assertThat(multiPart.getParameter("longText"))
                .isEqualTo("very long text");
        assertThat(multiPart.getParameter("shortText"))
                .isEqualTo("short text");
    }

    protected String formFile(String fieldName, String filename, String content) {
        return endline +
                "--" + boundary + endline +
                "Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + filename + "\"" +
                endline +
                "Content-Type: text/csv" +
                endline +
                endline +
                content;
    }

    protected String formField(String fieldName, String content) {
        return endline +
                "--" + boundary + endline +
                "Content-Disposition: form-data; name=\"" + fieldName + "\"" +
                endline +
                endline +
                content;
    }
}
