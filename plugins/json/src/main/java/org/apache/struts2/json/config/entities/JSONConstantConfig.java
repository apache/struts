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
package org.apache.struts2.json.config.entities;

import java.util.Map;
import java.util.Objects;

import org.apache.struts2.config.entities.BeanConfig;
import org.apache.struts2.config.entities.ConstantConfig;
import org.apache.struts2.json.JSONConstants;

public class JSONConstantConfig extends ConstantConfig {
    private BeanConfig jsonWriter;
    private Boolean jsonResultExcludeProxyProperties;
    private String jsonDateFormat;

    @Override
    public Map<String, String> getAllAsStringsMap() {
        Map<String, String> map = super.getAllAsStringsMap();

        map.put(JSONConstants.JSON_WRITER, beanConfToString(jsonWriter));
        map.put(JSONConstants.RESULT_EXCLUDE_PROXY_PROPERTIES, Objects.toString(jsonResultExcludeProxyProperties, null));
        map.put(JSONConstants.DATE_FORMAT, jsonDateFormat);

        return map;
    }

    public BeanConfig getJsonWriter() {
        return jsonWriter;
    }

    public void setJsonWriter(BeanConfig jsonWriter) {
        this.jsonWriter = jsonWriter;
    }

    public void setJsonWriter(Class<?> clazz) {
        this.jsonWriter = new BeanConfig(clazz, clazz.getName());
    }

    public Boolean getJsonResultExcludeProxyProperties() {
        return jsonResultExcludeProxyProperties;
    }

    public void setJsonResultExcludeProxyProperties(Boolean jsonResultExcludeProxyProperties) {
        this.jsonResultExcludeProxyProperties = jsonResultExcludeProxyProperties;
    }

    public String getJsonDateFormat() {
        return jsonDateFormat;
    }

    public void setJsonDateFormat(String jsonDateFormat) {
        this.jsonDateFormat = jsonDateFormat;
    }
}
