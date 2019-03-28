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

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.XWorkTestCase;

import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Parameter;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import java.util.HashMap;
import java.util.Map;


/**
 * Unit test for {@link ActionMappingParametersInterceptor}.
 *
 * Adapted from ParametersInterceptor (@author Jason Carreira)
 */
public class ActionMappingParametersInterceptorTest extends XWorkTestCase {

    /**
     * Confirm that ActionMappingParametersInterceptor processing methods avoid
     * nested wrapping of parameters.
     */
    public void testParametersNoNestedWrapping() {
        final ActionMappingParametersInterceptor ampi = createActionMappingParametersInterceptor();
        final ActionContext ac = ActionContext.getContext();
        final Map<String, Object> parameters = new HashMap<String, Object>() {
            {
                put("fooKey", "fooValue");
                put("barKey", "barValue");
            }
        };
        final Map<String, Object> moreParameters = new HashMap<String, Object>() {
            {
                put("fooKey2", "fooValue2");
                put("barKey2", "barValue3");
            }
        };
        final Map<String, Object> evenMoreParameters = new HashMap<String, Object>() {
            {
                put("fooKey3", "fooValue3");
                put("barKey3", "barValue3");
            }
        };
        // Set up the initial ActionMapping with parameters in the context
        final HttpParameters wrappedParameters = HttpParameters.create(parameters).build();
        final ActionMapping actionMapping = new ActionMapping("testAction", "testNameSpace", "testMethod", parameters);
        ac.put(ServletActionContext.ACTION_MAPPING, actionMapping);

        // Retrieve parameters and confirm both builder result equality and that the contained Object
        // values of each Parameter element are not of type Parameter (i.e. no double-wrapping).
        // Note: ampi.retrieveParameters() only ever returns the parameters associated with the ActionContext's
        //   ActionMapping (not changed directly by ampi.addParametersToContext()).
        final HttpParameters retrievedParameters = ampi.retrieveParameters(ac);
        assertNotNull("retrievedParameters null ?", retrievedParameters);
        // Note: Cannot perform equality test on HttpParameters directly as hashCode values differ even
        //   when the contents are equivalent.  Instead we compare size and content equality.
        assertEquals("retrievedParameters size not equal to wrappedParameters size ?",
                retrievedParameters.size(), wrappedParameters.size());

        for (String parameterName : retrievedParameters.keySet() ) {
            Parameter retrievedParameter = retrievedParameters.get(parameterName);
            Parameter wrappedParameter = wrappedParameters.get(parameterName);
            assertNotNull("retrievedParameter is null ?", retrievedParameter);
            assertNotNull("wrappedParameter is null ?", wrappedParameter);
            Object retrievedParameterValue = retrievedParameter.getObject();
            Object wrappedParameterValue = wrappedParameter.getObject();
            assertNotNull("retrievedParameterValue is null ?", retrievedParameterValue);
            assertNotNull("wrappedParameterValue is null ?", wrappedParameterValue);
            assertFalse("retrievedParameterValue is type Parameter ?", retrievedParameterValue instanceof Parameter);
            assertEquals("retrievedParameterValue not equal to wrappedParameterValue ?",
                    retrievedParameterValue, wrappedParameterValue);
        }

        // Set up the initial ActionMapping with (artificially) already-wrapped parameters in the context
        final Map<String, Object> wrappedParametersMap = new HashMap<>(wrappedParameters.size());
        for (String parameterName : wrappedParameters.keySet() ) {
            wrappedParametersMap.put(parameterName, wrappedParameters.get(parameterName));
        }
        final ActionMapping actionMappingWrapped = new ActionMapping("testAction", "testNameSpace", "testMethod", wrappedParametersMap);
        ac.put(ServletActionContext.ACTION_MAPPING, actionMappingWrapped);

        // Retrieve parameters and confirm that the contained Object values of each Parameter
        // element are not of type Parameter (i.e. no double-wrapping).
        // Note: ampi.retrieveParameters() only ever returns the parameters associated with the ActionContext's
        //   ActionMapping (not changed directly by ampi.addParametersToContext()).
        final HttpParameters retrievedWrappedParameters = ampi.retrieveParameters(ac);
        assertNotNull("retrievedWrappedParameters null ?", retrievedWrappedParameters);
        // Note: Cannot perform equality test on HttpParameters directly as hashCode values differ even
        //   when the contents are equivalent.  Instead we compare size and content equality.
        assertEquals("retrievedWrappedParameters size not equal to wrappedParametersMap size ?",
                retrievedWrappedParameters.size(), wrappedParametersMap.size());

        for (String parameterName : retrievedWrappedParameters.keySet() ) {
            Parameter retrievedParameter = retrievedWrappedParameters.get(parameterName);
            Object wrappedParameter = wrappedParametersMap.get(parameterName);
            assertNotNull("retrievedParameter is null ?", retrievedParameter);
            assertNotNull("wrappedParameter is null ?", wrappedParameter);
            assertTrue("wrappedParameter is not a Parameter ?", wrappedParameter instanceof Parameter);
            Object retrievedParameterValue = retrievedParameter.getObject();
            Object wrappedParameterValue = ((Parameter) wrappedParameter).getObject();
            assertNotNull("retrievedParameterValue is null ?", retrievedParameterValue);
            assertNotNull("wrappedParameterValue is null ?", wrappedParameterValue);
            assertFalse("retrievedParameterValue is type Parameter ?", retrievedParameterValue instanceof Parameter);
            assertEquals("retrievedParameterValue not equal to wrappedParameterValue ?",
                    retrievedParameterValue, wrappedParameterValue);
        }

        // Add parameters to the context
        ampi.addParametersToContext(ac, parameters);

        // Retrieve parameters and confirm the expected size and that the contained Object
        // values of each Parameter element are not of type Parameter (i.e. no double-wrapping).
        final HttpParameters retrievedACParameters = ac.getParameters();
        assertNotNull("retrievedACParameters null ?", retrievedACParameters);
        assertEquals("retrievedACParameters size not equal to parameters size ?",
                retrievedACParameters.size(), parameters.size());

        for (String parameterName : retrievedACParameters.keySet() ) {
            Parameter retrievedACParameter = retrievedACParameters.get(parameterName);
            assertNotNull("retrievedACParameter is null ?", retrievedACParameter);
            Object parameterValue = retrievedACParameter.getObject();
            assertNotNull("parameterValue is null ?", parameterValue);
            assertFalse("parameterValue is of type Parameter ?", parameterValue instanceof Parameter);
        }

        // Add additional parameters to the context
        ampi.addParametersToContext(ac, moreParameters);

        // Retrieve parameters and confirm the expected size and that the contained Object
        // values of each Parameter element are not of type Parameter (i.e. no double-wrapping).
        final HttpParameters retrievedACMoreParameters = ac.getParameters();
        assertNotNull("retrievedACMoreParameters null ?", retrievedACMoreParameters);
        assertEquals("retrievedACMoreParameters size not equal to combined size (parameters + moreParameters) ?",
                retrievedACMoreParameters.size(), parameters.size() + moreParameters.size());

        for (String parameterName : retrievedACMoreParameters.keySet() ) {
            Parameter retrievedACMoreParameter = retrievedACMoreParameters.get(parameterName);
            assertNotNull("retrievedACMoreParameter is null ?", retrievedACMoreParameter);
            Object parameterValue = retrievedACMoreParameter.getObject();
            assertNotNull("parameterValue (more) is null ?", parameterValue);
            assertFalse("parameterValue (more) is of type Parameter ?", parameterValue instanceof Parameter);
        }

        // Build some "already wrapped" parameters and attempt to add them to the context
        final HttpParameters evenMoreWrappedParameters = HttpParameters.create(evenMoreParameters).build();
        assertNotNull("evenMoreWrappedParameters null ?", evenMoreWrappedParameters);
        assertEquals("evenMoreWrappedParameters size not equal to evenMoreParameters size ?",
                evenMoreWrappedParameters.size(), evenMoreParameters.size());
        ampi.addParametersToContext(ac, evenMoreWrappedParameters);

        // Retrieve parameters and confirm the expected size and that the contained Object
        // values of each Parameter element are not of type Parameter (i.e. no double-wrapping).
        final HttpParameters retrievedACEvenMoreParameters = ac.getParameters();
        assertNotNull("retrievedACEvenMoreParameters null ?", retrievedACEvenMoreParameters);
        assertEquals("retrievedACEvenMoreParameters size not equal to combined size (parameters + moreParameters + evenMoreParameters) ?",
                retrievedACEvenMoreParameters.size(), parameters.size() + moreParameters.size() + evenMoreParameters.size());

        for (String parameterName : retrievedACEvenMoreParameters.keySet() ) {
            Parameter retrievedACEvenMoreParameter = retrievedACEvenMoreParameters.get(parameterName);
            assertNotNull("retrievedACEvenMoreParameter is null ?", retrievedACEvenMoreParameter);
            Object parameterValue = retrievedACEvenMoreParameter.getObject();
            assertNotNull("parameterValue (even more) is null ?", parameterValue);
            assertFalse("parameterValue (even more) is of type Parameter ?", parameterValue instanceof Parameter);
        }
    }

    /**
     * Create and configure an ActionMappingParametersInterceptor instance
     * 
     * @return 
     */
    private ActionMappingParametersInterceptor createActionMappingParametersInterceptor() {
        ActionMappingParametersInterceptor ampi = new ActionMappingParametersInterceptor();
        container.inject(ampi);
        return ampi;
    }

}
