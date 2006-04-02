/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.components;

import org.apache.struts.webwork.views.jsp.AbstractUITagTest;
import org.apache.struts.webwork.TestAction;
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
