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
package org.apache.struts.beanvalidation.validation.interceptor;

import com.opensymphony.xwork2.TextProviderFactory;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.validator.DelegatingValidatorContext;
import com.opensymphony.xwork2.validator.ValidatorContext;
import org.apache.struts.beanvalidation.actions.CheckDigitAction;
import org.apache.struts.beanvalidation.actions.FieldAction;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class BeanValidationInterceptorMessageTest extends XWorkTestCase {

    private BeanValidationInterceptor interceptor;

    public void testDefaultProviderMessageIsNotLookedUpAsTextKey() {
        CheckDigitAction action = new CheckDigitAction();
        action.setCardNumber("79927398711");
        ConstraintViolation<Object> violation = violationOf(action);
        List<String> lookups = new ArrayList<>();
        ValidatorContext context = new DelegatingValidatorContext(action, container.getInstance(TextProviderFactory.class)) {
            @Override
            public boolean hasKey(String key) {
                lookups.add(key);
                return super.hasKey(key);
            }

            @Override
            public String getText(String key) {
                lookups.add(key);
                return super.getText(key);
            }
        };

        String message = interceptor.resolveMessage(violation, context);

        assertEquals(violation.getMessage(), message);
        assertTrue(lookups.toString(), lookups.isEmpty());
    }

    public void testBundleMessageIsConvertedToUtf8() {
        interceptor.setConvertToUtf8("true");
        FieldAction action = blankFieldAction();

        String message = interceptor.resolveMessage(violationOf(action), contextWithText(action, () -> "Å¼"));

        assertEquals("ż", message);
    }

    public void testBlankBundleMessageIsNotConverted() {
        interceptor.setConvertToUtf8("true");
        FieldAction action = blankFieldAction();

        String message = interceptor.resolveMessage(violationOf(action), contextWithText(action, () -> " "));

        assertEquals(" ", message);
    }

    public void testProviderMessageIsNotConvertedToUtf8() {
        interceptor.setConvertToUtf8("true");
        CheckDigitAction action = new CheckDigitAction();
        action.setCardNumber("ż79927398711");

        String message = interceptor.resolveMessage(violationOf(action), contextWithText(action, () -> "unused"));

        assertTrue(message, message.contains("ż79927398711"));
    }

    public void testFailedBundleLookupFallsBackToTemplate() {
        FieldAction action = blankFieldAction();

        String message = interceptor.resolveMessage(violationOf(action), contextWithText(action, () -> {
            throw new IllegalStateException("lookup failed");
        }));

        assertEquals("canNotBeBlank", message);
    }

    private static FieldAction blankFieldAction() {
        FieldAction action = new FieldAction();
        action.setTest(" ");
        return action;
    }

    private static ConstraintViolation<Object> violationOf(Object action) {
        return Validation.buildDefaultValidatorFactory().getValidator().validate(action).iterator().next();
    }

    private ValidatorContext contextWithText(Object action, Supplier<String> text) {
        return new DelegatingValidatorContext(action, container.getInstance(TextProviderFactory.class)) {
            @Override
            public boolean hasKey(String key) {
                return true;
            }

            @Override
            public String getText(String key) {
                return text.get();
            }
        };
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        interceptor = new BeanValidationInterceptor();
    }
}
