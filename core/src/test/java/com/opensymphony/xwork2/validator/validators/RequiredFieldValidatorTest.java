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
package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.TextProviderFactory;
import com.opensymphony.xwork2.validator.DummyValidatorContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.junit.Test;

import java.util.ArrayList;

public class RequiredFieldValidatorTest extends StrutsInternalTestCase {

    @Test
    public void testNullObject() throws Exception {
        // given
        RequiredFieldValidator rfv = container.inject(RequiredFieldValidator.class);
        rfv.setValueStack(ActionContext.getContext().getValueStack());
        rfv.setFieldName("stringValue");
        rfv.setDefaultMessage("${fieldName} field is required!");
        ValidationAction action = new ValidationAction();
        DummyValidatorContext context = new DummyValidatorContext(action, container.getInstance(TextProviderFactory.class));
        rfv.setValidatorContext(context);

        // when
        rfv.validate(action);

        // then
        assertTrue(context.hasFieldErrors());
        assertEquals(1, context.getFieldErrors().size());
        assertNotNull(context.getFieldErrors().get("stringValue"));
        assertEquals("stringValue field is required!", context.getFieldErrors().get("stringValue").get(0));
    }

    @Test
    public void testArrayObject() throws Exception {
        // given
        RequiredFieldValidator rfv = container.inject(RequiredFieldValidator.class);
        rfv.setValueStack(ActionContext.getContext().getValueStack());
        rfv.setFieldName("ints");
        rfv.setDefaultMessage("${fieldName} field is required!");
        ValidationAction action = new ValidationAction();
        action.setInts(new Integer[]{});
        DummyValidatorContext context = new DummyValidatorContext(action, container.getInstance(TextProviderFactory.class));
        rfv.setValidatorContext(context);

        // when
        rfv.validate(action);

        // then
        assertTrue(context.hasFieldErrors());
        assertEquals(1, context.getFieldErrors().size());
        assertNotNull(context.getFieldErrors().get("ints"));
        assertEquals("ints field is required!", context.getFieldErrors().get("ints").get(0));
    }
    
    @Test
    public void testCollectionObject() throws Exception {
        // given
        RequiredFieldValidator rfv = container.inject(RequiredFieldValidator.class);
        rfv.setValueStack(ActionContext.getContext().getValueStack());
        rfv.setFieldName("shorts");
        rfv.setDefaultMessage("${fieldName} field is required!");
        ValidationAction action = new ValidationAction();
        action.setShorts(new ArrayList<Short>());
        DummyValidatorContext context = new DummyValidatorContext(action, container.getInstance(TextProviderFactory.class));
        rfv.setValidatorContext(context);

        // when
        rfv.validate(action);

        // then
        assertTrue(context.hasFieldErrors());
        assertEquals(1, context.getFieldErrors().size());
        assertNotNull(context.getFieldErrors().get("shorts"));
        assertEquals("shorts field is required!", context.getFieldErrors().get("shorts").get(0));
    }

}