/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.string.common");
dojo.string.trim = function (str, wh) {
	if (!str.replace) {
		return str;
	}
	if (!str.length) {
		return str;
	}
	var re = (wh > 0) ? (/^\s+/) : (wh < 0) ? (/\s+$/) : (/^\s+|\s+$/g);
	return str.replace(re, "");
};
dojo.string.trimStart = function (str) {
	return dojo.string.trim(str, 1);
};
dojo.string.trimEnd = function (str) {
	return dojo.string.trim(str, -1);
};
dojo.string.repeat = function (str, count, separator) {
	var out = "";
	for (var i = 0; i < count; i++) {
		out += str;
		if (separator && i < count - 1) {
			out += separator;
		}
	}
	return out;
};
dojo.string.pad = function (str, len, c, dir) {
	var out = String(str);
	if (!c) {
		c = "0";
	}
	if (!dir) {
		dir = 1;
	}
	while (out.length < len) {
		if (dir > 0) {
			out = c + out;
		} else {
			out += c;
		}
	}
	return out;
};
dojo.string.padLeft = function (str, len, c) {
	return dojo.string.pad(str, len, c, 1);
};
dojo.string.padRight = function (str, len, c) {
	return dojo.string.pad(str, len, c, -1);
};

