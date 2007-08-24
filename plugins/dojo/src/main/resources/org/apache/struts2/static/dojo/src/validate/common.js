/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.validate.common");
dojo.require("dojo.regexp");
dojo.validate.isText = function (value, flags) {
	flags = (typeof flags == "object") ? flags : {};
	if (/^\s*$/.test(value)) {
		return false;
	}
	if (typeof flags.length == "number" && flags.length != value.length) {
		return false;
	}
	if (typeof flags.minlength == "number" && flags.minlength > value.length) {
		return false;
	}
	if (typeof flags.maxlength == "number" && flags.maxlength < value.length) {
		return false;
	}
	return true;
};
dojo.validate.isInteger = function (value, flags) {
	var re = new RegExp("^" + dojo.regexp.integer(flags) + "$");
	return re.test(value);
};
dojo.validate.isRealNumber = function (value, flags) {
	var re = new RegExp("^" + dojo.regexp.realNumber(flags) + "$");
	return re.test(value);
};
dojo.validate.isCurrency = function (value, flags) {
	var re = new RegExp("^" + dojo.regexp.currency(flags) + "$");
	return re.test(value);
};
dojo.validate._isInRangeCache = {};
dojo.validate.isInRange = function (value, flags) {
	value = value.replace(dojo.lang.has(flags, "separator") ? flags.separator : ",", "", "g").replace(dojo.lang.has(flags, "symbol") ? flags.symbol : "$", "");
	if (isNaN(value)) {
		return false;
	}
	flags = (typeof flags == "object") ? flags : {};
	var max = (typeof flags.max == "number") ? flags.max : Infinity;
	var min = (typeof flags.min == "number") ? flags.min : -Infinity;
	var dec = (typeof flags.decimal == "string") ? flags.decimal : ".";
	var cache = dojo.validate._isInRangeCache;
	var cacheIdx = value + "max" + max + "min" + min + "dec" + dec;
	if (typeof cache[cacheIdx] != "undefined") {
		return cache[cacheIdx];
	}
	var pattern = "[^" + dec + "\\deE+-]";
	value = value.replace(RegExp(pattern, "g"), "");
	value = value.replace(/^([+-]?)(\D*)/, "$1");
	value = value.replace(/(\D*)$/, "");
	pattern = "(\\d)[" + dec + "](\\d)";
	value = value.replace(RegExp(pattern, "g"), "$1.$2");
	value = Number(value);
	if (value < min || value > max) {
		cache[cacheIdx] = false;
		return false;
	}
	cache[cacheIdx] = true;
	return true;
};
dojo.validate.isNumberFormat = function (value, flags) {
	var re = new RegExp("^" + dojo.regexp.numberFormat(flags) + "$", "i");
	return re.test(value);
};
dojo.validate.isValidLuhn = function (value) {
	var sum, parity, curDigit;
	if (typeof value != "string") {
		value = String(value);
	}
	value = value.replace(/[- ]/g, "");
	parity = value.length % 2;
	sum = 0;
	for (var i = 0; i < value.length; i++) {
		curDigit = parseInt(value.charAt(i));
		if (i % 2 == parity) {
			curDigit *= 2;
		}
		if (curDigit > 9) {
			curDigit -= 9;
		}
		sum += curDigit;
	}
	return !(sum % 10);
};

