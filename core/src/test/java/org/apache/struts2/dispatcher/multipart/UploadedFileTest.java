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

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class UploadedFileTest {

    @Test
    public void defaultGetInputStreamReadsByteArrayContent() throws IOException {
        UploadedFile file = new UploadedFile() {
            public Long length() { return 3L; }
            public String getName() { return "x"; }
            public String getOriginalName() { return "x"; }
            public boolean isFile() { return false; }
            public boolean delete() { return true; }
            public String getAbsolutePath() { return null; }
            public Object getContent() { return "abc".getBytes(UTF_8); }
            public String getContentType() { return "text/plain"; }
            public String getInputName() { return "file"; }
        };

        try (InputStream in = file.getInputStream()) {
            assertThat(new String(in.readAllBytes(), UTF_8)).isEqualTo("abc");
        }
    }

    @Test
    public void defaultIsMissingReflectsContent() {
        assertThat(uploadedFileReturning("abc".getBytes(UTF_8)).isMissing()).isFalse();
        assertThat(uploadedFileReturning(null).isMissing()).isTrue();
    }

    private static UploadedFile uploadedFileReturning(Object content) {
        return new UploadedFile() {
            public Long length() { return 0L; }
            public String getName() { return "x"; }
            public String getOriginalName() { return "x"; }
            public boolean isFile() { return false; }
            public boolean delete() { return true; }
            public String getAbsolutePath() { return null; }
            public Object getContent() { return content; }
            public String getContentType() { return "text/plain"; }
            public String getInputName() { return "file"; }
        };
    }
}
