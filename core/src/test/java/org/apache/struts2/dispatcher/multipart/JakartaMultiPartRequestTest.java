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

import org.apache.commons.fileupload.FileItem;
import org.apache.struts2.StrutsInternalTestCase;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Test cases for {@link JakartaMultiPartRequest} that verify security-related functionality,
 * specifically comprehensive cleanup of temporary files.
 */
public class JakartaMultiPartRequestTest extends StrutsInternalTestCase {

    private File tempDir;
    private JakartaMultiPartRequest multiPartRequest;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        // Create a temporary directory for test files
        Path tempPath = Files.createTempDirectory("struts-multipart-test");
        tempDir = tempPath.toFile();
        
        multiPartRequest = new TestableJakartaMultiPartRequest();
        multiPartRequest.setMaxSize("2048");
        multiPartRequest.setMaxFiles("10");
        multiPartRequest.setMaxFileSize("1024");
    }

    @Override
    protected void tearDown() throws Exception {
        if (tempDir != null && tempDir.exists()) {
            // Clean up test directory
            File[] files = tempDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            tempDir.delete();
        }
        super.tearDown();
    }

    /**
     * Test that comprehensive cleanup removes all temporary files created during multipart processing.
     * This addresses the security vulnerability where temporary files could be leaked.
     */
    public void testComprehensiveCleanupRemovesAllTempFiles() throws Exception {
        // Create a mock multipart request with both file upload and form field
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
        request.setMethod("POST");
        
        String multipartContent = createMultipartContent();
        request.setContent(multipartContent.getBytes(StandardCharsets.UTF_8));

        // Count files before processing
        int filesBefore = countTempFiles();

        // Process the multipart request
        multiPartRequest.parse(request, tempDir.getAbsolutePath());

        // Count files after processing (should be more due to temp files)
        int filesAfterProcessing = countTempFiles();

        // Verify that temp files were created during processing
        assertTrue("Temporary files should be created during multipart processing", 
                   filesAfterProcessing > filesBefore);

        // Perform cleanup
        multiPartRequest.cleanUp();

        // Count files after cleanup
        int filesAfterCleanup = countTempFiles();

        // Verify comprehensive cleanup removed all temporary files
        assertEquals("All temporary files should be cleaned up", filesBefore, filesAfterCleanup);
    }

    /**
     * Test that all FileItem instances are tracked for cleanup, including both file uploads and form fields.
     */
    public void testAllFileItemsAreTrackedForCleanup() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
        request.setMethod("POST");
        
        String multipartContent = createMultipartContentWithMultipleFields();
        request.setContent(multipartContent.getBytes(StandardCharsets.UTF_8));

        // Process the multipart request
        multiPartRequest.parse(request, tempDir.getAbsolutePath());

        // Access the tracked items through our testable implementation
        TestableJakartaMultiPartRequest testable = (TestableJakartaMultiPartRequest) multiPartRequest;
        List<FileItem> trackedItems = testable.getAllFileItems();

        // Verify that all items (both files and form fields) are tracked
        assertTrue("Should track multiple FileItem instances", trackedItems.size() >= 3);
        
        // Verify tracking includes both form fields and file uploads
        boolean hasFormField = false;
        boolean hasFileUpload = false;
        
        for (FileItem item : trackedItems) {
            if (item.isFormField()) {
                hasFormField = true;
            } else {
                hasFileUpload = true;
            }
        }
        
        assertTrue("Should track form field items", hasFormField);
        assertTrue("Should track file upload items", hasFileUpload);
    }

    private String createMultipartContent() {
        return "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\n" +
               "Content-Disposition: form-data; name=\"textField\"\r\n\r\n" +
               "test value\r\n" +
               "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\n" +
               "Content-Disposition: form-data; name=\"fileField\"; filename=\"test.txt\"\r\n" +
               "Content-Type: text/plain\r\n\r\n" +
               "file content\r\n" +
               "------WebKitFormBoundary7MA4YWxkTrZu0gW--\r\n";
    }

    private String createMultipartContentWithMultipleFields() {
        return "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\n" +
               "Content-Disposition: form-data; name=\"textField1\"\r\n\r\n" +
               "value1\r\n" +
               "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\n" +
               "Content-Disposition: form-data; name=\"textField2\"\r\n\r\n" +
               "value2\r\n" +
               "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\n" +
               "Content-Disposition: form-data; name=\"fileField\"; filename=\"test.txt\"\r\n" +
               "Content-Type: text/plain\r\n\r\n" +
               "file content\r\n" +
               "------WebKitFormBoundary7MA4YWxkTrZu0gW--\r\n";
    }

    private int countTempFiles() {
        if (tempDir == null || !tempDir.exists()) {
            return 0;
        }
        File[] files = tempDir.listFiles();
        return files != null ? files.length : 0;
    }

    /**
     * Testable subclass that exposes internal state for verification
     */
    private static class TestableJakartaMultiPartRequest extends JakartaMultiPartRequest {
        public List<FileItem> getAllFileItems() {
            return allFileItems;
        }
    }
}