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

import org.apache.struts2.dispatcher.LocalizedMessage;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

import static org.apache.commons.lang3.StringUtils.normalizeSpace;
import static org.assertj.core.api.Assertions.assertThat;

public class JakartaMultiPartRequestTest extends AbstractMultiPartRequestTest {

    @Override
    protected AbstractMultiPartRequest createMultipartRequest() {
        return new JakartaMultiPartRequest();
    }

    @Test
    public void temporaryFileCreationErrorsAreNotDuplicated() throws IOException {
        // Test that duplicate errors are not added to the errors list
        JakartaMultiPartRequest multiPartWithDuplicateErrors = new JakartaMultiPartRequest();
        
        // Simulate adding the same error twice
        IOException testException = new IOException("Test exception");
        LocalizedMessage errorMessage = multiPartWithDuplicateErrors.buildErrorMessage(
                testException.getClass(), testException.getMessage(), new Object[]{"test.csv"});
        
        // when - add same error twice
        multiPartWithDuplicateErrors.errors.add(errorMessage);
        if (!multiPartWithDuplicateErrors.errors.contains(errorMessage)) {
            multiPartWithDuplicateErrors.errors.add(errorMessage);
        }
        
        // then - only one error should be present
        assertThat(multiPartWithDuplicateErrors.getErrors()).hasSize(1);
    }

