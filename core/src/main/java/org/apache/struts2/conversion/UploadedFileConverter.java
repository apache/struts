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

import com.opensymphony.xwork2.conversion.impl.DefaultTypeConverter;
import org.apache.struts2.dispatcher.multipart.UploadedFile;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.util.Map;

public class UploadedFileConverter extends DefaultTypeConverter {

    @Override
    public Object convertValue(Map<String, Object> context, Object target, Member member, String propertyName, Object value, Class toType) {
        if (File.class.equals(toType)) {
            if (value.getClass().isArray() && Array.getLength(value) == 1) {
                Object obj = Array.get(value, 0);
                if (obj instanceof UploadedFile) {
                    UploadedFile file = (UploadedFile) obj;
                    if (file.getContent() instanceof File) {
                        return file.getContent();
                    }
                    return new File(file.getAbsolutePath());
                }
            }
        }

        return super.convertValue(context, target, member, propertyName, value, toType);
    }

}
