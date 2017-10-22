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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Member;
import java.util.Collections;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

public class UploadedFileConverterTest {

    private Map<String, Object> context;
    private Class target;
    private Member member;
    private String propertyName;
    private File tempFile;

    @Before
    public void setUp() throws Exception {
        context = Collections.emptyMap();
        target = File.class;
        member = File.class.getMethod("length");
        propertyName = "ignore";
        tempFile = File.createTempFile("struts", "test");
    }

    @After
    public void tearDown() throws Exception {
        tempFile.delete();
    }

    @Test
    public void convertUploadedFileToFile() throws Exception {
        // given
        UploadedFileConverter ufc = new UploadedFileConverter();
        UploadedFile uploadedFile = new StrutsUploadedFile(tempFile);

        // when
        Object result = ufc.convertValue(context, target, member, propertyName, uploadedFile, File.class);

        // then
        assertThat(result).isInstanceOf(File.class);
        File file = (File) result;
        assertThat(file.length()).isEqualTo(tempFile.length());
        assertThat(file.getAbsolutePath()).isEqualTo(tempFile.getAbsolutePath());
    }

    @Test
    public void convertUploadedFileArrayToFile() throws Exception {
        // given
        UploadedFileConverter ufc = new UploadedFileConverter();
        UploadedFile[] uploadedFile = new UploadedFile[] { new StrutsUploadedFile(tempFile) };

        // when
        Object result = ufc.convertValue(context, target, member, propertyName, uploadedFile, File.class);

        // then
        assertThat(result).isInstanceOf(File.class);
        File file = (File) result;
        assertThat(file.length()).isEqualTo(tempFile.length());
        assertThat(file.getAbsolutePath()).isEqualTo(tempFile.getAbsolutePath());
    }

}