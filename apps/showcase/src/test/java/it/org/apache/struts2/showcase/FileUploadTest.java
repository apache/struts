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

import java.io.File;

import org.htmlunit.WebClient;
import org.htmlunit.html.DomElement;
import org.htmlunit.html.HtmlFileInput;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlSubmitInput;
import org.junit.Assert;
import org.junit.Test;

public class FileUploadTest {

    @Test
    public void testEmptyFile() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            final HtmlPage page = webClient.getPage(ParameterUtils.getBaseUrl() + "/fileupload/doUpload.action");
            final HtmlForm form = page.getFormByName("doUpload");
            HtmlInput captionInput = form.getInputByName("caption");
            HtmlFileInput uploadInput = form.getInputByName("upload");
            captionInput.type("some caption");
            File tempFile = File.createTempFile("testEmptyFile", ".tmp");
            tempFile.deleteOnExit();
            uploadInput.setValue(tempFile.getAbsolutePath());
            final HtmlSubmitInput button = form.getInputByValue("Submit");
            final HtmlPage resultPage = button.click();
            DomElement errorMessage = resultPage.getFirstByXPath("//span[@class='errorMessage']");
            Assert.assertNotNull(errorMessage);
            Assert.assertEquals("File cannot be empty", errorMessage.getVisibleText());
        }
    }

}
