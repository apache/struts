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

import com.opensymphony.xwork2.conversion.annotations.Conversion;
import com.opensymphony.xwork2.conversion.annotations.ConversionType;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;
import com.opensymphony.xwork2.util.Element;

import java.util.List;

@Conversion(
	conversions={
		@TypeConversion(type=ConversionType.APPLICATION,
						key="com.opensymphony.xwork2.test.annotations.Address",
						converterClass=AddressTypeConverter.class),
		@TypeConversion(type=ConversionType.APPLICATION,
						key="com.opensymphony.xwork2.test.annotations.Person",
						converter="com.opensymphony.xwork2.test.annotations.PersonTypeConverter")})
public class PersonAction {
	List<Person> users;
	private List<Address> address;
	@Element(com.opensymphony.xwork2.test.annotations.Address.class)
	private List addressesNoGenericElementAnnotation;

	public List<Person> getUsers() {
		return users;
	}

	public void setUsers(List<Person> users) {
		this.users = users;
	}

	public void setAddress(List<Address> address) {
		this.address = address;
	}

	public List<Address> getAddress() {
		return address;
	}

	public void setAddressesNoGenericElementAnnotation(List addressesNoGenericElementAnnotation) {
		this.addressesNoGenericElementAnnotation = addressesNoGenericElementAnnotation;
	}

	public List getAddressesNoGenericElementAnnotation() {
		return addressesNoGenericElementAnnotation;
	}
}