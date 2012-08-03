/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.UsTextbox");
dojo.require("dojo.widget.ValidationTextbox");
dojo.require("dojo.validate.us");
dojo.widget.defineWidget("dojo.widget.UsStateTextbox", dojo.widget.ValidationTextbox, {mixInProperties:function (localProperties) {
	dojo.widget.UsStateTextbox.superclass.mixInProperties.apply(this, arguments);
	if (localProperties.allowterritories) {
		this.flags.allowTerritories = (localProperties.allowterritories == "true");
	}
	if (localProperties.allowmilitary) {
		this.flags.allowMilitary = (localProperties.allowmilitary == "true");
	}
}, isValid:function () {
	return dojo.validate.us.isState(this.textbox.value, this.flags);
}});
dojo.widget.defineWidget("dojo.widget.UsZipTextbox", dojo.widget.ValidationTextbox, {isValid:function () {
	return dojo.validate.us.isZipCode(this.textbox.value);
}});
dojo.widget.defineWidget("dojo.widget.UsSocialSecurityNumberTextbox", dojo.widget.ValidationTextbox, {isValid:function () {
	return dojo.validate.us.isSocialSecurityNumber(this.textbox.value);
}});
dojo.widget.defineWidget("dojo.widget.UsPhoneNumberTextbox", dojo.widget.ValidationTextbox, {isValid:function () {
	return dojo.validate.us.isPhoneNumber(this.textbox.value);
}});

