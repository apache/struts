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