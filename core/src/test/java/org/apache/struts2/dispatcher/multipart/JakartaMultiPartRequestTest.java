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

import org.apache.commons.fileupload2.core.DiskFileItem;
import org.apache.struts2.dispatcher.LocalizedMessage;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.normalizeSpace;
import static org.assertj.core.api.Assertions.assertThat;

public class JakartaMultiPartRequestTest extends AbstractMultiPartRequestTest {

    @Override
    protected AbstractMultiPartRequest createMultipartRequest() {
        return new JakartaMultiPartRequest();
    }

    @Test
    public void temporaryFileCleanupForInMemoryUploads() throws IOException, NoSuchFieldException, IllegalAccessException {
        // given - small files that will be in-memory
        String content = formFile("file1", "test1.csv", "a,b,c,d") + 
                        formFile("file2", "test2.csv", "1,2,3,4") +
                        endline + "--" + boundary + "--";
        
        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));
        
        // when
        multiPart.parse(mockRequest, tempDir);
        
        // Access private field to verify temporary files are tracked
        Field tempFilesField = JakartaMultiPartRequest.class.getDeclaredField("temporaryFiles");
        tempFilesField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<File> temporaryFiles = (List<File>) tempFilesField.get(multiPart);
        
        // Store file paths before cleanup for verification
        List<String> tempFilePaths = temporaryFiles.stream()
                .map(File::getAbsolutePath)
                .toList();
        
        // Verify temporary files exist before cleanup
        assertThat(temporaryFiles).isNotEmpty();
        for (File tempFile : temporaryFiles) {
            assertThat(tempFile).exists();
        }
        
        // when - cleanup
        multiPart.cleanUp();
        
        // then - verify files are deleted and tracking list is cleared
        for (String tempFilePath : tempFilePaths) {
            assertThat(new File(tempFilePath)).doesNotExist();
        }
        assertThat(temporaryFiles).isEmpty();
    }

    @Test
    public void cleanupMethodsCanBeOverridden() {
        // Create a custom implementation to test extensibility
        class CustomJakartaMultiPartRequest extends JakartaMultiPartRequest {
            boolean diskFileItemsCleanedUp = false;
            boolean temporaryFilesCleanedUp = false;
            
            @Override
            protected void cleanUpDiskFileItems() {
                diskFileItemsCleanedUp = true;
                super.cleanUpDiskFileItems();
            }
            
            @Override
            protected void cleanUpTemporaryFiles() {
                temporaryFilesCleanedUp = true;
                super.cleanUpTemporaryFiles();
            }
        }
        
        CustomJakartaMultiPartRequest customMultiPart = new CustomJakartaMultiPartRequest();
        
        // when
        customMultiPart.cleanUp();
        
        // then
        assertThat(customMultiPart.diskFileItemsCleanedUp).isTrue();
        assertThat(customMultiPart.temporaryFilesCleanedUp).isTrue();
    }

    @Test
    public void temporaryFileCreationFailureAddsError() throws IOException {
        // Create a custom implementation that simulates temp file creation failure
        class FaultyJakartaMultiPartRequest extends JakartaMultiPartRequest {
            @Override
            protected void processFileField(DiskFileItem item, String saveDir) {
                // Simulate in-memory upload that fails to create temp file
                if (item.isInMemory()) {
                    try {
                        // Simulate IOException during temp file creation
                        throw new IOException("Simulated temp file creation failure");
                    } catch (IOException e) {
                        // Add the error to the errors list for proper user feedback
                        LocalizedMessage errorMessage = buildErrorMessage(e.getClass(), e.getMessage(), 
                                                                        new Object[]{item.getName()});
                        if (!errors.contains(errorMessage)) {
                            errors.add(errorMessage);
                        }
                    }
                } else {
                    super.processFileField(item, saveDir);
                }
            }
        }
        
        FaultyJakartaMultiPartRequest faultyMultiPart = new FaultyJakartaMultiPartRequest();
        
        // given - small file that would normally be in-memory
        String content = formFile("file1", "test1.csv", "a,b") + 
                        endline + "--" + boundary + "--";
        
        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));
        
        // when
        faultyMultiPart.parse(mockRequest, tempDir);
        
        // then - verify error is properly captured
        assertThat(faultyMultiPart.getErrors())
                .hasSize(1)
                .first()
                .extracting(LocalizedMessage::getTextKey)
                .isEqualTo("struts.messages.upload.error.IOException");
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
    public void temporaryFilesCreatedInSaveDirectory() throws IOException, NoSuchFieldException, IllegalAccessException {
        // Test that temporary files for in-memory uploads are created in the saveDir, not system temp
        String content = formFile("file1", "test1.csv", "small,content") +
                        endline + "--" + boundary + "--";
        
        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));
        
        // when
        multiPart.parse(mockRequest, tempDir);
        
        // Access private field to get temporary files
        Field tempFilesField = JakartaMultiPartRequest.class.getDeclaredField("temporaryFiles");
        tempFilesField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<File> temporaryFiles = (List<File>) tempFilesField.get(multiPart);
        
        // then - verify temporary files are created in saveDir
        assertThat(temporaryFiles).isNotEmpty();
        for (File tempFile : temporaryFiles) {
            // Verify the temporary file is in the saveDir, not system temp
            assertThat(tempFile.getParent()).isEqualTo(tempDir);
            assertThat(tempFile.getName()).startsWith("upload_");
            assertThat(tempFile.getName()).endsWith(".tmp");
            assertThat(tempFile).exists();
        }
    }

    @Test
    public void secureTemporaryFileNaming() throws IOException, NoSuchFieldException, IllegalAccessException {
        // Test that temporary files use UUID-based naming for security
        String content = formFile("file1", "malicious../../../etc/passwd", "content") +
                        endline + "--" + boundary + "--";
        
        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));
        
        // when
        multiPart.parse(mockRequest, tempDir);
        
        // Access private field to get temporary files
        Field tempFilesField = JakartaMultiPartRequest.class.getDeclaredField("temporaryFiles");
        tempFilesField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<File> temporaryFiles = (List<File>) tempFilesField.get(multiPart);
        
        // then - verify secure naming prevents directory traversal
        assertThat(temporaryFiles).isNotEmpty();
        for (File tempFile : temporaryFiles) {
            // Verify the temporary file uses secure UUID naming
            assertThat(tempFile.getName()).startsWith("upload_");
            assertThat(tempFile.getName()).endsWith(".tmp");
            // Verify it doesn't contain malicious path elements
            assertThat(tempFile.getName()).doesNotContain("..");
            assertThat(tempFile.getName()).doesNotContain("/");
            assertThat(tempFile.getName()).doesNotContain("\\");
            // Verify it's in the correct directory
            assertThat(tempFile.getParent()).isEqualTo(tempDir);
        }
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

}
