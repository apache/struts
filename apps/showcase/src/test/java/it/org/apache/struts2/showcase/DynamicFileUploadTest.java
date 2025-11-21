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
package it.org.apache.struts2.showcase;

import org.assertj.core.api.Assertions;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlFileInput;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlRadioButtonInput;
import org.htmlunit.html.HtmlSubmitInput;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.security.SecureRandom;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for dynamic file upload validation feature.
 * Tests the WithLazyParams functionality that allows runtime configuration
 * of file upload validation rules based on action state.
 */
public class DynamicFileUploadTest {

    @Test
    public void testDynamicUploadValidDocument() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            final HtmlPage page = webClient.getPage(ParameterUtils.getBaseUrl() + "/fileupload/dynamicUpload.action");
            final HtmlForm form = page.getForms().get(0);

            // Select document upload type
            HtmlRadioButtonInput documentRadio = form.getInputByValue("document");
            documentRadio.setChecked(true);

            // Create a small PDF-like file
            File pdfFile = createTestFile("test.pdf", 1024);
            pdfFile.deleteOnExit();

            HtmlFileInput uploadInput = form.getInputByName("upload");
            uploadInput.setFiles(pdfFile);

            final HtmlSubmitInput button = form.getInputByValue("Upload File");
            final HtmlPage resultPage = button.click();

