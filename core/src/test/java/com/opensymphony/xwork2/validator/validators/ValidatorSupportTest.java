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
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.ognl.OgnlValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.validator.ValidationException;

public class ValidatorSupportTest extends XWorkTestCase {

    public void testConditionalParseExpression() {
        OgnlValueStack stack = (OgnlValueStack) container.getInstance(ValueStackFactory.class).createValueStack();
        stack.getContext().put("something", "somevalue");

        ActionContext.of(stack.getContext()).withContainer(container).bind();

        ValidatorSupport validator = new ValidatorSupport() {
            public void validate(Object object) throws ValidationException {
            }
        };
        validator.setValueStack(ActionContext.getContext().getValueStack());

        String result1 = validator.parse("${#something}", String.class).toString();

        assertEquals(result1, "somevalue");
    }

}
