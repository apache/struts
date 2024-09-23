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

import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlFileInput;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlSubmitInput;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.security.SecureRandom;

import static org.assertj.core.api.Assertions.assertThat;

public class FileUploadTest {

    @Test
    public void testSimpleFileUpload() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            final HtmlPage page = webClient.getPage(ParameterUtils.getBaseUrl() + "/fileupload/doUpload.action");
            final HtmlForm form = page.getFormByName("doUpload");
            HtmlInput captionInput = form.getInputByName("caption");
            HtmlFileInput uploadInput = form.getInputByName("upload");
            captionInput.type("some caption");
            File tempFile = File.createTempFile("testEmptyFile", ".txt");
            tempFile.deleteOnExit();

            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.append("Some strings");
                writer.flush();
            }

            uploadInput.setValue(tempFile.getAbsolutePath());
            final HtmlSubmitInput button = form.getInputByValue("Submit");
            final HtmlPage resultPage = button.click();

            String content = resultPage.getVisibleText();
            assertThat(content).contains(
                    "ContentType: text/plain",
                    "Original FileName: " + tempFile.getName(),
                    "Caption: some caption",
                    "Size: 12",
                    "Input name: upload"
            );
        }
    }

    @Test
    public void testUploadOverMaxSize() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            final HtmlPage page = webClient.getPage(ParameterUtils.getBaseUrl() + "/fileupload/doUpload.action");
            final HtmlForm form = page.getFormByName("doUpload");
            HtmlInput captionInput = form.getInputByName("caption");
            HtmlFileInput uploadInput = form.getInputByName("upload");

            captionInput.type("Large file");

            File tempFile = File.createTempFile("testEmptyFile", ".txt");
            SecureRandom rng = new SecureRandom();
            tempFile.deleteOnExit();
            try (FileWriter writer = new FileWriter(tempFile)) {
                for (int i = 0; i < 10240; ++i) {
                    String line = String.format("%s %s%n", rng.nextInt(), rng.nextInt());
                    writer.append(line);
                }
                writer.flush();
            }

            uploadInput.setValue(tempFile.getAbsolutePath());
            final HtmlSubmitInput button = form.getInputByValue("Submit");
            final HtmlPage resultPage = button.click();

            String content = resultPage.getVisibleText();
            System.out.println(content);
            assertThat(content).contains(
                    "Request exceeded allowed size limit! Max size allowed is: 10,240!"
            );
        }
    }

}
