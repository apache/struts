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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

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

}
