/*
 * $Id$
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
package org.apache.struts2.interceptor.validation;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ValidationAware;

/**
 * <p>Extends the annotations validator and returns a JSON string with the
 * validation errors. If validation succeeds the action is invoked.</p> 
 * 
 * <p>If 'validationFailedStatus' is set it will be used as the Response status
 * when validation fails.</p>
 * 
 * <p>If the request has a parameter 'struts.validateOnly' execution will return after 
 * validation (action won't be executed).</p>
 */
public class JSONValidationInterceptor extends AnnotationValidationInterceptor {
    private static final Log LOG = LogFactory
        .getLog(JSONValidationInterceptor.class);
    private static final String VALIDATE_ONLY_PARAM = "struts.validateOnly";
    static char[] hex = "0123456789ABCDEF".toCharArray();

    private int validationFailedStatus = -1;

    /**
     * HTTP status that will be set in the response if validation fails
     * @param validationFailedStatus
     */
    public void setValidationFailedStatus(int validationFailedStatus) {
        this.validationFailedStatus = validationFailedStatus;
    }

    @Override
    protected String doIntercept(ActionInvocation invocation) throws Exception {
        //validate
        doBeforeInvocation(invocation);

        HttpServletResponse response = ServletActionContext.getResponse();
        HttpServletRequest request = ServletActionContext.getRequest();

        Object action = invocation.getAction();

        if (action instanceof ValidationAware) {
            // generate json
            ValidationAware validationAware = (ValidationAware) action;
            if (validationAware.hasErrors()) {
                if (validationFailedStatus >= 0)
                    response.setStatus(validationFailedStatus);
                response.getWriter().print(buildResponse(validationAware));
                return Action.NONE;
            } 
        }

        String validateOnly = request.getParameter(VALIDATE_ONLY_PARAM);
        if (validateOnly != null && "true".equals(validateOnly)) {
            //there were no errors
            response.getWriter().print("/* {} */");
            return Action.NONE;
        } else {
            return invocation.invoke();
        }
    }

    /**
     * @return JSON string that contains the errors and field errors
     */
    @SuppressWarnings("unchecked")
    protected String buildResponse(ValidationAware validationAware) {
        //should we use FreeMarker here?
        StringBuilder sb = new StringBuilder();
        sb.append("/* { ");

        if (validationAware.hasErrors()) {
            //action errors
            if (validationAware.hasActionErrors()) {
                sb.append("\"errors\":");
                sb.append(buildArray(validationAware.getActionErrors()));
                sb.append(",");
            }

            //field errors
            if (validationAware.hasFieldErrors()) {
                sb.append("\"fieldErrors\": {");
                Map<String, List<String>> fieldErrors = validationAware
                    .getFieldErrors();
                for (Map.Entry<String, List<String>> fieldError : fieldErrors
                    .entrySet()) {
                    sb.append("\"");
                    sb.append(fieldError.getKey());
                    sb.append("\":");
                    sb.append(buildArray(fieldError.getValue()));
                    sb.append(",");
                }
                //remove trailing comma, IE creates an empty object, duh
                sb.deleteCharAt(sb.length() - 1);
                sb.append("}");
            }
        }

        sb.append("} */");
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
            sb.append(escapeJSON(value));
            sb.append("\",");
        }
        if (values.size() > 0)
            sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }

    private String escapeJSON(Object obj) {
        StringBuilder sb = new StringBuilder();

        CharacterIterator it = new StringCharacterIterator(obj.toString());

        for (char c = it.first(); c != CharacterIterator.DONE; c = it.next()) {
            if (c == '"') {
                sb.append("\\\"");
            } else if (c == '\\') {
                sb.append("\\\\");
            } else if (c == '/') {
                sb.append("\\/");
            } else if (c == '\b') {
                sb.append("\\b");
            } else if (c == '\f') {
                sb.append("\\f");
            } else if (c == '\n') {
                sb.append("\\n");
            } else if (c == '\r') {
                sb.append("\\r");
            } else if (c == '\t') {
                sb.append("\\t");
            } else if (Character.isISOControl(c)) {
                sb.append(unicode(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Represent as unicode
     * @param c character to be encoded
     */
    private String unicode(char c) {
        StringBuilder sb = new StringBuilder();
        sb.append("\\u");

        int n = c;

        for (int i = 0; i < 4; ++i) {
            int digit = (n & 0xf000) >> 12;

            sb.append(hex[digit]);
            n <<= 4;
        }
        return sb.toString();
    }

}
