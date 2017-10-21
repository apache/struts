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
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.Parameter;

import java.util.HashMap;
import java.util.Map;

/**
 * Just as the CheckboxInterceptor checks that if only the hidden field is present, so too does this interceptor. If
 * the "__multiselect_" request parameter is present and its visible counterpart is not, set a new request parameter to an
 * empty Sting.
 */
public class MultiselectInterceptor extends AbstractInterceptor {
    private static final long serialVersionUID = 1L;

    /**
     * Just as the CheckboxInterceptor checks that if only the hidden field is present, so too does this interceptor.
     * If the "__multiselect_" request parameter is present and its visible counterpart is not, set a new request parameter
     * to an empty Sting.
     *
     * @param ai ActionInvocation
     * @return the result of the action
     * @throws Exception if error
     * @see com.opensymphony.xwork2.interceptor.Interceptor#intercept(com.opensymphony.xwork2.ActionInvocation)
     */
    public String intercept(ActionInvocation ai) throws Exception {
        HttpParameters parameters = ai.getInvocationContext().getParameters();
        Map<String, Parameter> newParams = new HashMap<>();

        for (String name : parameters.keySet()) {
            if (name.startsWith("__multiselect_")) {
                String key = name.substring("__multiselect_".length());

                // is this multi-select box submitted?
                if (!parameters.contains(key)) {
                    // if not, let's be sure to default the value to an empty string array
                    newParams.put(key, new Parameter.Request(key, new String[0]));
                }

                parameters = parameters.remove(name);
            }
        }

        ai.getInvocationContext().getParameters().appendAll(newParams);

        return ai.invoke();
    }

}
