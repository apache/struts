/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.lang.type");
dojo.require("dojo.lang.common");
dojo.lang.whatAmI = function (value) {
	dojo.deprecated("dojo.lang.whatAmI", "use dojo.lang.getType instead", "0.5");
	return dojo.lang.getType(value);
};
dojo.lang.whatAmI.custom = {};
dojo.lang.getType = function (value) {
	try {
		if (dojo.lang.isArray(value)) {
			return "array";
		}
		if (dojo.lang.isFunction(value)) {
			return "function";
		}
		if (dojo.lang.isString(value)) {
			return "string";
		}
		if (dojo.lang.isNumber(value)) {
			return "number";
		}
		if (dojo.lang.isBoolean(value)) {
			return "boolean";
		}
		if (dojo.lang.isAlien(value)) {
			return "alien";
		}
		if (dojo.lang.isUndefined(value)) {
			return "undefined";
		}
		for (var name in dojo.lang.whatAmI.custom) {
			if (dojo.lang.whatAmI.custom[name](value)) {
				return name;
			}
		}
		if (dojo.lang.isObject(value)) {
			return "object";
		}
	}
	catch (e) {
	}
	return "unknown";
};
dojo.lang.isNumeric = function (value) {
	return (!isNaN(value) && isFinite(value) && (value != null) && !dojo.lang.isBoolean(value) && !dojo.lang.isArray(value) && !/^\s*$/.test(value));
};
dojo.lang.isBuiltIn = function (value) {
	return (dojo.lang.isArray(value) || dojo.lang.isFunction(value) || dojo.lang.isString(value) || dojo.lang.isNumber(value) || dojo.lang.isBoolean(value) || (value == null) || (value instanceof Error) || (typeof value == "error"));
};
dojo.lang.isPureObject = function (value) {
	return ((value != null) && dojo.lang.isObject(value) && value.constructor == Object);
};
dojo.lang.isOfType = function (value, type, keywordParameters) {
	var optional = false;
	if (keywordParameters) {
		optional = keywordParameters["optional"];
	}
	if (optional && ((value === null) || dojo.lang.isUndefined(value))) {
		return true;
	}
	if (dojo.lang.isArray(type)) {
		var arrayOfTypes = type;
		for (var i in arrayOfTypes) {
			var aType = arrayOfTypes[i];
			if (dojo.lang.isOfType(value, aType)) {
				return true;
			}
		}
		return false;
	} else {
		if (dojo.lang.isString(type)) {
			type = type.toLowerCase();
		}
		switch (type) {
		  case Array:
		  case "array":
			return dojo.lang.isArray(value);
		  case Function:
		  case "function":
			return dojo.lang.isFunction(value);
		  case String:
		  case "string":
			return dojo.lang.isString(value);
		  case Number:
		  case "number":
			return dojo.lang.isNumber(value);
		  case "numeric":
			return dojo.lang.isNumeric(value);
		  case Boolean:
		  case "boolean":
			return dojo.lang.isBoolean(value);
		  case Object:
		  case "object":
			return dojo.lang.isObject(value);
		  case "pureobject":
			return dojo.lang.isPureObject(value);
		  case "builtin":
			return dojo.lang.isBuiltIn(value);
		  case "alien":
			return dojo.lang.isAlien(value);
		  case "undefined":
			return dojo.lang.isUndefined(value);
		  case null:
		  case "null":
			return (value === null);
		  case "optional":
			dojo.deprecated("dojo.lang.isOfType(value, [type, \"optional\"])", "use dojo.lang.isOfType(value, type, {optional: true} ) instead", "0.5");
			return ((value === null) || dojo.lang.isUndefined(value));
		  default:
			if (dojo.lang.isFunction(type)) {
				return (value instanceof type);
			} else {
				dojo.raise("dojo.lang.isOfType() was passed an invalid type");
			}
		}
	}
	dojo.raise("If we get here, it means a bug was introduced above.");
};
dojo.lang.getObject = function (str) {
	var parts = str.split("."), i = 0, obj = dj_global;
	do {
		obj = obj[parts[i++]];
	} while (i < parts.length && obj);
	return (obj != dj_global) ? obj : null;
};
dojo.lang.doesObjectExist = function (str) {
	var parts = str.split("."), i = 0, obj = dj_global;
	do {
		obj = obj[parts[i++]];
	} while (i < parts.length && obj);
	return (obj && obj != dj_global);
};

