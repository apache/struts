/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.widget.InternetTextbox");
dojo.require("dojo.widget.ValidationTextbox");
dojo.require("dojo.validate.web");
dojo.widget.defineWidget("dojo.widget.IpAddressTextbox", dojo.widget.ValidationTextbox, {mixInProperties:function (localProperties) {
	dojo.widget.IpAddressTextbox.superclass.mixInProperties.apply(this, arguments);
	if (localProperties.allowdotteddecimal) {
		this.flags.allowDottedDecimal = (localProperties.allowdotteddecimal == "true");
	}
	if (localProperties.allowdottedhex) {
		this.flags.allowDottedHex = (localProperties.allowdottedhex == "true");
	}
	if (localProperties.allowdottedoctal) {
		this.flags.allowDottedOctal = (localProperties.allowdottedoctal == "true");
	}
	if (localProperties.allowdecimal) {
		this.flags.allowDecimal = (localProperties.allowdecimal == "true");
	}
	if (localProperties.allowhex) {
		this.flags.allowHex = (localProperties.allowhex == "true");
	}
	if (localProperties.allowipv6) {
		this.flags.allowIPv6 = (localProperties.allowipv6 == "true");
	}
	if (localProperties.allowhybrid) {
		this.flags.allowHybrid = (localProperties.allowhybrid == "true");
	}
}, isValid:function () {
	return dojo.validate.isIpAddress(this.textbox.value, this.flags);
}});
dojo.widget.defineWidget("dojo.widget.UrlTextbox", dojo.widget.IpAddressTextbox, {mixInProperties:function (localProperties) {
	dojo.widget.UrlTextbox.superclass.mixInProperties.apply(this, arguments);
	if (localProperties.scheme) {
		this.flags.scheme = (localProperties.scheme == "true");
	}
	if (localProperties.allowip) {
		this.flags.allowIP = (localProperties.allowip == "true");
	}
	if (localProperties.allowlocal) {
		this.flags.allowLocal = (localProperties.allowlocal == "true");
	}
	if (localProperties.allowcc) {
		this.flags.allowCC = (localProperties.allowcc == "true");
	}
	if (localProperties.allowgeneric) {
		this.flags.allowGeneric = (localProperties.allowgeneric == "true");
	}
}, isValid:function () {
	return dojo.validate.isUrl(this.textbox.value, this.flags);
}});
dojo.widget.defineWidget("dojo.widget.EmailTextbox", dojo.widget.UrlTextbox, {mixInProperties:function (localProperties) {
	dojo.widget.EmailTextbox.superclass.mixInProperties.apply(this, arguments);
	if (localProperties.allowcruft) {
		this.flags.allowCruft = (localProperties.allowcruft == "true");
	}
}, isValid:function () {
	return dojo.validate.isEmailAddress(this.textbox.value, this.flags);
}});
dojo.widget.defineWidget("dojo.widget.EmailListTextbox", dojo.widget.EmailTextbox, {mixInProperties:function (localProperties) {
	dojo.widget.EmailListTextbox.superclass.mixInProperties.apply(this, arguments);
	if (localProperties.listseparator) {
		this.flags.listSeparator = localProperties.listseparator;
	}
}, isValid:function () {
	return dojo.validate.isEmailAddressList(this.textbox.value, this.flags);
}});

