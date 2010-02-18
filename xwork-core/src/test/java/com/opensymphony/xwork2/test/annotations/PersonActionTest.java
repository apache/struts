package com.opensymphony.xwork2.test.annotations;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;

import java.util.Map;


public class PersonActionTest extends XWorkTestCase {
    
	public void testAddPerson() {
        ValueStack stack = ActionContext.getContext().getValueStack();

        Map<String, Object> stackContext = stack.getContext();
        stackContext.put(ReflectionContextState.CREATE_NULL_OBJECTS, Boolean.TRUE);
        stackContext.put(ReflectionContextState.DENY_METHOD_EXECUTION, Boolean.TRUE);
        stackContext.put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);

        PersonAction action = new PersonAction();
        stack.push(action);

        stack.setValue("users", "jonathan:gerrish");
        assertNotNull(action.getUsers());
        assertEquals(1, action.getUsers().size());
        
        for(Object person : action.getUsers()) {
        	System.out.println("Person: " + person);
        }
        
        assertEquals(Person.class, action.getUsers().get(0).getClass());
        assertEquals("jonathan", action.getUsers().get(0).getFirstName());
        assertEquals("gerrish", action.getUsers().get(0).getLastName());
	}
	
	public void testAddAddress() {
        ValueStack stack = ActionContext.getContext().getValueStack();
		Map<String, Object> stackContext = stack.getContext();
		stackContext.put(ReflectionContextState.CREATE_NULL_OBJECTS, Boolean.TRUE);
		stackContext.put(ReflectionContextState.DENY_METHOD_EXECUTION, Boolean.TRUE);
		stackContext.put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);

		PersonAction action = new PersonAction();
		stack.push(action);

		stack.setValue("address", "2 Chandos Court:61 Haverstock Hill:London:England");
		assertNotNull(action.getAddress());
		assertEquals(1, action.getAddress().size());

		for(Object address : action.getAddress()) {
			System.out.println("Address: " + address);
		}

		assertEquals(Address.class, action.getAddress().get(0).getClass());
		assertEquals("2 Chandos Court", action.getAddress().get(0).getLine1());
		assertEquals("61 Haverstock Hill", action.getAddress().get(0).getLine2());
		assertEquals("London", action.getAddress().get(0).getCity());
		assertEquals("England", action.getAddress().get(0).getCountry());
	}
	
	public void testAddAddressesNoGenericElementAnnotation() {
        ValueStack stack = ActionContext.getContext().getValueStack();
		Map<String, Object> stackContext = stack.getContext();
		stackContext.put(ReflectionContextState.CREATE_NULL_OBJECTS, Boolean.TRUE);
		stackContext.put(ReflectionContextState.DENY_METHOD_EXECUTION, Boolean.TRUE);
		stackContext.put(XWorkConverter.REPORT_CONVERSION_ERRORS, Boolean.TRUE);

		PersonAction action = new PersonAction();
		stack.push(action);

		stack.setValue("addressesNoGenericElementAnnotation", "2 Chandos Court:61 Haverstock Hill:London:England");
		assertNotNull(action.getAddressesNoGenericElementAnnotation());
		assertEquals(1, action.getAddressesNoGenericElementAnnotation().size());

		for(Object address : action.getAddressesNoGenericElementAnnotation()) {
			System.out.println("Address: " + address);
		}

		assertEquals(Address.class, action.getAddressesNoGenericElementAnnotation().get(0).getClass());
		assertEquals("2 Chandos Court", ((Address)action.getAddressesNoGenericElementAnnotation().get(0)).getLine1());
		assertEquals("61 Haverstock Hill", ((Address)action.getAddressesNoGenericElementAnnotation().get(0)).getLine2());
		assertEquals("London", ((Address)action.getAddressesNoGenericElementAnnotation().get(0)).getCity());
		assertEquals("England", ((Address)action.getAddressesNoGenericElementAnnotation().get(0)).getCountry());
	}
}