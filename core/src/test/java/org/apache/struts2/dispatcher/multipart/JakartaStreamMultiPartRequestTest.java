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
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

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

}
