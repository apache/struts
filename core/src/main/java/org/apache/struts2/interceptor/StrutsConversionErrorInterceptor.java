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
import com.opensymphony.xwork2.interceptor.ConversionErrorInterceptor;
import com.opensymphony.xwork2.util.ValueStack;


/**
 * <!-- START SNIPPET: description -->
 *
 * This interceptor extends {@link ConversionErrorInterceptor} but only adds conversion errors from the ActionContext to
 * the field errors of the action if the field value is not null, "", or {""} (a size 1 String array with only an empty
 * String). See {@link ConversionErrorInterceptor} for more information, as well as the Type Conversion documentation.
 *
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Interceptor parameters:</u>
 *
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 *
 * <li>None</li>
 *
 * </ul>
 *
 * <!-- END SNIPPET: parameters -->
 *
 * <p/> <u>Extending the interceptor:</u>
 *
 * <p/>
 *
 * <!-- START SNIPPET: extending -->
 *
 * There are no known extension points for this interceptor.
 *
 * <!-- END SNIPPET: extending -->
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="params"/&gt;
 *     &lt;interceptor-ref name="conversionError"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @see com.opensymphony.xwork2.ActionContext#getConversionErrors()
 * @see ConversionErrorInterceptor
 */
public class StrutsConversionErrorInterceptor extends ConversionErrorInterceptor {

    private static final long serialVersionUID = 2759744840082921602L;

    protected Object getOverrideExpr(ActionInvocation invocation, Object value) {
        ValueStack stack = invocation.getStack();

        try {
            stack.push(value);

            return escape(stack.findString("top"));
        } finally {
            stack.pop();
        }
    }

    /**
     * Returns <tt>false</tt> if the value is null, "", or {""} (array of size 1 with a blank element). Returns
     * <tt>true</tt> otherwise.
     *
     * @param propertyName the name of the property to check.
     * @param value        the value to error check.
     * @return <tt>false</tt>  if the value is null, "", or {""}, <tt>true</tt> otherwise.
     */
    protected boolean shouldAddError(String propertyName, Object value) {
        if (value == null) {
            return false;
        }

        if ("".equals(value)) {
            return false;
        }

        if (value instanceof String[]) {
            String[] array = (String[]) value;

            if (array.length == 0) {
                return false;
            }

            if (array.length > 1) {
                return true;
            }

            String str = array[0];

            if ("".equals(str)) {
                return false;
            }
        }

        return true;
    }
}
