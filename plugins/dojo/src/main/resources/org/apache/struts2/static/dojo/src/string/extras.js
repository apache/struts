/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.string.extras");
dojo.require("dojo.string.common");
dojo.require("dojo.lang.common");
dojo.require("dojo.lang.array");
dojo.string.substituteParams = function (template, hash) {
	var map = (typeof hash == "object") ? hash : dojo.lang.toArray(arguments, 1);
	return template.replace(/\%\{(\w+)\}/g, function (match, key) {
		if (typeof (map[key]) != "undefined" && map[key] != null) {
			return map[key];
		}
		dojo.raise("Substitution not found: " + key);
	});
};
dojo.string.capitalize = function (str) {
	if (!dojo.lang.isString(str)) {
		return "";
	}
	if (arguments.length == 0) {
		str = this;
	}
	var words = str.split(" ");
	for (var i = 0; i < words.length; i++) {
		words[i] = words[i].charAt(0).toUpperCase() + words[i].substring(1);
	}
	return words.join(" ");
};
dojo.string.isBlank = function (str) {
	if (!dojo.lang.isString(str)) {
		return true;
	}
	return (dojo.string.trim(str).length == 0);
};
dojo.string.encodeAscii = function (str) {
	if (!dojo.lang.isString(str)) {
		return str;
	}
	var ret = "";
	var value = escape(str);
	var match, re = /%u([0-9A-F]{4})/i;
	while ((match = value.match(re))) {
		var num = Number("0x" + match[1]);
		var newVal = escape("&#" + num + ";");
		ret += value.substring(0, match.index) + newVal;
		value = value.substring(match.index + match[0].length);
	}
	ret += value.replace(/\+/g, "%2B");
	return ret;
};
dojo.string.escape = function (type, str) {
	var args = dojo.lang.toArray(arguments, 1);
	switch (type.toLowerCase()) {
	  case "xml":
	  case "html":
	  case "xhtml":
		return dojo.string.escapeXml.apply(this, args);
	  case "sql":
		return dojo.string.escapeSql.apply(this, args);
	  case "regexp":
	  case "regex":
		return dojo.string.escapeRegExp.apply(this, args);
	  case "javascript":
	  case "jscript":
	  case "js":
		return dojo.string.escapeJavaScript.apply(this, args);
	  case "ascii":
		return dojo.string.encodeAscii.apply(this, args);
	  default:
		return str;
	}
};
dojo.string.escapeXml = function (str, noSingleQuotes) {
	str = str.replace(/&/gm, "&amp;").replace(/</gm, "&lt;").replace(/>/gm, "&gt;").replace(/"/gm, "&quot;");
	if (!noSingleQuotes) {
		str = str.replace(/'/gm, "&#39;");
	}
	return str;
};
dojo.string.escapeSql = function (str) {
	return str.replace(/'/gm, "''");
};
dojo.string.escapeRegExp = function (str) {
	return str.replace(/\\/gm, "\\\\").replace(/([\f\b\n\t\r[\^$|?*+(){}])/gm, "\\$1");
};
dojo.string.escapeJavaScript = function (str) {
	return str.replace(/(["'\f\b\n\t\r])/gm, "\\$1");
};
dojo.string.escapeString = function (str) {
	return ("\"" + str.replace(/(["\\])/g, "\\$1") + "\"").replace(/[\f]/g, "\\f").replace(/[\b]/g, "\\b").replace(/[\n]/g, "\\n").replace(/[\t]/g, "\\t").replace(/[\r]/g, "\\r");
};
dojo.string.summary = function (str, len) {
	if (!len || str.length <= len) {
		return str;
	}
	return str.substring(0, len).replace(/\.+$/, "") + "...";
};
dojo.string.endsWith = function (str, end, ignoreCase) {
	if (ignoreCase) {
		str = str.toLowerCase();
		end = end.toLowerCase();
	}
	if ((str.length - end.length) < 0) {
		return false;
	}
	return str.lastIndexOf(end) == str.length - end.length;
};
dojo.string.endsWithAny = function (str) {
	for (var i = 1; i < arguments.length; i++) {
		if (dojo.string.endsWith(str, arguments[i])) {
			return true;
		}
	}
	return false;
};
dojo.string.startsWith = function (str, start, ignoreCase) {
	if (ignoreCase) {
		str = str.toLowerCase();
		start = start.toLowerCase();
	}
	return str.indexOf(start) == 0;
};
dojo.string.startsWithAny = function (str) {
	for (var i = 1; i < arguments.length; i++) {
		if (dojo.string.startsWith(str, arguments[i])) {
			return true;
		}
	}
	return false;
};
dojo.string.has = function (str) {
	for (var i = 1; i < arguments.length; i++) {
		if (str.indexOf(arguments[i]) > -1) {
			return true;
		}
	}
	return false;
};
dojo.string.normalizeNewlines = function (text, newlineChar) {
	if (newlineChar == "\n") {
		text = text.replace(/\r\n/g, "\n");
		text = text.replace(/\r/g, "\n");
	} else {
		if (newlineChar == "\r") {
			text = text.replace(/\r\n/g, "\r");
			text = text.replace(/\n/g, "\r");
		} else {
			text = text.replace(/([^\r])\n/g, "$1\r\n").replace(/\r([^\n])/g, "\r\n$1");
		}
	}
	return text;
};
dojo.string.splitEscaped = function (str, charac) {
	var components = [];
	for (var i = 0, prevcomma = 0; i < str.length; i++) {
		if (str.charAt(i) == "\\") {
			i++;
			continue;
		}
		if (str.charAt(i) == charac) {
			components.push(str.substring(prevcomma, i));
			prevcomma = i + 1;
		}
	}
	components.push(str.substr(prevcomma));
	return components;
};

