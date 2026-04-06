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
package org.apache.struts2.rest.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ActionInvocation;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.interceptor.parameter.StrutsParameter;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Handles JSON content using jackson-lib
 */
public class JacksonJsonHandler implements ContentTypeHandler {

    private static final Logger LOG = LogManager.getLogger(JacksonJsonHandler.class);
    private static final String DEFAULT_CONTENT_TYPE = "application/json";
    private String defaultEncoding = "ISO-8859-1";
    private ObjectMapper mapper = new ObjectMapper();
    private boolean requireAnnotations = false;

    @Inject(value = StrutsConstants.STRUTS_PARAMETERS_REQUIRE_ANNOTATIONS, required = false)
    public void setRequireAnnotations(String requireAnnotations) {
        this.requireAnnotations = org.apache.commons.lang3.BooleanUtils.toBoolean(requireAnnotations);
    }

    @Override
    public void toObject(ActionInvocation invocation, Reader in, Object target) throws IOException {
        mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        if (requireAnnotations) {
            // Deserialize into a map first, then filter by @StrutsParameter annotation
            @SuppressWarnings("unchecked")
            Map<String, Object> jsonMap = mapper.readValue(in, Map.class);
            applyAnnotatedProperties(target, jsonMap);
        } else {
            ObjectReader or = mapper.readerForUpdating(target);
            or.readValue(in);
        }
    }

    /**
     * Sets only properties whose setter method is annotated with @StrutsParameter,
     * consistent with ParametersInterceptor behavior for URL parameters.
     */
    private void applyAnnotatedProperties(Object target, Map<String, Object> jsonMap) {
        try {
            BeanInfo info = Introspector.getBeanInfo(target.getClass());
            PropertyDescriptor[] props = info.getPropertyDescriptors();
            for (PropertyDescriptor prop : props) {
                String name = prop.getName();
                if (!jsonMap.containsKey(name)) {
                    continue;
                }
                Method setter = prop.getWriteMethod();
                if (setter == null) {
                    continue;
                }
                if (setter.getAnnotation(StrutsParameter.class) == null) {
                    LOG.debug("REST JSON property '{}' rejected: setter [{}] missing @StrutsParameter annotation",
                            name, setter.getName());
                    continue;
                }
                // Use Jackson to convert the value to the correct type and set it
                Object value = jsonMap.get(name);
                Object converted = mapper.convertValue(value, setter.getParameterTypes()[0]);
                setter.invoke(target, converted);
            }
        } catch (Exception e) {
            LOG.error("Error applying annotated properties from JSON", e);
        }
    }

    @Override
    public String fromObject(ActionInvocation invocation, Object obj, String resultCode, Writer stream) throws IOException {
        mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        mapper.writeValue(stream, obj);
        return null;
    }

    @Override
    public String getContentType() {
        return DEFAULT_CONTENT_TYPE + ";charset=" + this.defaultEncoding;
    }

    @Override
    public String getExtension() {
        return "json";
    }

    @Inject(StrutsConstants.STRUTS_I18N_ENCODING)
    public void setDefaultEncoding(String val) {
        this.defaultEncoding = val;
    }
}
