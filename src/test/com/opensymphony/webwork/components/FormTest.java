/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.components;

import com.opensymphony.webwork.views.jsp.AbstractUITagTest;
import com.opensymphony.webwork.TestAction;
import com.opensymphony.xwork.validator.validators.RequiredFieldValidator;

import java.util.List;

/**
 * <code>FormTest</code>
 *
 * @author Rainer Hermanns
 */
public class FormTest extends AbstractUITagTest {


    public void testTestFormGetValidators() {
        Form form = new Form(stack, request, response);
        form.getParameters().put("actionClass", TestAction.class);
        List v = form.getValidators("foo");
        assertEquals(1, v.size());
        assertEquals(RequiredFieldValidator.class, v.get(0).getClass());
    }
}
