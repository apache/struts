/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.lang.array");
dojo.require("dojo.lang.common");
dojo.lang.mixin(dojo.lang, {has:function (obj, name) {
	try {
		return typeof obj[name] != "undefined";
	}
	catch (e) {
		return false;
	}
}, isEmpty:function (obj) {
	if (dojo.lang.isObject(obj)) {
		var tmp = {};
		var count = 0;
		for (var x in obj) {
			if (obj[x] && (!tmp[x])) {
				count++;
				break;
			}
		}
		return count == 0;
	} else {
		if (dojo.lang.isArrayLike(obj) || dojo.lang.isString(obj)) {
			return obj.length == 0;
		}
	}
}, map:function (arr, obj, unary_func) {
	var isString = dojo.lang.isString(arr);
	if (isString) {
		arr = arr.split("");
	}
	if (dojo.lang.isFunction(obj) && (!unary_func)) {
		unary_func = obj;
		obj = dj_global;
	} else {
		if (dojo.lang.isFunction(obj) && unary_func) {
			var tmpObj = obj;
			obj = unary_func;
			unary_func = tmpObj;
		}
	}
	if (Array.map) {
		var outArr = Array.map(arr, unary_func, obj);
	} else {
		var outArr = [];
		for (var i = 0; i < arr.length; ++i) {
			outArr.push(unary_func.call(obj, arr[i]));
		}
	}
	if (isString) {
		return outArr.join("");
	} else {
		return outArr;
	}
}, reduce:function (arr, initialValue, obj, binary_func) {
	var reducedValue = initialValue;
	if (arguments.length == 2) {
		binary_func = initialValue;
		reducedValue = arr[0];
		arr = arr.slice(1);
	} else {
		if (arguments.length == 3) {
			if (dojo.lang.isFunction(obj)) {
				binary_func = obj;
				obj = null;
			}
		} else {
			if (dojo.lang.isFunction(obj)) {
				var tmp = binary_func;
				binary_func = obj;
				obj = tmp;
			}
		}
	}
	var ob = obj || dj_global;
	dojo.lang.map(arr, function (val) {
		reducedValue = binary_func.call(ob, reducedValue, val);
	});
	return reducedValue;
}, forEach:function (anArray, callback, thisObject) {
	if (dojo.lang.isString(anArray)) {
		anArray = anArray.split("");
	}
	if (Array.forEach) {
		Array.forEach(anArray, callback, thisObject);
	} else {
		if (!thisObject) {
			thisObject = dj_global;
		}
		for (var i = 0, l = anArray.length; i < l; i++) {
			callback.call(thisObject, anArray[i], i, anArray);
		}
	}
}, _everyOrSome:function (every, arr, callback, thisObject) {
	if (dojo.lang.isString(arr)) {
		arr = arr.split("");
	}
	if (Array.every) {
		return Array[every ? "every" : "some"](arr, callback, thisObject);
	} else {
		if (!thisObject) {
			thisObject = dj_global;
		}
		for (var i = 0, l = arr.length; i < l; i++) {
			var result = callback.call(thisObject, arr[i], i, arr);
			if (every && !result) {
				return false;
			} else {
				if ((!every) && (result)) {
					return true;
				}
			}
		}
		return Boolean(every);
	}
}, every:function (arr, callback, thisObject) {
	return this._everyOrSome(true, arr, callback, thisObject);
}, some:function (arr, callback, thisObject) {
	return this._everyOrSome(false, arr, callback, thisObject);
}, filter:function (arr, callback, thisObject) {
	var isString = dojo.lang.isString(arr);
	if (isString) {
		arr = arr.split("");
	}
	var outArr;
	if (Array.filter) {
		outArr = Array.filter(arr, callback, thisObject);
	} else {
		if (!thisObject) {
			if (arguments.length >= 3) {
				dojo.raise("thisObject doesn't exist!");
			}
			thisObject = dj_global;
		}
		outArr = [];
		for (var i = 0; i < arr.length; i++) {
			if (callback.call(thisObject, arr[i], i, arr)) {
				outArr.push(arr[i]);
			}
		}
	}
	if (isString) {
		return outArr.join("");
	} else {
		return outArr;
	}
}, unnest:function () {
	var out = [];
	for (var i = 0; i < arguments.length; i++) {
		if (dojo.lang.isArrayLike(arguments[i])) {
			var add = dojo.lang.unnest.apply(this, arguments[i]);
			out = out.concat(add);
		} else {
			out.push(arguments[i]);
		}
	}
	return out;
}, toArray:function (arrayLike, startOffset) {
	var array = [];
	for (var i = startOffset || 0; i < arrayLike.length; i++) {
		array.push(arrayLike[i]);
	}
	return array;
}});

