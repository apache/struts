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
    private BeanConfig jsonReader;
    private Boolean jsonResultExcludeProxyProperties;
    private String jsonDateFormat;
    private Integer jsonMaxElements;
    private Integer jsonMaxDepth;
    private Integer jsonMaxLength;
    private Integer jsonMaxStringLength;
    private Integer jsonMaxKeyLength;

    @Override
    public Map<String, String> getAllAsStringsMap() {
        Map<String, String> map = super.getAllAsStringsMap();

        map.put(JSONConstants.JSON_WRITER, beanConfToString(jsonWriter));
        map.put(JSONConstants.JSON_READER, beanConfToString(jsonReader));
        map.put(JSONConstants.RESULT_EXCLUDE_PROXY_PROPERTIES, Objects.toString(jsonResultExcludeProxyProperties, null));
        map.put(JSONConstants.DATE_FORMAT, jsonDateFormat);
        map.put(JSONConstants.JSON_MAX_ELEMENTS, Objects.toString(jsonMaxElements, null));
        map.put(JSONConstants.JSON_MAX_DEPTH, Objects.toString(jsonMaxDepth, null));
        map.put(JSONConstants.JSON_MAX_LENGTH, Objects.toString(jsonMaxLength, null));
        map.put(JSONConstants.JSON_MAX_STRING_LENGTH, Objects.toString(jsonMaxStringLength, null));
        map.put(JSONConstants.JSON_MAX_KEY_LENGTH, Objects.toString(jsonMaxKeyLength, null));

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

    public BeanConfig getJsonReader() {
        return jsonReader;
    }

    public void setJsonReader(BeanConfig jsonReader) {
        this.jsonReader = jsonReader;
    }

    public void setJsonReader(Class<?> clazz) {
        this.jsonReader = new BeanConfig(clazz, clazz.getName());
    }

    public Integer getJsonMaxElements() {
        return jsonMaxElements;
    }

    public void setJsonMaxElements(Integer jsonMaxElements) {
        this.jsonMaxElements = jsonMaxElements;
    }

    public Integer getJsonMaxDepth() {
        return jsonMaxDepth;
    }

    public void setJsonMaxDepth(Integer jsonMaxDepth) {
        this.jsonMaxDepth = jsonMaxDepth;
    }

    public Integer getJsonMaxLength() {
        return jsonMaxLength;
    }

    public void setJsonMaxLength(Integer jsonMaxLength) {
        this.jsonMaxLength = jsonMaxLength;
    }

    public Integer getJsonMaxStringLength() {
        return jsonMaxStringLength;
    }

    public void setJsonMaxStringLength(Integer jsonMaxStringLength) {
        this.jsonMaxStringLength = jsonMaxStringLength;
    }

    public Integer getJsonMaxKeyLength() {
        return jsonMaxKeyLength;
    }

    public void setJsonMaxKeyLength(Integer jsonMaxKeyLength) {
        this.jsonMaxKeyLength = jsonMaxKeyLength;
    }
}
