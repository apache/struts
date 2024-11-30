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
package org.apache.struts2.url;

import org.apache.struts2.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StrutsQueryStringParser implements QueryStringParser {

    private static final Logger LOG = LogManager.getLogger(StrutsQueryStringParser.class);

    private final UrlDecoder decoder;

    @Inject
    public StrutsQueryStringParser(UrlDecoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public Result parse(String queryString) {
        if (StringUtils.isEmpty(queryString)) {
            LOG.debug("Query String is empty, returning an empty map");
            return this.empty();
        }

        Result queryParams = StrutsQueryStringParserResult.create();
        String[] params = extractParams(queryString);
        for (String param : params) {
            if (StringUtils.isBlank(param)) {
                LOG.debug("Param [{}] is blank, skipping", param);
                continue;
            }
            String paramName;
            String paramValue = "";
            int index = param.indexOf("=");
            if (index > -1) {
                paramName = param.substring(0, index);
                paramValue = param.substring(index + 1);
            } else {
                paramName = param;
            }
            queryParams = extractParam(paramName, paramValue, queryParams);
        }
        return queryParams.withQueryFragment(extractFragment(queryString));
    }

    @Override
    public Result empty() {
        return new StrutsQueryStringParserResult(Collections.emptyMap(), "");
    }

    private String[] extractParams(String queryString) {
        LOG.trace("Extracting params from query string: {}", queryString);
        String[] params = queryString.split("&");

        int fragmentIndex = queryString.lastIndexOf("#");
        if (fragmentIndex > -1) {
            LOG.trace("Stripping fragment at index: {}", fragmentIndex);
            params = queryString.substring(0, fragmentIndex).split("&");
        }
        return params;
    }

    private Result extractParam(String paramName, String paramValue, Result queryParams) {
        String decodedParamName = decoder.decode(paramName, true);
        String decodedParamValue = decoder.decode(paramValue, true);
        return queryParams.addParam(decodedParamName, decodedParamValue);
    }

    private String extractFragment(String queryString) {
        int fragmentIndex = queryString.lastIndexOf("#");
        if (fragmentIndex > -1) {
            return queryString.substring(fragmentIndex + 1);
        }
        return "";
    }

    public static class StrutsQueryStringParserResult implements Result {

        private final Map<String, Object> queryParams;
        private String queryFragment;

        static Result create() {
            return new StrutsQueryStringParserResult(new LinkedHashMap<>(), "");
        }

        private StrutsQueryStringParserResult(Map<String, Object> queryParams, String queryFragment) {
            this.queryParams = queryParams;
            this.queryFragment = queryFragment;
        }

        public Result addParam(String name, String value) {
            if (queryParams.containsKey(name)) {
                // WW-1619 append new param value to existing value(s)
                Object currentParam = queryParams.get(name);
                if (currentParam instanceof String) {
                    queryParams.put(name, new String[]{(String) currentParam, value});
                } else {
                    String[] currentParamValues = (String[]) currentParam;
                    if (currentParamValues != null) {
                        List<String> paramList = new ArrayList<>(List.of(currentParamValues));
                        paramList.add(value);
                        queryParams.put(name, paramList.toArray(new String[0]));
                    } else {
                        queryParams.put(name, new String[]{value});
                    }
                }
            } else {
                queryParams.put(name, value);
            }

            return this;
        }

        public Result withQueryFragment(String queryFragment) {
            this.queryFragment = queryFragment;
            return this;
        }

        public Map<String, Object> getQueryParams() {
            return Collections.unmodifiableMap(queryParams);
        }

        public String getQueryFragment() {
            return queryFragment;
        }

        public boolean contains(String name) {
            return queryParams.containsKey(name);
        }

        public boolean isEmpty() {
            return queryParams.isEmpty();
        }
    }
}
