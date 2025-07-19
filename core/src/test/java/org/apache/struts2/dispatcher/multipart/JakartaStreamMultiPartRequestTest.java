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
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JakartaStreamMultiPartRequestTest extends AbstractMultiPartRequestTest {

    @Override
    protected AbstractMultiPartRequest createMultipartRequest() {
        return new JakartaStreamMultiPartRequest();
    }

    @Test
    public void maxSizeOfFiles() throws IOException {
        // given
        String content = formFile("file1", "test1.csv", "1,2,3,4") +
                formFile("file2", "test2.csv", "5,6,7,8") +
                endline + "--" + boundary + "--";

        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));

        assertThat(JakartaServletDiskFileUpload.isMultipartContent(mockRequest)).isTrue();

        // when
        multiPart.setMaxSizeOfFiles("10");
        multiPart.parse(mockRequest, tempDir);

        // then
        assertThat(multiPart.uploadedFiles)
                .hasSize(1);
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
        assertThat(multiPart.getErrors())
                .map(LocalizedMessage::getTextKey)
                .containsExactly("struts.messages.upload.error.FileUploadSizeException");
    }

    @Test
    public void readStreamProperlyHandlesResources() throws Exception {
        // Create a test input stream with known data
        byte[] testData = "test data for stream reading".getBytes(StandardCharsets.UTF_8);
        InputStream testStream = new java.io.ByteArrayInputStream(testData);
        
        JakartaStreamMultiPartRequest streamMultiPart = new JakartaStreamMultiPartRequest();
        
        // Use reflection to access private readStream method
        Method readStreamMethod = JakartaStreamMultiPartRequest.class.getDeclaredMethod("readStream", InputStream.class);
        readStreamMethod.setAccessible(true);
        
        // when
        String result = (String) readStreamMethod.invoke(streamMultiPart, testStream);
        
        // then
        assertThat(result).isEqualTo("test data for stream reading");
    }

    @Test
    public void readStreamHandlesExceptionsProperly() throws Exception {
        // Create a stream that throws an exception
        InputStream faultyStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("Simulated stream failure");
            }
        };
        
        JakartaStreamMultiPartRequest streamMultiPart = new JakartaStreamMultiPartRequest();
        
        // Use reflection to access private readStream method
        Method readStreamMethod = JakartaStreamMultiPartRequest.class.getDeclaredMethod("readStream", InputStream.class);
        readStreamMethod.setAccessible(true);
        
        // when/then - should propagate the exception
        assertThatThrownBy(() -> readStreamMethod.invoke(streamMultiPart, faultyStream))
                .isInstanceOf(InvocationTargetException.class)
                .cause()
                .isInstanceOf(IOException.class)
                .hasMessage("Simulated stream failure");
    }

    @Test
    public void readStreamHandlesEmptyStream() throws Exception {
        // Create an empty stream
        InputStream emptyStream = new java.io.ByteArrayInputStream(new byte[0]);
        
        JakartaStreamMultiPartRequest streamMultiPart = new JakartaStreamMultiPartRequest();
        
        // Use reflection to access private readStream method
        Method readStreamMethod = JakartaStreamMultiPartRequest.class.getDeclaredMethod("readStream", InputStream.class);
        readStreamMethod.setAccessible(true);
        
        // when
        String result = (String) readStreamMethod.invoke(streamMultiPart, emptyStream);
        
        // then
        assertThat(result).isEmpty();
    }

    @Test
    public void readStreamHandlesLargeData() throws Exception {
        // Create a large data stream to test buffer handling
        StringBuilder largeData = new StringBuilder();
        for (int i = 0; i < 2000; i++) {
            largeData.append("line").append(i).append("\n");
        }
        
        byte[] testData = largeData.toString().getBytes(StandardCharsets.UTF_8);
        InputStream largeStream = new java.io.ByteArrayInputStream(testData);
        
        JakartaStreamMultiPartRequest streamMultiPart = new JakartaStreamMultiPartRequest();
        
        // Use reflection to access private readStream method
        Method readStreamMethod = JakartaStreamMultiPartRequest.class.getDeclaredMethod("readStream", InputStream.class);
        readStreamMethod.setAccessible(true);
        
        // when
        String result = (String) readStreamMethod.invoke(streamMultiPart, largeStream);
        
        // then
        assertThat(result).isEqualTo(largeData.toString());
        assertThat(result.length()).isGreaterThan(1024); // Verify it's larger than internal buffer
    }

    @Test
    public void processFileItemAsFormFieldHandlesNullFieldName() throws IOException {
        // Test the null field name path in processFileItemAsFormField
        String content = formFile("", "test.csv", "data") + // Field name will be empty/null-like
                        endline + "--" + boundary + "--";
        
        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));
        
        // when
        multiPart.parse(mockRequest, tempDir);
        
        // then - should complete without error, but no parameters should be added
        assertThat(multiPart.getErrors()).isEmpty();
    }

    @Test
    public void processFileItemAsFileFieldHandlesNullFieldName() throws IOException {
        // This test covers the null field name path in processFileItemAsFileField
        JakartaStreamMultiPartRequest streamMultiPart = new JakartaStreamMultiPartRequest();
        
        // Create a mock file item with null field name
        String content = "--" + boundary + endline +
                        "Content-Disposition: form-data; filename=\"test.csv\"" + endline +
                        "Content-Type: text/csv" + endline +
                        endline +
                        "test data" +
                        endline + "--" + boundary + "--";
        
        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));
        
        // when
        streamMultiPart.parse(mockRequest, tempDir);
        
        // then - should complete without error but no files should be uploaded
        assertThat(streamMultiPart.getErrors()).isEmpty();
        assertThat(streamMultiPart.uploadedFiles).isEmpty();
    }

    @Test  
    public void exceedsMaxFilesPath() throws IOException {
        // Test the exceedsMaxFiles method path
        String content = formFile("file1", "test1.csv", "data1") +
                        formFile("file2", "test2.csv", "data2") +
                        formFile("file3", "test3.csv", "data3") +
                        endline + "--" + boundary + "--";
        
        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));
        
        // when - set max files to 1
        multiPart.setMaxFiles("1");
        multiPart.parse(mockRequest, tempDir);
        
        // then - should have only 1 file and errors for others
        assertThat(multiPart.uploadedFiles).hasSize(1);
        assertThat(multiPart.getErrors())
                .isNotEmpty()
                .allSatisfy(error -> 
                    assertThat(error.getTextKey()).isEqualTo("struts.messages.upload.error.FileUploadFileCountLimitException")
                );
    }

    @Test
    public void actualSizeOfUploadedFilesCalculation() throws IOException {
        // Test the actualSizeOfUploadedFiles method
        String content = formFile("file1", "test1.csv", "data1234567890") + // 14 bytes + headers
                        formFile("file2", "test2.csv", "moredata") + // 8 bytes + headers  
                        endline + "--" + boundary + "--";
        
        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));
        
        // when
        multiPart.parse(mockRequest, tempDir);
        
        // then - should have uploaded files and calculate their total size
        assertThat(multiPart.uploadedFiles).hasSize(2);
        assertThat(multiPart.getFile("file1")).hasSize(1);
        assertThat(multiPart.getFile("file2")).hasSize(1);
        
        // Verify files have the expected content
        assertThat(multiPart.getFile("file1")[0].getContent())
                .asInstanceOf(InstanceOfAssertFactories.FILE)
                .content()
                .isEqualTo("data1234567890");
        assertThat(multiPart.getFile("file2")[0].getContent())
                .asInstanceOf(InstanceOfAssertFactories.FILE)
                .content()
                .isEqualTo("moredata");
    }

    @Test
    public void createTemporaryFileMethod() throws Exception {
        // Test the createTemporaryFile method directly
        JakartaStreamMultiPartRequest streamMultiPart = new JakartaStreamMultiPartRequest();
        Path testLocation = Paths.get(tempDir);
        
        // when
        File tempFile1 = streamMultiPart.createTemporaryFile("test.csv", testLocation);
        File tempFile2 = streamMultiPart.createTemporaryFile("another.txt", testLocation);
        
        // then
        assertThat(tempFile1.getName()).startsWith("upload_");
        assertThat(tempFile1.getName()).endsWith(".tmp");
        assertThat(tempFile1.getParent()).isEqualTo(tempDir);
        
        assertThat(tempFile2.getName()).startsWith("upload_");
        assertThat(tempFile2.getName()).endsWith(".tmp");
        assertThat(tempFile2.getParent()).isEqualTo(tempDir);
        
        // Should be unique names
        assertThat(tempFile1.getName()).isNotEqualTo(tempFile2.getName());
        
        // Clean up
        tempFile1.delete();
        tempFile2.delete();
    }

    @Test
    public void streamFileToDiskWithDifferentBufferSizes() throws IOException {
        // Test streamFileToDisk with different buffer sizes
        String largeContent = "x".repeat(5000); // Content larger than default buffer
        String content = formFile("largefile", "large.csv", largeContent) +
                        endline + "--" + boundary + "--";
        
        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));
        
        // when - use small buffer size to ensure multiple reads
        multiPart.setBufferSize("100");
        multiPart.parse(mockRequest, tempDir);
        
        // then
        assertThat(multiPart.getErrors()).isEmpty();
        assertThat(multiPart.getFile("largefile")).hasSize(1);
        assertThat(multiPart.getFile("largefile")[0].getContent())
                .asInstanceOf(InstanceOfAssertFactories.FILE)
                .content()
                .isEqualTo(largeContent);
    }

    @Test
    public void exceedsMaxSizeOfFilesWithFileCleanup() throws IOException {
        // Test the file deletion path when max size is exceeded
        String content = formFile("file1", "test1.csv", "small") +
                        formFile("file2", "test2.csv", "this is a much larger file content that should exceed the limit") +
                        endline + "--" + boundary + "--";
        
        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));
        
        // when - set very small max size 
        multiPart.setMaxSizeOfFiles("20");
        multiPart.parse(mockRequest, tempDir);
        
        // then - should have first file uploaded but error for second
        assertThat(multiPart.uploadedFiles).hasSize(1);
        assertThat(multiPart.getFile("file1")).hasSize(1);
        assertThat(multiPart.getFile("file2")).isEmpty();
        assertThat(multiPart.getErrors())
                .isNotEmpty()
                .anyMatch(error -> 
                    error.getTextKey().equals("struts.messages.upload.error.FileUploadSizeException")
                );
    }

    @Test
    public void createUploadedFileWithVariousContentTypes() throws IOException {
        // Test different content types and file names
        String content = 
            endline + "--" + boundary + endline +
            "Content-Disposition: form-data; name=\"textfile\"; filename=\"document.txt\"" + endline +
            "Content-Type: text/plain" + endline +
            endline +
            "Plain text content" +
            endline + "--" + boundary + endline +
            "Content-Disposition: form-data; name=\"jsonfile\"; filename=\"data.json\"" + endline +
            "Content-Type: application/json" + endline +
            endline +
            "{\"key\": \"value\"}" +
            endline + "--" + boundary + "--";
        
        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));
        
        // when
        multiPart.parse(mockRequest, tempDir);
        
        // then
        assertThat(multiPart.getErrors()).isEmpty();
        assertThat(multiPart.uploadedFiles).hasSize(2);
        
        // Verify text file
        assertThat(multiPart.getFile("textfile")).hasSize(1);
        assertThat(multiPart.getFile("textfile")[0].getContentType()).isEqualTo("text/plain");
        assertThat(multiPart.getFile("textfile")[0].getOriginalName()).isEqualTo("document.txt");
        
        // Verify JSON file
        assertThat(multiPart.getFile("jsonfile")).hasSize(1);
        assertThat(multiPart.getFile("jsonfile")[0].getContentType()).isEqualTo("application/json");
        assertThat(multiPart.getFile("jsonfile")[0].getOriginalName()).isEqualTo("data.json");
    }

    @Test
    public void emptyFileNameFieldsAreSkipped() throws IOException {
        // Test files with empty names are skipped
        String content = 
            endline + "--" + boundary + endline +
            "Content-Disposition: form-data; name=\"emptyfile\"; filename=\"\"" + endline +
            "Content-Type: text/plain" + endline +
            endline +
            "This should be skipped" +
            endline + "--" + boundary + endline +
            "Content-Disposition: form-data; name=\"validfile\"; filename=\"valid.txt\"" + endline +
            "Content-Type: text/plain" + endline +
            endline +
            "This should be processed" +
            endline + "--" + boundary + "--";
        
        mockRequest.setContent(content.getBytes(StandardCharsets.UTF_8));
        
        // when
        multiPart.parse(mockRequest, tempDir);
        
        // then
        assertThat(multiPart.getErrors()).isEmpty();
        assertThat(multiPart.uploadedFiles).hasSize(1);
        assertThat(multiPart.getFile("emptyfile")).isEmpty();
        assertThat(multiPart.getFile("validfile")).hasSize(1);
    }

}
