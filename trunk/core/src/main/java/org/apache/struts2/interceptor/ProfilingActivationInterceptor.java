/*
 * $Id: CreateSessionInterceptor.java 439747 2006-09-03 09:22:46Z mrdon $
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.interceptor;

import org.apache.struts2.dispatcher.Dispatcher;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.profiling.UtilTimerStack;

/**
 * Allows profiling to be enabled or disabled via request parameters, when
 * devMode is enabled.
 */
public class ProfilingActivationInterceptor extends AbstractInterceptor {

    private String profilingKey = "profiling";
    
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

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        if (Dispatcher.getInstance().isDevMode()) {
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
