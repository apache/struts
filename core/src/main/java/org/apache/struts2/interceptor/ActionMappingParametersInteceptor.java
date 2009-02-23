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

package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.interceptor.ParametersInterceptor;
import com.opensymphony.xwork2.ActionContext;

import java.util.Map;
import java.util.Collections;
import java.util.TreeMap;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

/**
 * <!-- START SNIPPET: description -->
 * This interceptor sets all parameters from the action mapping, for this request, on the value stack.  It operates
 * exactly like {@link ParametersInterceptor}, only the parameters come from the {@link ActionMapping}, not the
 * {@link ActionContext#getParameters()} method.
 * <!-- END SNIPPET: description -->
 * <p/>
 * <p/> <u>Interceptor parameters:</u>
 * <p/>
 * <!-- START SNIPPET: parameters -->
 * <p/>
 * <ul>
 * <p/>
 * <li>ordered - set to true if you want the top-down property setter behaviour</li>
 * <p/>
 * </ul>
 * <p/>
 * <!-- END SNIPPET: parameters -->
 * <p/>
 * <p/> <u>Extending the interceptor:</u>
 * <p/>
 * <!-- START SNIPPET: extending -->
 * <p/>
 * <p/> The best way to add behavior to this interceptor is to utilize the {@link com.opensymphony.xwork2.interceptor.ParameterNameAware} interface in your
 * actions. However, if you wish to apply a global rule that isn't implemented in your action, then you could extend
 * this interceptor and override the {@link #acceptableName(String)} method.
 * <p/>
 * <!-- END SNIPPET: extending -->
 * <p/>
 * <p/> <u>Example code:</u>
 * <p/>
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="mappingParams"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 */
public class ActionMappingParametersInteceptor extends ParametersInterceptor {

    /**
     * @param ac The action context
     * @return the parameters from the action mapping in the context.  If none found, returns
     *         an empty map.
     */
    @Override
    protected Map<String, Object> retrieveParameters(ActionContext ac) {
        ActionMapping mapping = (ActionMapping) ac.get(ServletActionContext.ACTION_MAPPING);
        if (mapping != null) {
            return mapping.getParams();
        } else {
            return Collections.emptyMap();
        }
    }

    /**
     * Adds the parameters into context's ParameterMap
     *
     * @param ac        The action context
     * @param newParams The parameter map to apply
     *                  <p/>
     *                  In this class this is a no-op, since the parameters were fetched from the same location.
     *                  In subclasses both retrieveParameters() and addParametersToContext() should be overridden.
     */
    @Override
    protected void addParametersToContext(ActionContext ac, Map newParams) {
        Map previousParams = ac.getParameters();
        Map combinedParams = null;
        if (previousParams != null) {
            combinedParams = new TreeMap(previousParams);
        } else {
            combinedParams = new TreeMap();
        }
        combinedParams.putAll(newParams);

        ac.setParameters(combinedParams);
    }
}
