package com.opensymphony.xwork2.test.annotations;

import com.opensymphony.xwork2.conversion.impl.DefaultTypeConverter;

import java.util.Map;

public class PersonTypeConverter extends DefaultTypeConverter {
	@Override
    public Object convertValue(Map<String, Object> context, Object value, Class toType) {
		if(value instanceof String) {
			return decodePerson((String)value);
		} else if(value instanceof String && value.getClass().isArray()) {
			return decodePerson(((String[])value)[0]);
		} else {
			Person person = (Person)value;
			return person.getFirstName() + ":" + person.getLastName();
		}
	}

	private Person decodePerson(String encodedPerson) {
		String[] parts = ((String)encodedPerson).split(":");
		Person person = new Person();
		person.setFirstName(parts[0]);
		person.setLastName(parts[1]);
		return person;
	}
}