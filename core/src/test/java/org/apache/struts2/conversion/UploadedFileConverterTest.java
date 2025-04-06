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
package org.apache.struts2.conversion;

import org.apache.struts2.dispatcher.multipart.StrutsUploadedFile;
import org.apache.struts2.dispatcher.multipart.UploadedFile;
import org.apache.struts2.ognl.StrutsContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Member;

import static org.assertj.core.api.Assertions.assertThat;

public class UploadedFileConverterTest {

    private StrutsContext context;
    private Class<?> target;
    private Member member;
    private String propertyName;
    private File tempFile;
    private String contentType;
    private String originalName;

    @Before
    public void setUp() throws Exception {
        context = StrutsContext.empty();
        target = File.class;
        member = File.class.getMethod("length");
        propertyName = "ignore";
        tempFile = File.createTempFile("struts", "test");
        contentType = "text/plain";
        originalName = tempFile.getName();
    }

    @After
    public void tearDown() throws Exception {
        tempFile.delete();
    }

    @Test
    public void convertUploadedFileToFile() {
        // given
        UploadedFileConverter ufc = new UploadedFileConverter();
        UploadedFile uploadedFile = StrutsUploadedFile.Builder.create(tempFile).withContentType(this.contentType).withOriginalName(this.originalName).build();

        // when
        Object result = ufc.convertValue(context, target, member, propertyName, uploadedFile, File.class);

        // then
        assertThat(result).isInstanceOf(File.class);
        File file = (File) result;
        assertThat(file.length()).isEqualTo(tempFile.length());
        assertThat(file.getAbsolutePath()).isEqualTo(tempFile.getAbsolutePath());
    }

    @Test
    public void convertUploadedFileArrayToFile() {
        // given
        UploadedFileConverter ufc = new UploadedFileConverter();
        UploadedFile[] uploadedFile = new UploadedFile[]{
                StrutsUploadedFile.Builder.create(tempFile)
                        .withContentType(this.contentType)
                        .withOriginalName(this.originalName)
                        .build()
        };

        // when
        Object result = ufc.convertValue(context, target, member, propertyName, uploadedFile, File.class);

        // then
        assertThat(result).isInstanceOf(File.class);
        File file = (File) result;
        assertThat(file.length()).isEqualTo(tempFile.length());
        assertThat(file.getAbsolutePath()).isEqualTo(tempFile.getAbsolutePath());
    }

}