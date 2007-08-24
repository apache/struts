/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.lang.common");
dojo.lang.inherits = function (subclass, superclass) {
	if (!dojo.lang.isFunction(superclass)) {
		dojo.raise("dojo.inherits: superclass argument [" + superclass + "] must be a function (subclass: [" + subclass + "']");
	}
	subclass.prototype = new superclass();
	subclass.prototype.constructor = subclass;
	subclass.superclass = superclass.prototype;
	subclass["super"] = superclass.prototype;
};
dojo.lang._mixin = function (obj, props) {
	var tobj = {};
	for (var x in props) {
		if ((typeof tobj[x] == "undefined") || (tobj[x] != props[x])) {
			obj[x] = props[x];
		}
	}
	if (dojo.render.html.ie && (typeof (props["toString"]) == "function") && (props["toString"] != obj["toString"]) && (props["toString"] != tobj["toString"])) {
		obj.toString = props.toString;
	}
	return obj;
};
dojo.lang.mixin = function (obj, props) {
	for (var i = 1, l = arguments.length; i < l; i++) {
		dojo.lang._mixin(obj, arguments[i]);
	}
	return obj;
};
dojo.lang.extend = function (constructor, props) {
	for (var i = 1, l = arguments.length; i < l; i++) {
		dojo.lang._mixin(constructor.prototype, arguments[i]);
	}
	return constructor;
};
dojo.inherits = dojo.lang.inherits;
dojo.mixin = dojo.lang.mixin;
dojo.extend = dojo.lang.extend;
dojo.lang.find = function (array, value, identity, findLast) {
	if (!dojo.lang.isArrayLike(array) && dojo.lang.isArrayLike(value)) {
		dojo.deprecated("dojo.lang.find(value, array)", "use dojo.lang.find(array, value) instead", "0.5");
		var temp = array;
		array = value;
		value = temp;
	}
	var isString = dojo.lang.isString(array);
	if (isString) {
		array = array.split("");
	}
	if (findLast) {
		var step = -1;
		var i = array.length - 1;
		var end = -1;
	} else {
		var step = 1;
		var i = 0;
		var end = array.length;
	}
	if (identity) {
		while (i != end) {
			if (array[i] === value) {
				return i;
			}
			i += step;
		}
	} else {
		while (i != end) {
			if (array[i] == value) {
				return i;
			}
			i += step;
		}
	}
	return -1;
};
dojo.lang.indexOf = dojo.lang.find;
dojo.lang.findLast = function (array, value, identity) {
	return dojo.lang.find(array, value, identity, true);
};
dojo.lang.lastIndexOf = dojo.lang.findLast;
dojo.lang.inArray = function (array, value) {
	return dojo.lang.find(array, value) > -1;
};
dojo.lang.isObject = function (it) {
	if (typeof it == "undefined") {
		return false;
	}
	return (typeof it == "object" || it === null || dojo.lang.isArray(it) || dojo.lang.isFunction(it));
};
dojo.lang.isArray = function (it) {
	return (it && it instanceof Array || typeof it == "array");
};
dojo.lang.isArrayLike = function (it) {
	if ((!it) || (dojo.lang.isUndefined(it))) {
		return false;
	}
	if (dojo.lang.isString(it)) {
		return false;
	}
	if (dojo.lang.isFunction(it)) {
		return false;
	}
	if (dojo.lang.isArray(it)) {
		return true;
	}
	if ((it.tagName) && (it.tagName.toLowerCase() == "form")) {
		return false;
	}
	if (dojo.lang.isNumber(it.length) && isFinite(it.length)) {
		return true;
	}
	return false;
};
dojo.lang.isFunction = function (it) {
	return (it instanceof Function || typeof it == "function");
};
(function () {
	if ((dojo.render.html.capable) && (dojo.render.html["safari"])) {
		dojo.lang.isFunction = function (it) {
			if ((typeof (it) == "function") && (it == "[object NodeList]")) {
				return false;
			}
			return (it instanceof Function || typeof it == "function");
		};
	}
})();
dojo.lang.isString = function (it) {
	return (typeof it == "string" || it instanceof String);
};
dojo.lang.isAlien = function (it) {
	if (!it) {
		return false;
	}
	return !dojo.lang.isFunction(it) && /\{\s*\[native code\]\s*\}/.test(String(it));
};
dojo.lang.isBoolean = function (it) {
	return (it instanceof Boolean || typeof it == "boolean");
};
dojo.lang.isNumber = function (it) {
	return (it instanceof Number || typeof it == "number");
};
dojo.lang.isUndefined = function (it) {
	return ((typeof (it) == "undefined") && (it == undefined));
};

