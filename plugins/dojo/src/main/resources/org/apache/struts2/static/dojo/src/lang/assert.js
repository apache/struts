/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.lang.assert");
dojo.require("dojo.lang.common");
dojo.require("dojo.lang.array");
dojo.require("dojo.lang.type");
dojo.lang.assert = function (booleanValue, message) {
	if (!booleanValue) {
		var errorMessage = "An assert statement failed.\n" + "The method dojo.lang.assert() was called with a 'false' value.\n";
		if (message) {
			errorMessage += "Here's the assert message:\n" + message + "\n";
		}
		throw new Error(errorMessage);
	}
};
dojo.lang.assertType = function (value, type, keywordParameters) {
	if (dojo.lang.isString(keywordParameters)) {
		dojo.deprecated("dojo.lang.assertType(value, type, \"message\")", "use dojo.lang.assertType(value, type) instead", "0.5");
	}
	if (!dojo.lang.isOfType(value, type, keywordParameters)) {
		if (!dojo.lang.assertType._errorMessage) {
			dojo.lang.assertType._errorMessage = "Type mismatch: dojo.lang.assertType() failed.";
		}
		dojo.lang.assert(false, dojo.lang.assertType._errorMessage);
	}
};
dojo.lang.assertValidKeywords = function (object, expectedProperties, message) {
	var key;
	if (!message) {
		if (!dojo.lang.assertValidKeywords._errorMessage) {
			dojo.lang.assertValidKeywords._errorMessage = "In dojo.lang.assertValidKeywords(), found invalid keyword:";
		}
		message = dojo.lang.assertValidKeywords._errorMessage;
	}
	if (dojo.lang.isArray(expectedProperties)) {
		for (key in object) {
			if (!dojo.lang.inArray(expectedProperties, key)) {
				dojo.lang.assert(false, message + " " + key);
			}
		}
	} else {
		for (key in object) {
			if (!(key in expectedProperties)) {
				dojo.lang.assert(false, message + " " + key);
			}
		}
	}
};

