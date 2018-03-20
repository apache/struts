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
package org.apache.struts2.rest.config.entities;

import java.util.Map;
import java.util.Objects;

import org.apache.struts2.config.entities.ConstantConfig;
import org.apache.struts2.rest.RestConstants;

public class RestConstantConfig extends ConstantConfig {
    private String restDefaultExtension;
    private Boolean restLogger;
    private String restDefaultErrorResultName;
    private Boolean restContentRestrictToGet;
    private String mapperIndexMethodName;
    private String mapperGetMethodName;
    private String mapperPostMethodName;
    private String mapperEditMethodName;
    private String mapperNewMethodName;
    private String mapperDeleteMethodName;
    private String mapperPutMethodName;
    private String mapperOptionsMethodName;
    private String mapperPostContinueMethodName;
    private String mapperPutContinueMethodName;
    private String restNamespace;
    private String restValidationFailureStatusCode;

    @Override
    public Map<String, String> getAllAsStringsMap() {
        Map<String, String> map = super.getAllAsStringsMap();

        map.put(RestConstants.REST_DEFAULT_EXTENSION, restDefaultExtension);
        map.put(RestConstants.REST_LOGGER, Objects.toString(restLogger, null));
        map.put(RestConstants.REST_DEFAULT_ERROR_RESULT_NAME, restDefaultErrorResultName);
        map.put(RestConstants.REST_CONTENT_RESTRICT_TO_GET, Objects.toString(restContentRestrictToGet, null));
        map.put(RestConstants.REST_MAPPER_INDEX_METHOD_NAME, mapperIndexMethodName);
        map.put(RestConstants.REST_MAPPER_GET_METHOD_NAME, mapperGetMethodName);
        map.put(RestConstants.REST_MAPPER_POST_METHOD_NAME, mapperPostMethodName);
        map.put(RestConstants.REST_MAPPER_EDIT_METHOD_NAME, mapperEditMethodName);
        map.put(RestConstants.REST_MAPPER_NEW_METHOD_NAME, mapperNewMethodName);
        map.put(RestConstants.REST_MAPPER_DELETE_METHOD_NAME, mapperDeleteMethodName);
        map.put(RestConstants.REST_MAPPER_PUT_METHOD_NAME, mapperPutMethodName);
        map.put(RestConstants.REST_MAPPER_OPTIONS_METHOD_NAME, mapperOptionsMethodName);
        map.put(RestConstants.REST_MAPPER_POST_CONTINUE_METHOD_NAME, mapperPostContinueMethodName);
        map.put(RestConstants.REST_MAPPER_PUT_CONTINUE_METHOD_NAME, mapperPutContinueMethodName);
        map.put(RestConstants.STRUTS_REST_NAMESPACE, restNamespace);
        map.put(RestConstants.REST_VALIDATION_FAILURE_STATUS_CODE, restValidationFailureStatusCode);

        return map;
    }

    public String getRestDefaultExtension() {
        return restDefaultExtension;
    }

    public void setRestDefaultExtension(String restDefaultExtension) {
        this.restDefaultExtension = restDefaultExtension;
    }

    public Boolean getRestLogger() {
        return restLogger;
    }

    public void setRestLogger(Boolean restLogger) {
        this.restLogger = restLogger;
    }

    public String getRestDefaultErrorResultName() {
        return restDefaultErrorResultName;
    }

    public void setRestDefaultErrorResultName(String restDefaultErrorResultName) {
        this.restDefaultErrorResultName = restDefaultErrorResultName;
    }

    public Boolean getRestContentRestrictToGet() {
        return restContentRestrictToGet;
    }

    public void setRestContentRestrictToGet(Boolean restContentRestrictToGet) {
        this.restContentRestrictToGet = restContentRestrictToGet;
    }

    public String getMapperIndexMethodName() {
        return mapperIndexMethodName;
    }

    public void setMapperIndexMethodName(String mapperIndexMethodName) {
        this.mapperIndexMethodName = mapperIndexMethodName;
    }

    public String getMapperGetMethodName() {
        return mapperGetMethodName;
    }

    public void setMapperGetMethodName(String mapperGetMethodName) {
        this.mapperGetMethodName = mapperGetMethodName;
    }

    public String getMapperPostMethodName() {
        return mapperPostMethodName;
    }

    public void setMapperPostMethodName(String mapperPostMethodName) {
        this.mapperPostMethodName = mapperPostMethodName;
    }

    public String getMapperEditMethodName() {
        return mapperEditMethodName;
    }

    public void setMapperEditMethodName(String mapperEditMethodName) {
        this.mapperEditMethodName = mapperEditMethodName;
    }

    public String getMapperNewMethodName() {
        return mapperNewMethodName;
    }

    public void setMapperNewMethodName(String mapperNewMethodName) {
        this.mapperNewMethodName = mapperNewMethodName;
    }

    public String getMapperDeleteMethodName() {
        return mapperDeleteMethodName;
    }

    public void setMapperDeleteMethodName(String mapperDeleteMethodName) {
        this.mapperDeleteMethodName = mapperDeleteMethodName;
    }

    public String getMapperPutMethodName() {
        return mapperPutMethodName;
    }

    public void setMapperPutMethodName(String mapperPutMethodName) {
        this.mapperPutMethodName = mapperPutMethodName;
    }

    public String getMapperOptionsMethodName() {
        return mapperOptionsMethodName;
    }

    public void setMapperOptionsMethodName(String mapperOptionsMethodName) {
        this.mapperOptionsMethodName = mapperOptionsMethodName;
    }

    public String getMapperPostContinueMethodName() {
        return mapperPostContinueMethodName;
    }

    public void setMapperPostContinueMethodName(String mapperPostContinueMethodName) {
        this.mapperPostContinueMethodName = mapperPostContinueMethodName;
    }

    public String getMapperPutContinueMethodName() {
        return mapperPutContinueMethodName;
    }

    public void setMapperPutContinueMethodName(String mapperPutContinueMethodName) {
        this.mapperPutContinueMethodName = mapperPutContinueMethodName;
    }

    public String getRestNamespace() {
        return restNamespace;
    }

    public void setRestNamespace(String restNamespace) {
        this.restNamespace = restNamespace;
    }

    public String getRestValidationFailureStatusCode() {
        return restValidationFailureStatusCode;
    }

    public void setRestValidationFailureStatusCode(String restValidationFailureStatusCode) {
        this.restValidationFailureStatusCode = restValidationFailureStatusCode;
    }
}
