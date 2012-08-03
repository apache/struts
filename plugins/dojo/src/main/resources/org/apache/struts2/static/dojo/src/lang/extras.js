/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.lang.extras");
dojo.require("dojo.lang.common");
dojo.lang.setTimeout = function (func, delay) {
	var context = window, argsStart = 2;
	if (!dojo.lang.isFunction(func)) {
		context = func;
		func = delay;
		delay = arguments[2];
		argsStart++;
	}
	if (dojo.lang.isString(func)) {
		func = context[func];
	}
	var args = [];
	for (var i = argsStart; i < arguments.length; i++) {
		args.push(arguments[i]);
	}
	return dojo.global().setTimeout(function () {
		func.apply(context, args);
	}, delay);
};
dojo.lang.clearTimeout = function (timer) {
	dojo.global().clearTimeout(timer);
};
dojo.lang.getNameInObj = function (ns, item) {
	if (!ns) {
		ns = dj_global;
	}
	for (var x in ns) {
		if (ns[x] === item) {
			return new String(x);
		}
	}
	return null;
};
dojo.lang.shallowCopy = function (obj, deep) {
	var i, ret;
	if (obj === null) {
		return null;
	}
	if (dojo.lang.isObject(obj)) {
		ret = new obj.constructor();
		for (i in obj) {
			if (dojo.lang.isUndefined(ret[i])) {
				ret[i] = deep ? dojo.lang.shallowCopy(obj[i], deep) : obj[i];
			}
		}
	} else {
		if (dojo.lang.isArray(obj)) {
			ret = [];
			for (i = 0; i < obj.length; i++) {
				ret[i] = deep ? dojo.lang.shallowCopy(obj[i], deep) : obj[i];
			}
		} else {
			ret = obj;
		}
	}
	return ret;
};
dojo.lang.firstValued = function () {
	for (var i = 0; i < arguments.length; i++) {
		if (typeof arguments[i] != "undefined") {
			return arguments[i];
		}
	}
	return undefined;
};
dojo.lang.getObjPathValue = function (objpath, context, create) {
	with (dojo.parseObjPath(objpath, context, create)) {
		return dojo.evalProp(prop, obj, create);
	}
};
dojo.lang.setObjPathValue = function (objpath, value, context, create) {
	dojo.deprecated("dojo.lang.setObjPathValue", "use dojo.parseObjPath and the '=' operator", "0.6");
	if (arguments.length < 4) {
		create = true;
	}
	with (dojo.parseObjPath(objpath, context, create)) {
		if (obj && (create || (prop in obj))) {
			obj[prop] = value;
		}
	}
};

