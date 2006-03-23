/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.Result;
import com.opensymphony.xwork.util.OgnlValueStack;
import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * TestResult
 *
 * @author Jason Carreira
 *         Created Apr 12, 2003 9:49:35 PM
 */
public class TestResult implements Result {

    private static final Log LOG = LogFactory.getLog(TestResult.class);


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
            OgnlValueStack stack = ActionContext.getContext().getValueStack();

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
