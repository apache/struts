package org.apache.struts2.xwork2.test.annotations;

import org.apache.struts2.xwork2.conversion.annotations.Conversion;
import org.apache.struts2.xwork2.conversion.annotations.ConversionType;
import org.apache.struts2.xwork2.conversion.annotations.TypeConversion;
import org.apache.struts2.xwork2.util.Element;

import java.util.List;

@Conversion(
	conversions={
		@TypeConversion(type=ConversionType.APPLICATION,
						key="com.opensymphony.xwork2.test.annotations.Address",
						converter="com.opensymphony.xwork2.test.annotations.AddressTypeConverter"),
		@TypeConversion(type=ConversionType.APPLICATION,
						key="com.opensymphony.xwork2.test.annotations.Person",
						converter="com.opensymphony.xwork2.test.annotations.PersonTypeConverter")})
public class PersonAction {
	List<Person> users;
	private List<Address> address;
	@Element(org.apache.struts2.xwork2.test.annotations.Address.class)
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