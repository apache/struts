/*
 * Copyright 2002-2003,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.util.ValueStack;
import junit.framework.Assert;


/**
 * NestedAction
 *
 * @author Jason Carreira
 *         Created Mar 5, 2003 3:08:19 PM
 */
public class NestedAction implements Action {

    private String nestedProperty = ActionNestingTest.NESTED_VALUE;


    public NestedAction() {
    }


    public String getNestedProperty() {
        return nestedProperty;
    }

    public String execute() throws Exception {
        Assert.fail();

        return null;
    }

    public String noStack() {
        ValueStack stack = ActionContext.getContext().getValueStack();
        // Action + DefaultTextProvider on the stack
        Assert.assertEquals(2, stack.size());
        Assert.assertNull(stack.findValue(ActionNestingTest.KEY));
        Assert.assertEquals(ActionNestingTest.NESTED_VALUE, stack.findValue(ActionNestingTest.NESTED_KEY));

        return SUCCESS;
    }

    public String stack() {
        ValueStack stack = ActionContext.getContext().getValueStack();
        //DefaultTextProvider, NestedActionTest pushed on by the test, and the NestedAction
        Assert.assertEquals(3, stack.size());
        Assert.assertNotNull(stack.findValue(ActionNestingTest.KEY));
        Assert.assertEquals(ActionContext.getContext().getValueStack().findValue(ActionNestingTest.KEY), ActionNestingTest.VALUE);
        Assert.assertEquals(ActionNestingTest.NESTED_VALUE, stack.findValue(ActionNestingTest.NESTED_KEY));

        return SUCCESS;
    }
}
