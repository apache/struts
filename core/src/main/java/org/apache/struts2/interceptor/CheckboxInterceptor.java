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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.Parameter;
import org.apache.struts2.dispatcher.HttpParameters;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <!-- START SNIPPET: description -->
 * <p>
 * Looks for a hidden identification field that specifies the original value of the checkbox.
 * If the checkbox isn't submitted, insert it into the parameters as if it was with the value
 * of 'false'.
 * </p>
 * <!-- END SNIPPET: description -->
 *
 * <!-- START SNIPPET: parameters -->
 * <ul>
 * <li>setUncheckedValue - The default value of an unchecked box can be overridden by setting the 'uncheckedValue' property.</li>
 * </ul>
 * <!-- END SNIPPET: parameters -->
 *
 * <!-- START SNIPPET: extending -->
 *
 * <!-- END SNIPPET: extending -->
 */
public class CheckboxInterceptor extends AbstractInterceptor {

    /** Auto-generated serialization id */
    private static final long serialVersionUID = -586878104807229585L;

    private String uncheckedValue = Boolean.FALSE.toString();

    private static final Logger LOG = LogManager.getLogger(CheckboxInterceptor.class);

    public String intercept(ActionInvocation ai) throws Exception {
        HttpParameters parameters = ai.getInvocationContext().getParameters();
        Map<String, Parameter> extraParams = new HashMap<>();

        Set<String> checkboxParameters = new HashSet<>();
        for (Map.Entry<String, Parameter> parameter : parameters.entrySet()) {
            String name = parameter.getKey();
            if (name.startsWith("__checkbox_")) {
                String checkboxName = name.substring("__checkbox_".length());

                Parameter value = parameter.getValue();
                checkboxParameters.add(name);
                if (value.isMultiple()) {
                    LOG.debug("Bypassing automatic checkbox detection due to multiple checkboxes of the same name: {}", name);
                    continue;
                }

                // is this checkbox checked/submitted?
                if (!parameters.contains(checkboxName)) {
                    // if not, let's be sure to default the value to false
                    extraParams.put(checkboxName, new Parameter.Request(checkboxName, uncheckedValue));
                }
            }
        }
        parameters.remove(checkboxParameters);

        ai.getInvocationContext().getParameters().appendAll(extraParams);

        return ai.invoke();
    }

    /**
     * Overrides the default value for an unchecked checkbox
     *
     * @param uncheckedValue The uncheckedValue to set
     */
    public void setUncheckedValue(String uncheckedValue) {
        this.uncheckedValue = uncheckedValue;
    }
}
