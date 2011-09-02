/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.validate.creditCard");
dojo.require("dojo.lang.common");
dojo.require("dojo.validate.common");
dojo.validate.isValidCreditCard = function (value, ccType) {
	if (value && ccType && ((ccType.toLowerCase() == "er" || dojo.validate.isValidLuhn(value)) && (dojo.validate.isValidCreditCardNumber(value, ccType.toLowerCase())))) {
		return true;
	}
	return false;
};
dojo.validate.isValidCreditCardNumber = function (value, ccType) {
	if (typeof value != "string") {
		value = String(value);
	}
	value = value.replace(/[- ]/g, "");
	var results = [];
	var cardinfo = {"mc":"5[1-5][0-9]{14}", "ec":"5[1-5][0-9]{14}", "vi":"4([0-9]{12}|[0-9]{15})", "ax":"3[47][0-9]{13}", "dc":"3(0[0-5][0-9]{11}|[68][0-9]{12})", "bl":"3(0[0-5][0-9]{11}|[68][0-9]{12})", "di":"6011[0-9]{12}", "jcb":"(3[0-9]{15}|(2131|1800)[0-9]{11})", "er":"2(014|149)[0-9]{11}"};
	if (ccType && dojo.lang.has(cardinfo, ccType.toLowerCase())) {
		return Boolean(value.match(cardinfo[ccType.toLowerCase()]));
	} else {
		for (var p in cardinfo) {
			if (value.match("^" + cardinfo[p] + "$") != null) {
				results.push(p);
			}
		}
		return (results.length) ? results.join("|") : false;
	}
};
dojo.validate.isValidCvv = function (value, ccType) {
	if (typeof value != "string") {
		value = String(value);
	}
	var format;
	switch (ccType.toLowerCase()) {
	  case "mc":
	  case "ec":
	  case "vi":
	  case "di":
		format = "###";
		break;
	  case "ax":
		format = "####";
		break;
	  default:
		return false;
	}
	var flags = {format:format};
	if ((value.length == format.length) && (dojo.validate.isNumberFormat(value, flags))) {
		return true;
	}
	return false;
};

