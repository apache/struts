/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.validate.web");
dojo.require("dojo.validate.common");

dojo.validate.isIpAddress = function(/*String*/value, /*Object?*/flags) {
	// summary: Validates an IP address
	//
	// description:
	//  Supports 5 formats for IPv4: dotted decimal, dotted hex, dotted octal, decimal and hexadecimal.
	//  Supports 2 formats for Ipv6.
	//
	// value  A string.
	// flags  An object.  All flags are boolean with default = true.
	//    flags.allowDottedDecimal  Example, 207.142.131.235.  No zero padding.
	//    flags.allowDottedHex  Example, 0x18.0x11.0x9b.0x28.  Case insensitive.  Zero padding allowed.
	//    flags.allowDottedOctal  Example, 0030.0021.0233.0050.  Zero padding allowed.
	//    flags.allowDecimal  Example, 3482223595.  A decimal number between 0-4294967295.
	//    flags.allowHex  Example, 0xCF8E83EB.  Hexadecimal number between 0x0-0xFFFFFFFF.
	//      Case insensitive.  Zero padding allowed.
	//    flags.allowIPv6   IPv6 address written as eight groups of four hexadecimal digits.
	//    flags.allowHybrid   IPv6 address written as six groups of four hexadecimal digits
	//      followed by the usual 4 dotted decimal digit notation of IPv4. x:x:x:x:x:x:d.d.d.d

	var re = new RegExp("^" + dojo.regexp.ipAddress(flags) + "$", "i");
	return re.test(value); // Boolean
}


dojo.validate.isUrl = function(/*String*/value, /*Object?*/flags) {
	// summary: Checks if a string could be a valid URL
	// value: A string
	// flags: An object
	//    flags.scheme  Can be true, false, or [true, false]. 
	//      This means: required, not allowed, or either.
	//    flags in regexp.host can be applied.
	//    flags in regexp.ipAddress can be applied.
	//    flags in regexp.tld can be applied.

	var re = new RegExp("^" + dojo.regexp.url(flags) + "$", "i");
	return re.test(value); // Boolean
}

dojo.validate.isEmailAddress = function(/*String*/value, /*Object?*/flags) {
	// summary: Checks if a string could be a valid email address
	//
	// value: A string
	// flags: An object
	//    flags.allowCruft  Allow address like <mailto:foo@yahoo.com>.  Default is false.
	//    flags in regexp.host can be applied.
	//    flags in regexp.ipAddress can be applied.
	//    flags in regexp.tld can be applied.

	var re = new RegExp("^" + dojo.regexp.emailAddress(flags) + "$", "i");
	return re.test(value); // Boolean
}

dojo.validate.isEmailAddressList = function(/*String*/value, /*Object?*/flags) {
	// summary: Checks if a string could be a valid email address list.
	//
	// value  A string.
	// flags  An object.
	//    flags.listSeparator  The character used to separate email addresses.  Default is ";", ",", "\n" or " ".
	//    flags in regexp.emailAddress can be applied.
	//    flags in regexp.host can be applied.
	//    flags in regexp.ipAddress can be applied.
	//    flags in regexp.tld can be applied.

	var re = new RegExp("^" + dojo.regexp.emailAddressList(flags) + "$", "i");
	return re.test(value); // Boolean
}

dojo.validate.getEmailAddressList = function(/*String*/value, /*Object?*/flags) {
	// summary: Check if value is an email address list. If an empty list
	//  is returned, the value didn't pass the test or it was empty.
	//
	// value: A string
	// flags: An object (same as dojo.validate.isEmailAddressList)

	if(!flags) { flags = {}; }
	if(!flags.listSeparator) { flags.listSeparator = "\\s;,"; }

	if ( dojo.validate.isEmailAddressList(value, flags) ) {
		return value.split(new RegExp("\\s*[" + flags.listSeparator + "]\\s*")); // Array
	}
	return []; // Array
}
