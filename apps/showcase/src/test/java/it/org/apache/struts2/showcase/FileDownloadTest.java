/*
 * $Id$
 *
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;

public class FileDownloadTest {
    @Test
    public void testImage() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            final Page page = webClient.getPage(ParameterUtils.getBaseUrl() + "/filedownload/download.action");

            URL url = new URL(
                    "https://gitbox.apache.org/repos/asf?p=struts.git;a=blob_plain;f=apps/showcase/src/main/webapp/images/struts.gif;hb=HEAD");

            Assert.assertTrue(areFilesEqual(url.openStream(), page.getWebResponse().getContentAsStream()));
        }
    }

    public void testZip() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            final Page page = webClient.getPage(ParameterUtils.getBaseUrl() + "/filedownload/download2.action");

            URL url = new URL(
                    "https://gitbox.apache.org/repos/asf?p=struts.git;a=blob_plain;f=apps/showcase/src/main/webapp/images/struts-gif.zip;hb=HEAD");

            Assert.assertTrue(areFilesEqual(url.openStream(), page.getWebResponse().getContentAsStream()));
        }
    }

    private boolean areFilesEqual(InputStream i1, InputStream i2) throws IOException {
        // read and compare bytes pair-wise
        int b1, b2;
        do {
            b1 = i1.read();
            b2 = i2.read();
        } while (b1 == b2 && b1 != -1 && b2 != -1);
        i1.close();
        i2.close();
        // true only if end of file is reached for both
        return (b1 == -1) && (b2 == -1);
    }
}
