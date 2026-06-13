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
import org.apache.struts2.StrutsException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AbstractMultiPartRequestApiCheckTest {

    @Test
    public void verifyFileUploadApiPassesForCompatibleClass() {
        assertThatCode(() -> AbstractMultiPartRequest.verifyFileUploadApi(JakartaServletDiskFileUpload.class))
                .doesNotThrowAnyException();
    }

    @Test
    public void verifyFileUploadApiThrowsForIncompatibleClass() {
        assertThatThrownBy(() -> AbstractMultiPartRequest.verifyFileUploadApi(IncompatibleFileUpload.class))
                .isInstanceOf(StrutsException.class)
                .hasMessageContaining("setMaxSize")
                .hasMessageContaining("Align commons-fileupload2-core");
    }

    /** Stub lacking the size-limit setters, simulating a binary-incompatible fileupload version. */
    private static class IncompatibleFileUpload {
    }
}
