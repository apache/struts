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
        final RequiredStringValidator referenceNumberRequiredStringValidator = new RequiredStringValidator();
        referenceNumberRequiredStringValidator.setFieldName("referenceNumber");
        referenceNumberRequiredStringValidator.setDefaultMessage("Reference number is required");
        referenceNumberRequiredStringValidator.setValueStack(stack);

        // order
        final RequiredFieldValidator orderRequiredValidator = new RequiredFieldValidator();
        orderRequiredValidator.setFieldName("order");
        orderRequiredValidator.setDefaultMessage("Order is required");
        orderRequiredValidator.setValueStack(stack);

        // customer
        final RequiredFieldValidator customerRequiredValidator = new RequiredFieldValidator();
        customerRequiredValidator.setFieldName("customer");
        customerRequiredValidator.setDefaultMessage("Customer is required");
        customerRequiredValidator.setValueStack(stack);
        final VisitorFieldValidator customerVisitorValidator = new VisitorFieldValidator();
        customerVisitorValidator.setAppendPrefix(true);
        customerVisitorValidator.setFieldName("customer");
        customerVisitorValidator.setValueStack(stack);

        /* Customer.class */
        // customer -> name
        final RequiredStringValidator customerNameRequiredStringValidator = new RequiredStringValidator();
        customerNameRequiredStringValidator.setFieldName("name");
        customerNameRequiredStringValidator.setDefaultMessage("Name is required");
        customerNameRequiredStringValidator.setValueStack(stack);

        // customer -> age
        final RequiredFieldValidator customerAgeRequiredValidator = new RequiredFieldValidator();
        customerAgeRequiredValidator.setFieldName("age");
        customerAgeRequiredValidator.setDefaultMessage("Age is required");
        customerAgeRequiredValidator.setValueStack(stack);

        // customer -> Address
        final RequiredFieldValidator customerAddressRequiredFieldValidator = new RequiredFieldValidator();
        customerAddressRequiredFieldValidator.setFieldName("address");
        customerAddressRequiredFieldValidator.setDefaultMessage("Address is required");
        customerAddressRequiredFieldValidator.setValueStack(stack);

        final VisitorFieldValidator customerAddressVisitorFieldValidator = new VisitorFieldValidator();
        customerAddressVisitorFieldValidator.setFieldName("address");
        customerAddressVisitorFieldValidator.setAppendPrefix(true);
        //customerAddressVisitorFieldValidator.setDefaultMessage("");
        customerAddressVisitorFieldValidator.setValueStack(stack);



        /* Address.class */
        // customer -> Address -> street
        final RequiredStringValidator customerAddressStreetRequiredFieldValidator = new RequiredStringValidator();
        customerAddressStreetRequiredFieldValidator.setFieldName("street");
        customerAddressStreetRequiredFieldValidator.setDefaultMessage("Street is required");
        customerAddressStreetRequiredFieldValidator.setShortCircuit(true);
        customerAddressStreetRequiredFieldValidator.setValueStack(stack);

        final RequiredStringValidator customerAddressStreetRequiredFieldValidator2 = new RequiredStringValidator();
        customerAddressStreetRequiredFieldValidator2.setFieldName("street");
        customerAddressStreetRequiredFieldValidator2.setDefaultMessage("Street is required 2");
        customerAddressStreetRequiredFieldValidator2.setShortCircuit(true);
        customerAddressStreetRequiredFieldValidator2.setValueStack(stack);

        // customer -> Address -> pobox
        final RequiredStringValidator customerAddressPoboxRequiredFieldValidator = new RequiredStringValidator();
        customerAddressPoboxRequiredFieldValidator.setFieldName("pobox");
        customerAddressPoboxRequiredFieldValidator.setDefaultMessage("PO Box is required");
        customerAddressPoboxRequiredFieldValidator.setShortCircuit(false);
        customerAddressPoboxRequiredFieldValidator.setValueStack(stack);

        final RequiredStringValidator customerAddressPoboxRequiredFieldValidator2 = new RequiredStringValidator();
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
        customerVisitorValidator.setActionValidatorManager(validatorManager);
        customerAddressVisitorFieldValidator.setActionValidatorManager(validatorManager);

        MockAction action = new MockAction();
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

    private class MockAction extends ActionSupport {

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


    private class Customer {
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

    private class Address {
        private String street;
        private String pobox;

        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }

        public String getPobox() { return pobox; }
        public void setPobox(String pobox) { this.pobox = pobox; }
    }
}