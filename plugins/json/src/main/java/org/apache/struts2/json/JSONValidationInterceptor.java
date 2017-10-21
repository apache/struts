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
package org.apache.struts2.json;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Serializes validation and action errors into JSON. This interceptor does not
 * perform any validation, so it must follow the 'validation' interceptor on the stack.
 */
public class JSONValidationInterceptor extends MethodFilterInterceptor {

    private static final Logger LOG = LogManager.getLogger(JSONValidationInterceptor.class);

    public static final String VALIDATE_ONLY_PARAM = "struts.validateOnly";
    public static final String VALIDATE_JSON_PARAM = "struts.enableJSONValidation";
    public static final String NO_ENCODING_SET_PARAM = "struts.JSONValidation.no.encoding";

    public static final String DEFAULT_ENCODING = "UTF-8";

    private int validationFailedStatus = HttpServletResponse.SC_BAD_REQUEST;
    private String validateOnlyParam = VALIDATE_ONLY_PARAM;
    private String validateJsonParam = VALIDATE_JSON_PARAM;
    private String noEncodingSetParam = NO_ENCODING_SET_PARAM;

    @Override
    protected String doIntercept(ActionInvocation invocation) throws Exception {
        HttpServletResponse response = ServletActionContext.getResponse();
        HttpServletRequest request = ServletActionContext.getRequest();

        Object action = invocation.getAction();

        if (isJsonEnabled(request)) {
            if (action instanceof ValidationAware) {
                // generate json
                ValidationAware validationAware = (ValidationAware) action;
                if (validationAware.hasErrors()) {
                    return generateJSON(request, response, validationAware);
                }
            }
            if (isValidateOnly(request)) {
                //there were no errors
                setupEncoding(response, request);
                response.getWriter().print("{}");
                response.setContentType("application/json");
                return Action.NONE;
            } else {
                return invocation.invoke();
            }
        } else
            return invocation.invoke();
    }

    private void setupEncoding(HttpServletResponse response, HttpServletRequest request) {
        if (isSetEncoding(request)) {
        	LOG.debug("Default encoding not set!");
        } else {
            LOG.debug("Setting up encoding to: [{}]!", DEFAULT_ENCODING);
            response.setCharacterEncoding(DEFAULT_ENCODING);
        }
    }

    private String generateJSON(HttpServletRequest request, HttpServletResponse response, ValidationAware validationAware)
            throws IOException {
        if (validationFailedStatus >= 0) {
            response.setStatus(validationFailedStatus);
        }
        setupEncoding(response, request);
        response.getWriter().print(buildResponse(validationAware));
        response.setContentType("application/json");
        return Action.NONE;
    }

    public boolean isJsonEnabled(HttpServletRequest request) {
        return Boolean.parseBoolean(request.getParameter(validateJsonParam));
    }

    public boolean isValidateOnly(HttpServletRequest request) {
        return Boolean.parseBoolean(request.getParameter(validateOnlyParam));
    }

    public boolean isSetEncoding(HttpServletRequest request) {
        return Boolean.parseBoolean(request.getParameter(noEncodingSetParam));
    }

    /**
     * @param validationAware the validation aware object
     * @return JSON string that contains the errors and field errors
     */
    @SuppressWarnings("unchecked")
    protected String buildResponse(ValidationAware validationAware) {
        //should we use FreeMarker here?
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");

        if (validationAware.hasErrors()) {
            //action errors
            if (validationAware.hasActionErrors()) {
                sb.append("\"errors\":");
                sb.append(buildArray(validationAware.getActionErrors()));
            }

            //field errors
            if (validationAware.hasFieldErrors()) {
                if (validationAware.hasActionErrors())
                    sb.append(",");
                sb.append("\"fieldErrors\": {");
                Map<String, List<String>> fieldErrors = validationAware
                    .getFieldErrors();
                for (Map.Entry<String, List<String>> fieldError : fieldErrors
                    .entrySet()) {
                    sb.append("\"");
                    //if it is model driven, remove "model." see WW-2721
                    String fieldErrorKey = fieldError.getKey();
                    sb.append(((validationAware instanceof ModelDriven) &&  fieldErrorKey.startsWith("model."))? fieldErrorKey.substring(6)
                            : fieldErrorKey);
                    sb.append("\":");
                    sb.append(buildArray(fieldError.getValue()));
                    sb.append(",");
                }
                //remove trailing comma, IE creates an empty object, duh
                sb.deleteCharAt(sb.length() - 1);
                sb.append("}");
            }
        }

        sb.append("}");
        /*response should be something like:
         * {
         *      "errors": ["this", "that"],
         *      "fieldErrors": {
         *            field1: "this",
         *            field2: "that"
         *      }
         * }
         */
        return sb.toString();
    }

    private String buildArray(Collection<String> values) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (String value : values) {
            sb.append("\"");
            sb.append(StringEscapeUtils.escapeJson(value));
            sb.append("\",");
        }
        if (values.size() > 0)
            sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }

    /**
     * HTTP status that will be set in the response if validation fails
     *
     * @param validationFailedStatus validation failed status
     */
    public void setValidationFailedStatus(int validationFailedStatus) {
        this.validationFailedStatus = validationFailedStatus;
    }

    /**
     * Overrides 'struts.validateOnly' param name
     *
     * @param validateOnlyParam new param name
     */
    public void setValidateOnlyParam(String validateOnlyParam) {
        this.validateOnlyParam = validateOnlyParam;
    }

    /**
     * Overrides 'struts.enableJSONValidation' param name
     *
     * @param validateJsonParam new param name
     */
    public void setValidateJsonParam(String validateJsonParam) {
        this.validateJsonParam = validateJsonParam;
    }

    /**
     * Overrides 'struts.JSONValidation.no.encoding' param name
     *
     * @param noEncodingSetParam new param name
     */
    public void setNoEncodingSetParam(String noEncodingSetParam) {
        this.noEncodingSetParam = noEncodingSetParam;
    }
}
