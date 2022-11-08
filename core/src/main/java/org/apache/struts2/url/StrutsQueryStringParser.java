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

import com.opensymphony.xwork2.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
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
    public Map<String, Object> parse(String queryString, boolean forceValueArray) {
        if (StringUtils.isEmpty(queryString)) {
            LOG.debug("Query String is empty, returning an empty map");
            return Collections.emptyMap();
        }

        Map<String, Object> queryParams = new LinkedHashMap<>();
        String[] params = queryString.split("&");
        for (String param : params) {
            if (StringUtils.isBlank(param)) {
                LOG.debug("Param [{}] is blank, skipping", param);
                continue;
            }

            String[] tmpParams = param.split("=");
            String paramName = null;
            String paramValue = "";
            if (tmpParams.length > 0) {
                paramName = tmpParams[0];
            }
            if (tmpParams.length > 1) {
                paramValue = tmpParams[1];
            }
            if (paramName != null) {
                extractParam(paramName, paramValue, queryParams, forceValueArray);
            }
        }
        return queryParams;
    }

    private void extractParam(String paramName, String paramValue, Map<String, Object> queryParams, boolean forceValueArray) {
        String decodedParamName = decoder.decode(paramName, true);
        String decodedParamValue = decoder.decode(paramValue, true);

        if (queryParams.containsKey(decodedParamName) || forceValueArray) {
            // WW-1619 append new param value to existing value(s)
            Object currentParam = queryParams.get(decodedParamName);
            if (currentParam instanceof String) {
                queryParams.put(decodedParamName, new String[]{(String) currentParam, decodedParamValue});
            } else {
                String[] currentParamValues = (String[]) currentParam;
                if (currentParamValues != null) {
                    List<String> paramList = new ArrayList<>(Arrays.asList(currentParamValues));
                    paramList.add(decodedParamValue);
                    queryParams.put(decodedParamName, paramList.toArray(new String[0]));
                } else {
                    queryParams.put(decodedParamName, new String[]{decodedParamValue});
                }
            }
        } else {
            queryParams.put(decodedParamName, decodedParamValue);
        }
    }
}
