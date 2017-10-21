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