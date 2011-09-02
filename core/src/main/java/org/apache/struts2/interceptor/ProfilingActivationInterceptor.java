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

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.profiling.UtilTimerStack;

import org.apache.struts2.StrutsConstants;

/**
 * <!-- START SNIPPET: description -->
 *
 * Allows profiling to be enabled or disabled via request parameters, when
 * devMode is enabled.
 *
 * <!-- END SNIPPET: description -->
 *
 *
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 *  <li>profilingKey</li>
 * </ul>
 *
 * <!-- END SNIPPET: parameters -->
 *
 * <!-- START SNIPPET: extending -->
 *
 * none
 *
 * <!-- END SNIPPET: extending -->
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * // to change the profiling key
 * &lt;action ...&gt;
 *   ...
 *   &lt;interceptor-ref name="profiling"&gt;
 *      &lt;param name="profilingKey"&gt;profilingKey&lt;/param&gt;
 *   &lt;/interceptor-ref&gt;
 *   ...
 * &lt;/action&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @version $Date$ $Id$
 */
public class ProfilingActivationInterceptor extends AbstractInterceptor {

    private String profilingKey = "profiling";
    private boolean devMode;

    /**
     * @return the profilingKey
     */
    public String getProfilingKey() {
        return profilingKey;
    }

    /**
     * @param profilingKey the profilingKey to set
     */
    public void setProfilingKey(String profilingKey) {
        this.profilingKey = profilingKey;
    }
    
    @Inject(StrutsConstants.STRUTS_DEVMODE)
    public void setDevMode(String mode) {
        this.devMode = "true".equals(mode);
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        if (devMode) {
            Object val = invocation.getInvocationContext().getParameters().get(profilingKey);
            if (val != null) {
                String sval = (val instanceof String ? (String)val : ((String[])val)[0]);
                boolean enable = "yes".equalsIgnoreCase(sval) || "true".equalsIgnoreCase(sval);
                UtilTimerStack.setActive(enable);
                invocation.getInvocationContext().getParameters().remove(profilingKey);
            }
        }
        return invocation.invoke();

    }

}
