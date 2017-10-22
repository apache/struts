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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.dispatcher.LocalizedMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.DelegatingServletInputStream;
import org.testng.Assert;

public class JakartaStreamMultiPartRequestTest {

    private JakartaStreamMultiPartRequest multiPart;
    private Path tempDir;
    
    @Before
    public void initialize() {
        multiPart = new JakartaStreamMultiPartRequest();
        tempDir = Paths.get("target", "multi-part-test");
    }
    
    /**
     * Number of bytes in files greater than 2GB overflow the {@code int} primative.
     * The {@link HttpServletRequest#getContentLength()} returns {@literal -1} 
     * when the header is not present or the size is greater than {@link Integer#MAX_VALUE}.
     * @throws IOException 
     */
    @Test
    public void unknownContentLength() throws IOException {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getContentType()).thenReturn("multipart/form-data; charset=utf-8; boundary=__X_BOUNDARY__");
        Mockito.when(request.getMethod()).thenReturn("POST");
        Mockito.when(request.getContentLength()).thenReturn(Integer.valueOf(-1));
        StringBuilder entity = new StringBuilder();
        entity.append("\r\n--__X_BOUNDARY__\r\n");
        entity.append("Content-Disposition: form-data; name=\"upload\"; filename=\"test.csv\"\r\n");
        entity.append("Content-Type: text/csv\r\n\r\n1,2\r\n\r\n");
        entity.append("--__X_BOUNDARY__\r\n");
        entity.append("Content-Disposition: form-data; name=\"upload2\"; filename=\"test2.csv\"\r\n");
        entity.append("Content-Type: text/csv\r\n\r\n3,4\r\n\r\n");
        entity.append("--__X_BOUNDARY__--\r\n");
        Mockito.when(request.getInputStream()).thenReturn(new DelegatingServletInputStream(new ByteArrayInputStream(entity.toString().getBytes(StandardCharsets.UTF_8))));
        multiPart.setMaxSize("4");
        multiPart.parse(request, tempDir.toString());
        LocalizedMessage next = multiPart.getErrors().iterator().next();
        Assert.assertEquals(next.getTextKey(), "struts.messages.upload.error.SizeLimitExceededException");
    }
}
