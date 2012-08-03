package com.opensymphony.xwork2.test.annotations;

import com.opensymphony.xwork2.conversion.impl.DefaultTypeConverter;

import java.util.Map;

public class AddressTypeConverter extends DefaultTypeConverter {
	@Override public Object convertValue(Map<String, Object> context, Object value, Class toType) {
		if(value instanceof String) {
			return decodeAddress((String)value);
		} else if(value instanceof String && value.getClass().isArray()) {
			return decodeAddress(((String[])value)[0]);
		} else {
			Address address = (Address)value;
			return address.getLine1() + ":" + address.getLine2() + ":" +
			       address.getCity() + ":" + address.getCountry();
		}
	}

	private Address decodeAddress(String encodedAddress) {
		String[] parts = ((String)encodedAddress).split(":");
		Address address = new Address();
		address.setLine1(parts[0]);
		address.setLine2(parts[1]);
		address.setCity(parts[2]);
		address.setCountry(parts[3]);
		return address;
	}
}