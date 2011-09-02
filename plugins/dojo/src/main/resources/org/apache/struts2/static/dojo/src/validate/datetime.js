/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.validate.datetime");
dojo.require("dojo.validate.common");
dojo.validate.isValidTime = function (value, flags) {
	dojo.deprecated("dojo.validate.datetime", "use dojo.date.parse instead", "0.5");
	var re = new RegExp("^" + dojo.regexp.time(flags) + "$", "i");
	return re.test(value);
};
dojo.validate.is12HourTime = function (value) {
	dojo.deprecated("dojo.validate.datetime", "use dojo.date.parse instead", "0.5");
	return dojo.validate.isValidTime(value, {format:["h:mm:ss t", "h:mm t"]});
};
dojo.validate.is24HourTime = function (value) {
	dojo.deprecated("dojo.validate.datetime", "use dojo.date.parse instead", "0.5");
	return dojo.validate.isValidTime(value, {format:["HH:mm:ss", "HH:mm"]});
};
dojo.validate.isValidDate = function (dateValue, format) {
	dojo.deprecated("dojo.validate.datetime", "use dojo.date.parse instead", "0.5");
	if (typeof format == "object" && typeof format.format == "string") {
		format = format.format;
	}
	if (typeof format != "string") {
		format = "MM/DD/YYYY";
	}
	var reLiteral = format.replace(/([$^.*+?=!:|\/\\\(\)\[\]\{\}])/g, "\\$1");
	reLiteral = reLiteral.replace("YYYY", "([0-9]{4})");
	reLiteral = reLiteral.replace("MM", "(0[1-9]|10|11|12)");
	reLiteral = reLiteral.replace("M", "([1-9]|10|11|12)");
	reLiteral = reLiteral.replace("DDD", "(00[1-9]|0[1-9][0-9]|[12][0-9][0-9]|3[0-5][0-9]|36[0-6])");
	reLiteral = reLiteral.replace("DD", "(0[1-9]|[12][0-9]|30|31)");
	reLiteral = reLiteral.replace("D", "([1-9]|[12][0-9]|30|31)");
	reLiteral = reLiteral.replace("ww", "(0[1-9]|[1-4][0-9]|5[0-3])");
	reLiteral = reLiteral.replace("d", "([1-7])");
	reLiteral = "^" + reLiteral + "$";
	var re = new RegExp(reLiteral);
	if (!re.test(dateValue)) {
		return false;
	}
	var year = 0, month = 1, date = 1, dayofyear = 1, week = 1, day = 1;
	var tokens = format.match(/(YYYY|MM|M|DDD|DD|D|ww|d)/g);
	var values = re.exec(dateValue);
	for (var i = 0; i < tokens.length; i++) {
		switch (tokens[i]) {
		  case "YYYY":
			year = Number(values[i + 1]);
			break;
		  case "M":
		  case "MM":
			month = Number(values[i + 1]);
			break;
		  case "D":
		  case "DD":
			date = Number(values[i + 1]);
			break;
		  case "DDD":
			dayofyear = Number(values[i + 1]);
			break;
		  case "ww":
			week = Number(values[i + 1]);
			break;
		  case "d":
			day = Number(values[i + 1]);
			break;
		}
	}
	var leapyear = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
	if (date == 31 && (month == 4 || month == 6 || month == 9 || month == 11)) {
		return false;
	}
	if (date >= 30 && month == 2) {
		return false;
	}
	if (date == 29 && month == 2 && !leapyear) {
		return false;
	}
	if (dayofyear == 366 && !leapyear) {
		return false;
	}
	return true;
};

