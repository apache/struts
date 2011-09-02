/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.lang.func");
dojo.require("dojo.lang.common");
dojo.lang.hitch = function (thisObject, method) {
	var args = [];
	for (var x = 2; x < arguments.length; x++) {
		args.push(arguments[x]);
	}
	var fcn = (dojo.lang.isString(method) ? thisObject[method] : method) || function () {
	};
	return function () {
		var ta = args.concat([]);
		for (var x = 0; x < arguments.length; x++) {
			ta.push(arguments[x]);
		}
		return fcn.apply(thisObject, ta);
	};
};
dojo.lang.anonCtr = 0;
dojo.lang.anon = {};
dojo.lang.nameAnonFunc = function (anonFuncPtr, thisObj, searchForNames) {
	var nso = (thisObj || dojo.lang.anon);
	if ((searchForNames) || ((dj_global["djConfig"]) && (djConfig["slowAnonFuncLookups"] == true))) {
		for (var x in nso) {
			try {
				if (nso[x] === anonFuncPtr) {
					return x;
				}
			}
			catch (e) {
			}
		}
	}
	var ret = "__" + dojo.lang.anonCtr++;
	while (typeof nso[ret] != "undefined") {
		ret = "__" + dojo.lang.anonCtr++;
	}
	nso[ret] = anonFuncPtr;
	return ret;
};
dojo.lang.forward = function (funcName) {
	return function () {
		return this[funcName].apply(this, arguments);
	};
};
dojo.lang.curry = function (thisObj, func) {
	var outerArgs = [];
	thisObj = thisObj || dj_global;
	if (dojo.lang.isString(func)) {
		func = thisObj[func];
	}
	for (var x = 2; x < arguments.length; x++) {
		outerArgs.push(arguments[x]);
	}
	var ecount = (func["__preJoinArity"] || func.length) - outerArgs.length;
	function gather(nextArgs, innerArgs, expected) {
		var texpected = expected;
		var totalArgs = innerArgs.slice(0);
		for (var x = 0; x < nextArgs.length; x++) {
			totalArgs.push(nextArgs[x]);
		}
		expected = expected - nextArgs.length;
		if (expected <= 0) {
			var res = func.apply(thisObj, totalArgs);
			expected = texpected;
			return res;
		} else {
			return function () {
				return gather(arguments, totalArgs, expected);
			};
		}
	}
	return gather([], outerArgs, ecount);
};
dojo.lang.curryArguments = function (thisObj, func, args, offset) {
	var targs = [];
	var x = offset || 0;
	for (x = offset; x < args.length; x++) {
		targs.push(args[x]);
	}
	return dojo.lang.curry.apply(dojo.lang, [thisObj, func].concat(targs));
};
dojo.lang.tryThese = function () {
	for (var x = 0; x < arguments.length; x++) {
		try {
			if (typeof arguments[x] == "function") {
				var ret = (arguments[x]());
				if (ret) {
					return ret;
				}
			}
		}
		catch (e) {
			dojo.debug(e);
		}
	}
};
dojo.lang.delayThese = function (farr, cb, delay, onend) {
	if (!farr.length) {
		if (typeof onend == "function") {
			onend();
		}
		return;
	}
	if ((typeof delay == "undefined") && (typeof cb == "number")) {
		delay = cb;
		cb = function () {
		};
	} else {
		if (!cb) {
			cb = function () {
			};
			if (!delay) {
				delay = 0;
			}
		}
	}
	setTimeout(function () {
		(farr.shift())();
		cb();
		dojo.lang.delayThese(farr, cb, delay, onend);
	}, delay);
};

