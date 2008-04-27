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

package org.apache.struts2;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;


/**
 * TestResult
 *
 */
public class TestResult implements Result {

    private static final long serialVersionUID = -4429258122011663164L;


    private static final Logger LOG = LoggerFactory.getLogger(TestResult.class);


    private List expectedValues = new ArrayList();
    private List propertyNames = new ArrayList();


    public void setExpectedValue(int index, String value) {
        expectedValues.set(index, value);
    }

    public void setExpectedValue(String value) {
        expectedValues.add(value);
    }

    public List getExpectedValues() {
        return expectedValues;
    }

    public void setPropertyName(int index, String propertyName) {
        propertyNames.set(index, propertyName);
    }

    public void setPropertyName(String propertyName) {
        propertyNames.add(propertyName);
    }

    public List getPropertyNames() {
        return propertyNames;
    }

    public void execute(ActionInvocation invocation) throws Exception {
        LOG.info("executing TestResult.");

        if ((expectedValues != null) && (expectedValues.size() > 0) && (propertyNames != null) && (propertyNames.size() > 0))
        {
            ValueStack stack = ActionContext.getContext().getValueStack();

            for (int i = 0; i < propertyNames.size(); i++) {
                String propertyName = (String) propertyNames.get(i);
                String expectedValue = null;

                if (i < expectedValues.size()) {
                    expectedValue = (String) expectedValues.get(i);
                }

                String value = (String) stack.findValue(propertyName, String.class);
                Assert.assertEquals(expectedValue, value);
            }
        } else {
            LOG.error("One of expectedValues = " + expectedValues + " and propertyNames = " + propertyNames + " was null or empty.");
            Assert.fail();
        }
    }
}
