/*
 * $Id: CheckboxListTest.java 439747 2006-09-03 09:22:46Z mrdon $
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

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.Iterator;

/**
 * <!-- START SNIPPET: description -->
 * Looks for a hidden identification field that specifies the original value of the checkbox.
 * If the checkbox isn't submitted, insert it into the parameters as if it was with the value 
 * of 'false'.
 * <!-- END SNIPPET: description -->
 * <p/>
 * <!-- START SNIPPET: parameters -->
 * <ul><li>setUncheckedValue -
 * The default value of an unchecked box can be overridden by setting the 'uncheckedValue' property.
 * </li></ul>
 * <!-- END SNIPPET: parameters -->
 * <p/>
 * <!-- START SNIPPET: extending -->
 * <p/>
 * <!-- END SNIPPET: extending -->
 */
public class CheckboxInterceptor implements Interceptor {
    
    /** Auto-generated serialization id */
    private static final long serialVersionUID = -586878104807229585L;
    
    private String uncheckedValue = Boolean.FALSE.toString();

    public void destroy() {
    }

    public void init() {
    }

    public String intercept(ActionInvocation ai) throws Exception {
        Map parameters = ai.getInvocationContext().getParameters();
        Map<String, String> newParams = new HashMap<String, String>();
        Set<String> keys = parameters.keySet();
        for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
            String key = iterator.next();

            if (key.startsWith("__checkbox_")) {
                String name = key.substring("__checkbox_".length());

                iterator.remove();

                // is this checkbox checked/submitted?
                if (!parameters.containsKey(name)) {
                    // if not, let's be sure to default the value to false
                    newParams.put(name, uncheckedValue);
                }
            }
        }

        parameters.putAll(newParams);

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
