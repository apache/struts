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
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.validator.validators.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.validators.RequiredStringValidator;
import com.opensymphony.xwork2.validator.validators.VisitorFieldValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A test case for ActionValidatorManager.
 *
 * @author tmjee
 * @version $Date$ $Id$
 */
public class ActionValidatorManagerTest extends XWorkTestCase {



    public void testValidate() throws Exception {
        /* MockAction.class */
        // reference number
        ValueStack stack = container.getInstance(ValueStackFactory.class).createValueStack();
        final RequiredStringValidator referenceNumberRequiredStringValidator = container.inject(RequiredStringValidator.class);
        referenceNumberRequiredStringValidator.setFieldName("referenceNumber");
        referenceNumberRequiredStringValidator.setDefaultMessage("Reference number is required");
        referenceNumberRequiredStringValidator.setValueStack(stack);

        // order
        final RequiredFieldValidator orderRequiredValidator = container.inject(RequiredFieldValidator.class);
        orderRequiredValidator.setFieldName("order");
        orderRequiredValidator.setDefaultMessage("Order is required");
        orderRequiredValidator.setValueStack(stack);

        // customer
        final RequiredFieldValidator customerRequiredValidator = container.inject(RequiredFieldValidator.class);
        customerRequiredValidator.setFieldName("customer");
        customerRequiredValidator.setDefaultMessage("Customer is required");
        customerRequiredValidator.setValueStack(stack);
        final VisitorFieldValidator customerVisitorValidator = container.inject(VisitorFieldValidator.class);
        customerVisitorValidator.setAppendPrefix(true);
        customerVisitorValidator.setFieldName("customer");
        customerVisitorValidator.setValueStack(stack);

        /* Customer.class */
        // customer -> name
        final RequiredStringValidator customerNameRequiredStringValidator = container.inject(RequiredStringValidator.class);
        customerNameRequiredStringValidator.setFieldName("name");
        customerNameRequiredStringValidator.setDefaultMessage("Name is required");
        customerNameRequiredStringValidator.setValueStack(stack);

        // customer -> age
        final RequiredFieldValidator customerAgeRequiredValidator = container.inject(RequiredFieldValidator.class);
        customerAgeRequiredValidator.setFieldName("age");
        customerAgeRequiredValidator.setDefaultMessage("Age is required");
        customerAgeRequiredValidator.setValueStack(stack);

        // customer -> Address
        final RequiredFieldValidator customerAddressRequiredFieldValidator = container.inject(RequiredFieldValidator.class);
        customerAddressRequiredFieldValidator.setFieldName("address");
        customerAddressRequiredFieldValidator.setDefaultMessage("Address is required");
        customerAddressRequiredFieldValidator.setValueStack(stack);

        final VisitorFieldValidator customerAddressVisitorFieldValidator = container.inject(VisitorFieldValidator.class);
        customerAddressVisitorFieldValidator.setFieldName("address");
        customerAddressVisitorFieldValidator.setAppendPrefix(true);
        //customerAddressVisitorFieldValidator.setDefaultMessage("");
        customerAddressVisitorFieldValidator.setValueStack(stack);



        /* Address.class */
        // customer -> Address -> street
        final RequiredStringValidator customerAddressStreetRequiredFieldValidator = container.inject(RequiredStringValidator.class);
        customerAddressStreetRequiredFieldValidator.setFieldName("street");
        customerAddressStreetRequiredFieldValidator.setDefaultMessage("Street is required");
        customerAddressStreetRequiredFieldValidator.setShortCircuit(true);
        customerAddressStreetRequiredFieldValidator.setValueStack(stack);

        final RequiredStringValidator customerAddressStreetRequiredFieldValidator2 = container.inject(RequiredStringValidator.class);
        customerAddressStreetRequiredFieldValidator2.setFieldName("street");
        customerAddressStreetRequiredFieldValidator2.setDefaultMessage("Street is required 2");
        customerAddressStreetRequiredFieldValidator2.setShortCircuit(true);
        customerAddressStreetRequiredFieldValidator2.setValueStack(stack);

        // customer -> Address -> pobox
        final RequiredStringValidator customerAddressPoboxRequiredFieldValidator = container.inject(RequiredStringValidator.class);
        customerAddressPoboxRequiredFieldValidator.setFieldName("pobox");
        customerAddressPoboxRequiredFieldValidator.setDefaultMessage("PO Box is required");
        customerAddressPoboxRequiredFieldValidator.setShortCircuit(false);
        customerAddressPoboxRequiredFieldValidator.setValueStack(stack);

        final RequiredStringValidator customerAddressPoboxRequiredFieldValidator2 = container.inject(RequiredStringValidator.class);
        customerAddressPoboxRequiredFieldValidator2.setFieldName("pobox");
        customerAddressPoboxRequiredFieldValidator2.setDefaultMessage("PO Box is required 2");
        customerAddressPoboxRequiredFieldValidator2.setShortCircuit(false);
        customerAddressPoboxRequiredFieldValidator2.setValueStack(stack);



        final List<Validator> validatorsForMockAction = new ArrayList<Validator>() {
            {
                add(referenceNumberRequiredStringValidator);
                add(orderRequiredValidator);
                add(customerRequiredValidator);
                add(customerVisitorValidator);
            }
        };

        final List<Validator> validatorsForCustomer = new ArrayList<Validator>() {
            {
                add(customerNameRequiredStringValidator);
                add(customerAgeRequiredValidator);
                add(customerAddressRequiredFieldValidator);
                add(customerAddressVisitorFieldValidator);
            }
        };

        final List<Validator> validatorsForAddress = new ArrayList<Validator>() {
            {
                add(customerAddressStreetRequiredFieldValidator);
                add(customerAddressStreetRequiredFieldValidator2);
                add(customerAddressPoboxRequiredFieldValidator);
                add(customerAddressPoboxRequiredFieldValidator2);
            }
        };


        DefaultActionValidatorManager validatorManager = new DefaultActionValidatorManager() {
            @Override
            public List<Validator> getValidators(Class clazz, String context, String method) {
                if (clazz.isAssignableFrom(MockAction.class)) {
                    return validatorsForMockAction;
                }
                else if (clazz.isAssignableFrom(Customer.class)) {
                    return validatorsForCustomer;
                }
                else if (clazz.isAssignableFrom(Address.class)) {
                    return validatorsForAddress;
                }
                return Collections.emptyList();
            }
        };
        container.inject(validatorManager);

        customerVisitorValidator.setActionValidatorManager(validatorManager);
        customerAddressVisitorFieldValidator.setActionValidatorManager(validatorManager);

        MockAction action = container.inject(MockAction.class);
        stack.push(action);
        validatorManager.validate(action, "ctx");

        assertFalse(action.hasActionErrors());
        assertFalse(action.hasActionMessages());
        assertTrue(action.hasFieldErrors());
        assertTrue(action.getFieldErrors().containsKey("referenceNumber"));
        assertEquals((action.getFieldErrors().get("referenceNumber")).size(), 1);
        assertTrue(action.getFieldErrors().containsKey("order"));
        assertEquals((action.getFieldErrors().get("order")).size(), 1);
        assertTrue(action.getFieldErrors().containsKey("customer.name"));
        assertEquals((action.getFieldErrors().get("customer.name")).size(), 1);
        assertTrue(action.getFieldErrors().containsKey("customer.age"));
        assertEquals((action.getFieldErrors().get("customer.age")).size(), 1);
        assertTrue(action.getFieldErrors().containsKey("customer.address.street"));
        assertEquals((action.getFieldErrors().get("customer.address.street")).size(), 1);
        assertTrue(action.getFieldErrors().containsKey("customer.address.pobox"));
        assertEquals((action.getFieldErrors().get("customer.address.pobox")).size(), 2);
    }

    public static class MockAction extends ActionSupport {

        private String referenceNumber;
        private Integer order;
        private Customer customer = new Customer();


        public String getReferenceNumber() { return referenceNumber; }
        public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

        public Integer getOrder() { return order; }
        public void setOrder(Integer order) { this.order = order; }

        public Customer getCustomer() { return customer; }
        public void setCustomer(Customer customer) { this.customer = customer; }
    }


    public static class Customer {
        private String name;
        private Integer age;
        private Address address = new Address();

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }

        public Address getAddress() { return address; }
        public void setAddress(Address address) { this.address = address; }
    }

    public static class Address {
        private String street;
        private String pobox;

        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }

        public String getPobox() { return pobox; }
        public void setPobox(String pobox) { this.pobox = pobox; }
    }
}