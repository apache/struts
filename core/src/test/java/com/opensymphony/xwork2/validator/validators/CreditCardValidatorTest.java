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

import com.opensymphony.xwork2.TextProviderFactory;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.validator.DummyValidatorContext;
import com.opensymphony.xwork2.validator.ValidatorContext;
import org.apache.struts2.StrutsInternalTestCase;

import java.util.Arrays;
import java.util.List;

public class CreditCardValidatorTest extends StrutsInternalTestCase {

    private CreditCardValidator validator;
    private CreditCardAction action;
    private ValidatorContext context;

    public void testInvalidCardNumber() throws Exception {
        // given
        action.setAmericanExpress("123456768900");
        validator.setFieldName("americanExpress");
        validator.setDefaultMessage("It is not a valid American Express card number: ${americanExpress}");

        // when
        validator.validate(action);

        // then
        assertTrue(context.hasFieldErrors());
        assertEquals(1, context.getFieldErrors().size());
        assertEquals("It is not a valid American Express card number: 123456768900", context.getFieldErrors().get("americanExpress").get(0));
    }

    public void testInvalidArrayOfCardNumbers() throws Exception {
        // given
        action.setAmericanExpresses(new String[]{"098776544322"});
        validator.setFieldName("americanExpresses");
        validator.setDefaultMessage("It is not a valid American Express card number: ${currentValue}");

        // when
        validator.validate(action);

        // then
        assertTrue(context.hasFieldErrors());
        assertEquals(1, context.getFieldErrors().size());
        assertEquals("It is not a valid American Express card number: 098776544322", context.getFieldErrors().get("americanExpresses").get(0));
    }

    public void testEmptyArrayOfCardNumbers() throws Exception {
        // given
        action.setAmericanExpresses(new String[]{});
        validator.setFieldName("americanExpresses");
        validator.setDefaultMessage("It is not a valid American Express card number: ${currentValue}");

        // when
        validator.validate(action);

        // then
        assertFalse(context.hasFieldErrors());
    }

    public void testInvalidCollectionOfCardNumbers() throws Exception {
        // given
        action.setDinerClubs(Arrays.asList("75736151433"));
        validator.setFieldName("dinerClubs");
        validator.setDefaultMessage("It is not a valid Diner Club card number: ${currentValue}");

        // when
        validator.validate(action);

        // then
        assertTrue(context.hasFieldErrors());
        assertEquals(1, context.getFieldErrors().size());
        assertEquals("It is not a valid Diner Club card number: 75736151433", context.getFieldErrors().get("dinerClubs").get(0));
    }

    public void testValidAmericanExpressCard() throws Exception {
        // given
        action.setAmericanExpress("378282246310005");
        validator.setFieldName("americanExpress");

        // when
        validator.validate(action);

        // then
        assertFalse(context.hasFieldErrors());
    }

    public void testValidAmericanExpressCardWithSpaces() throws Exception {
        // given
        action.setAmericanExpress("3782 8224 6310 005");
        validator.setFieldName("americanExpress");

        // when
        validator.validate(action);

        // then
        assertFalse(context.hasFieldErrors());
    }

    public void testValidDinersClubCard() throws Exception {
        // given
        action.setDinersClub("30569309025904");
        validator.setFieldName("dinersClub");

        // when
        validator.validate(action);

        // then
        assertFalse(context.hasFieldErrors());
    }

    public void testValidJCBCard() throws Exception {
        // given
        action.setJCB("3530111333300000");
        validator.setFieldName("JCB");

        // when
        validator.validate(action);

        // then
        assertFalse(context.hasFieldErrors());
    }

    public void testMasterCardCard() throws Exception {
        // given
        action.setMasterCard("5555555555554444");
        validator.setFieldName("masterCard");

        // when
        validator.validate(action);

        // then
        assertFalse(context.hasFieldErrors());
    }

    public void testVisaCard() throws Exception {
        // given
        action.setVisa("4111111111111111");
        validator.setFieldName("visa");

        // when
        validator.validate(action);

        // then
        assertFalse(context.hasFieldErrors());
    }

    protected void setUp() throws Exception {
        super.setUp();
        validator = new CreditCardValidator();
        action = new CreditCardAction();
        TextProviderFactory tpf = container.getInstance(TextProviderFactory.class);
        context = new DummyValidatorContext(action, tpf);
        validator.setValidatorContext(context);

        ValueStack valueStack = container.getInstance(ValueStackFactory.class).createValueStack();
        valueStack.push(action);

        validator.setValueStack(valueStack);
    }

}

class CreditCardAction {

    private String americanExpress;
    private String dinersClub;
    private String JCB;
    private String masterCard;
    private String visa;
    private String[] americanExpresses;
    private List<String> dinerClubs;

    public void setAmericanExpress(String americanExpress) {
        this.americanExpress = americanExpress;
    }

    public String getAmericanExpress() {
        return americanExpress;
    }

    public void setDinersClub(String dinersClub) {
        this.dinersClub = dinersClub;
    }

    public String getDinersClub() {
        return dinersClub;
    }

    public void setJCB(String JCB) {
        this.JCB = JCB;
    }

    public String getJCB() {
        return JCB;
    }

    public void setMasterCard(String masterCard) {
        this.masterCard = masterCard;
    }

    public String getMasterCard() {
        return masterCard;
    }

    public void setVisa(String visa) {
        this.visa = visa;
    }

    public String getVisa() {
        return visa;
    }

    public void setAmericanExpresses(String[] americanExpresses) {
        this.americanExpresses = americanExpresses;
    }

    public String[] getAmericanExpresses() {
        return americanExpresses;
    }

    public void setDinerClubs(List<String> dinerClubs) {
        this.dinerClubs = dinerClubs;
    }

    public List<String> getDinerClubs() {
        return dinerClubs;
    }
}