            String content = resultPage.getVisibleText();
            assertThat(content).contains(
                    "File Upload Successful",
                    "Upload Type:\nDocument",
                    "Original Name:\n" + pdfFile.getName()
            );
        }
    }

    @Test
    public void testDynamicUploadValidImage() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            final HtmlPage page = webClient.getPage(ParameterUtils.getBaseUrl() + "/fileupload/dynamicUpload.action");
            final HtmlForm form = page.getForms().get(0);

            // Select image upload type
            HtmlRadioButtonInput imageRadio = form.getInputByValue("image");
            imageRadio.setChecked(true);

            assertThat(imageRadio)
                    .isNotNull()
                    .hasFieldOrProperty("value")
                    .isNotNull()
                    .extracting(HtmlRadioButtonInput::isChecked)
                    .asInstanceOf(Assertions.BOOLEAN).isTrue();
            
            // Create a small image-like file
            File imageFile = createTestFile("test.png", 1024);
            imageFile.deleteOnExit();

            HtmlFileInput uploadInput = form.getInputByName("upload");
            uploadInput.setFiles(imageFile);

            final HtmlSubmitInput button = form.getInputByValue("Upload File");
            final HtmlPage resultPage = button.click();

            String content = resultPage.getVisibleText();
            assertThat(content).contains(
                    "File Upload Successful",
                    "Upload Type:\nImage",
                    "Original Name:\n" + imageFile.getName()
            );
        }
    }

    @Test
    public void testDynamicUploadDocumentRejectsImage() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            final HtmlPage page = webClient.getPage(ParameterUtils.getBaseUrl() + "/fileupload/dynamicUpload.action");
            final HtmlForm form = page.getForms().get(0);

            // Select document upload type
            HtmlRadioButtonInput documentRadio = form.getInputByValue("document");
            documentRadio.setChecked(true);

            // Try to upload an image file
            File imageFile = createTestFile("test.jpg", 512);
            imageFile.deleteOnExit();

            HtmlFileInput uploadInput = form.getInputByName("upload");
            uploadInput.setFiles(imageFile);

            final HtmlSubmitInput button = form.getInputByValue("Upload File");
            final HtmlPage resultPage = button.click();

            String content = resultPage.getVisibleText();
            assertThat(content).contains(
                    "Content-Type not allowed",
                    "image/jpeg",
                    "File extension not allowed"
                    
            );
        }
    }

    @Test
    public void testDynamicUploadImageRejectsDocument() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            final HtmlPage page = webClient.getPage(ParameterUtils.getBaseUrl() + "/fileupload/dynamicUpload.action");
            final HtmlForm form = page.getForms().get(0);

            // Select image upload type
            HtmlRadioButtonInput imageRadio = form.getInputByValue("image");
            imageRadio.setChecked(true);

            // Try to upload a PDF file
            File pdfFile = createTestFile("test.pdf", 512);
            pdfFile.deleteOnExit();

            HtmlFileInput uploadInput = form.getInputByName("upload");
            uploadInput.setFiles(pdfFile);

            final HtmlSubmitInput button = form.getInputByValue("Upload File");
            final HtmlPage resultPage = button.click();

            String content = resultPage.getVisibleText();
            assertThat(content).contains(
                    "Content-Type not allowed",
                    "application/pdf",
                    "File extension not allowed"
            );
        }
    }

    @Test
    public void testDynamicUploadDocumentExceedsMaxSize() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            final HtmlPage page = webClient.getPage(ParameterUtils.getBaseUrl() + "/fileupload/dynamicUpload.action");
            final HtmlForm form = page.getForms().get(0);

            // Select document upload type (max 5MB)
            HtmlRadioButtonInput documentRadio = form.getInputByValue("document");
            documentRadio.setChecked(true);

            // Create a file larger than 5MB
            File largeFile = createLargeTestFile("large.pdf", 5 * 1024 * 1024 + 1024);
            largeFile.deleteOnExit();

            HtmlFileInput uploadInput = form.getInputByName("upload");
            uploadInput.setFiles(largeFile);

            final HtmlSubmitInput button = form.getInputByValue("Upload File");
            final HtmlPage resultPage = button.click();

            String content = resultPage.getVisibleText();
            assertThat(content).containsAnyOf("size", "Size", "limit", "exceed");
        }
    }

    @Test
    public void testDynamicUploadImageExceedsMaxSize() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            final HtmlPage page = webClient.getPage(ParameterUtils.getBaseUrl() + "/fileupload/dynamicUpload.action");
            final HtmlForm form = page.getForms().get(0);

            // Select image upload type (max 2MB)
            HtmlRadioButtonInput imageRadio = form.getInputByValue("image");
            imageRadio.setChecked(true);

            // Create a file larger than 2MB
            File largeFile = createLargeTestFile("large.png", 2 * 1024 * 1024 + 1024);
            largeFile.deleteOnExit();

            HtmlFileInput uploadInput = form.getInputByName("upload");
            uploadInput.setFiles(largeFile);

            final HtmlSubmitInput button = form.getInputByValue("Upload File");
            final HtmlPage resultPage = button.click();

            String content = resultPage.getVisibleText();
            assertThat(content).containsAnyOf("size", "Size", "limit", "exceed");
        }
    }

    @Test
    public void testDynamicUploadSwitchBetweenModes() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            final HtmlPage page = webClient.getPage(ParameterUtils.getBaseUrl() + "/fileupload/dynamicUpload.action");

            // Verify initial state shows document mode by default
            String initialContent = page.getVisibleText();
            assertThat(initialContent).contains("Document Upload");

            final HtmlForm form = page.getForms().get(0);

            // Switch to image mode and refresh rules
            HtmlRadioButtonInput imageRadio = form.getInputByValue("image");
            imageRadio.setChecked(true);

            final HtmlSubmitInput refreshButton = form.getInputByValue("Refresh Rules");
            final HtmlPage refreshedPage = refreshButton.click();

            // Verify rules changed to image mode
            String refreshedContent = refreshedPage.getVisibleText();
            assertThat(refreshedContent).contains(
                    "Image Upload",
                    "image/jpeg",
                    "2 MB"
            );
        }
    }

    /**
     * Creates a small test file with specified name and extension.
     */
    private File createTestFile(String fileName, int sizeInBytes) throws Exception {
        File tempFile = File.createTempFile("test_", fileName);
        try (FileWriter writer = new FileWriter(tempFile)) {
            // Write some content to make it non-empty
            for (int i = 0; i < sizeInBytes; i++) {
                writer.write('A');
            }
            writer.flush();
        }
        return tempFile;
    }

    /**
     * Creates a large test file for size limit testing.
     */
    private File createLargeTestFile(String fileName, int sizeInBytes) throws Exception {
        File tempFile = File.createTempFile("large_test_", fileName);
        SecureRandom rng = new SecureRandom();

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[8192];
            int remaining = sizeInBytes;

            while (remaining > 0) {
                int toWrite = Math.min(buffer.length, remaining);
                rng.nextBytes(buffer);
                fos.write(buffer, 0, toWrite);
                remaining -= toWrite;
            }
            fos.flush();
        }
        return tempFile;
    }
}
