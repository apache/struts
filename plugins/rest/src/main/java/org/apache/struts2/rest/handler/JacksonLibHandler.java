/*
 * $Id: JsonLibHandler.java 1097172 2011-04-27 16:36:54Z jogep $
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

package org.apache.struts2.rest.handler;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import org.apache.struts2.StrutsConstants;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * Handles JSON content using jackson-lib
 */
public class JacksonLibHandler extends AbstractContentTypeHandler {

    private static final String DEFAULT_CONTENT_TYPE = "application/json";
    private String defaultEncoding = "ISO-8859-1";
    private ObjectMapper mapper = new ObjectMapper();

    public void toObject(ActionInvocation invocation, Reader in, Object target) throws IOException {
        mapper.configure(Feature.WRITE_NULL_MAP_VALUES, false);
        ObjectReader or = mapper.readerForUpdating(target);
        or.readValue(in);
    }

    public String fromObject(ActionInvocation invocation, Object obj, String resultCode, Writer stream) throws IOException {
        mapper.configure(Feature.WRITE_NULL_MAP_VALUES, false);
        mapper.writeValue(stream, obj);
        return null;
    }

    public String getContentType() {
        return DEFAULT_CONTENT_TYPE + ";charset=" + this.defaultEncoding;
    }

    public String getExtension() {
        return "json";
    }

    @Inject(StrutsConstants.STRUTS_I18N_ENCODING)
    public void setDefaultEncoding(String val) {
        this.defaultEncoding = val;
    }
}