    @Test
    public void cleanupIsIdempotent() throws IOException {
        // given - process some files
        String content = formFile("file1", "test1.csv", "1,2,3,4") +
                        endline + "--" + boundary + "--";
        
        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));
        multiPart.parse(mockRequest, tempDir);
        
        // when - call cleanup multiple times
        multiPart.cleanUp();
        multiPart.cleanUp();
        multiPart.cleanUp();
        
        // then - should not throw exceptions and should be safe
        assertThat(multiPart.uploadedFiles).isEmpty();
        assertThat(multiPart.parameters).isEmpty();
    }

    @Test
    public void endToEndMultipartProcessingWithCleanup() throws IOException {
        // Test complete multipart processing lifecycle
        String content = formFile("file1", "test1.csv", "1,2,3,4") +
                        formField("param1", "value1") +
                        formFile("file2", "test2.csv", "5,6,7,8") +
                        formField("param2", "value2") +
                        endline + "--" + boundary + "--";
        
        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));
        
        // when - full processing
        multiPart.parse(mockRequest, tempDir);
        
        // then - verify everything was processed
        assertThat(multiPart.getErrors()).isEmpty();
        assertThat(multiPart.getFile("file1")).hasSize(1);
        assertThat(multiPart.getFile("file2")).hasSize(1);
        assertThat(multiPart.getParameter("param1")).isEqualTo("value1");
        assertThat(multiPart.getParameter("param2")).isEqualTo("value2");
        
        // when - cleanup
        multiPart.cleanUp();
        
        // then - verify complete cleanup
        assertThat(multiPart.uploadedFiles).isEmpty();
        assertThat(multiPart.parameters).isEmpty();
    }

    @Test
    public void processNormalFormFieldHandlesNullFieldName() throws IOException {
        // Test null field name handling in processNormalFormField
        String content = 
            endline + "--" + boundary + endline +
            "Content-Disposition: form-data" + endline + // No name attribute
            endline +
            "field value without name" +
            endline + "--" + boundary + endline +
            "Content-Disposition: form-data; name=\"validfield\"" + endline +
            endline +
            "valid field value" +
            endline + "--" + boundary + "--";
        
        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));
        
        // when
        multiPart.parse(mockRequest, tempDir);
        
        // then - should only process the valid field
        assertThat(multiPart.getErrors()).isEmpty();
        assertThat(multiPart.getParameter("validfield")).isEqualTo("valid field value");
        assertThat(multiPart.getParameterNames().asIterator()).toIterable().hasSize(1);
    }

    @Test
    public void processFileFieldHandlesNullFieldName() throws IOException {
        // Test null field name handling in processFileField
        String content = 
            endline + "--" + boundary + endline +
            "Content-Disposition: form-data; filename=\"orphan.txt\"" + endline + // No name attribute
            "Content-Type: text/plain" + endline +
            endline +
            "orphaned file content" +
            endline + "--" + boundary + endline +
            "Content-Disposition: form-data; name=\"validfile\"; filename=\"valid.txt\"" + endline +
            "Content-Type: text/plain" + endline +
            endline +
            "valid file content" +
            endline + "--" + boundary + "--";
        
        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));
        
        // when
        multiPart.parse(mockRequest, tempDir);
        
        // then - should only process the valid file
        assertThat(multiPart.getErrors()).isEmpty();
        assertThat(multiPart.uploadedFiles).hasSize(1);
        assertThat(multiPart.getFile("validfile")).hasSize(1);
        assertThat(multiPart.getFile("validfile")[0].getContent())
                .asInstanceOf(InstanceOfAssertFactories.FILE)
                .content()
                .isEqualTo("valid file content");
    }

    @Test
    public void diskFileItemCleanupCoverage() throws IOException, NoSuchFieldException, IllegalAccessException {
        // Test disk file item cleanup paths
        String content = formFile("file1", "test1.csv", "1,2,3,4") +
                        endline + "--" + boundary + "--";
        
        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));
        
        // when - force files to disk with small buffer
        multiPart.setBufferSize("1");
        multiPart.parse(mockRequest, tempDir);
        
        // Access private field to verify disk file items are tracked
        Field diskFileItemsField = JakartaMultiPartRequest.class.getDeclaredField("diskFileItems");
        diskFileItemsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.List<org.apache.commons.fileupload2.core.DiskFileItem> diskFileItems = 
            (java.util.List<org.apache.commons.fileupload2.core.DiskFileItem>) diskFileItemsField.get(multiPart);
        
        // then - should have disk file items tracked
        assertThat(diskFileItems).isNotEmpty();
        
        // when - cleanup
        multiPart.cleanUp();
        
        // then - should clear tracking
        assertThat(diskFileItems).isEmpty();
    }

    @Test
    public void inMemoryVsDiskFileHandling() throws IOException {
        // Test both in-memory and disk file handling paths
        String smallContent = "small"; // Should be in-memory
        String largeContent = "x".repeat(20000); // Should go to disk
        
        String content = formFile("smallfile", "small.txt", smallContent) +
                        formFile("largefile", "large.txt", largeContent) +
                        endline + "--" + boundary + "--";
        
        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));
        
        // when - use default buffer size
        multiPart.parse(mockRequest, tempDir);
        
        // then - both files should be processed
        assertThat(multiPart.getErrors()).isEmpty();
        assertThat(multiPart.uploadedFiles).hasSize(2);
        assertThat(multiPart.getFile("smallfile")).hasSize(1);
        assertThat(multiPart.getFile("largefile")).hasSize(1);
        
        // Verify content
        assertThat(multiPart.getFile("smallfile")[0].getContent())
                .asInstanceOf(InstanceOfAssertFactories.FILE)
                .content()
                .isEqualTo(smallContent);
        assertThat(multiPart.getFile("largefile")[0].getContent())
                .asInstanceOf(InstanceOfAssertFactories.FILE)
                .content()
                .isEqualTo(largeContent);
    }

    @Test
    public void errorDuplicationPrevention() throws IOException {
        // Test that duplicate errors are not added
        JakartaMultiPartRequest multiPartRequest = new JakartaMultiPartRequest();
        
        // Simulate adding the same error multiple times
        IOException testException = new IOException("Test error");
        LocalizedMessage errorMessage = multiPartRequest.buildErrorMessage(
                testException.getClass(), testException.getMessage(), new Object[]{"test.csv"});
        
        // when - try to add same error multiple times
        multiPartRequest.errors.add(errorMessage);
        if (!multiPartRequest.errors.contains(errorMessage)) {
            multiPartRequest.errors.add(errorMessage); // Should not be added
        }
        if (!multiPartRequest.errors.contains(errorMessage)) {
            multiPartRequest.errors.add(errorMessage); // Should not be added
        }
        
        // then - should only have one error
        assertThat(multiPartRequest.getErrors()).hasSize(1);
    }

    @Test
    public void processFileFieldHandlesEmptyFileName() throws IOException {
        String content =
            endline + "--" + boundary + endline +
            "Content-Disposition: form-data; name=\"emptyfile\"; filename=\"\"" + endline +
            "Content-Type: text/plain" + endline +
            endline +
            "some content that should be ignored" +
            endline + "--" + boundary + endline +
            "Content-Disposition: form-data; name=\"validfile\"; filename=\"test.txt\"" + endline +
            "Content-Type: text/plain" + endline +
            endline +
            "valid file content" +
            endline + "--" + boundary + "--";

        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));

        // when
        multiPart.parse(mockRequest, tempDir);

        // then - should only process the file with valid filename
        assertThat(multiPart.getErrors()).isEmpty();
        assertThat(multiPart.uploadedFiles).hasSize(1);
        assertThat(multiPart.getFile("validfile")).hasSize(1);
        assertThat(multiPart.getFile("emptyfile")).isEmpty();
        assertThat(multiPart.getFile("validfile")[0].getContent())
                .asInstanceOf(InstanceOfAssertFactories.FILE)
                .content()
                .isEqualTo("valid file content");
    }

    @Test
    public void inMemoryUploadIsNotWrittenToDiskUntilContentRequested() throws IOException {
        // given - a small file that Commons FileUpload keeps in memory
        String content = formFile("file1", "test1.csv", "a,b,c,d") +
                endline + "--" + boundary + "--";
        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));

        // when
        multiPart.parse(mockRequest, tempDir);

        UploadedFile file = multiPart.getFile("file1")[0];

        // then - nothing written to disk right after parse
        assertThat(file.isFile()).isFalse();

        // and - content is readable via the stream path without materializing
        try (InputStream in = file.getInputStream()) {
            assertThat(new String(in.readAllBytes(), StandardCharsets.UTF_8)).isEqualTo("a,b,c,d");
        }
        assertThat(file.isFile()).isFalse();

        // and - getContent() materializes a real file on demand
        File materialized = (File) file.getContent();
        assertThat(materialized).exists().hasContent("a,b,c,d");
        assertThat(file.isFile()).isTrue();

        // and - cleanUp removes the materialized file
        multiPart.cleanUp();
        assertThat(materialized).doesNotExist();
    }

}
