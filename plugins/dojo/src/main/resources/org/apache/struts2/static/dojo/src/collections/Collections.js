/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.collections.Collections");
dojo.collections.DictionaryEntry = function (k, v) {
	this.key = k;
	this.value = v;
	this.valueOf = function () {
		return this.value;
	};
	this.toString = function () {
		return String(this.value);
	};
};
dojo.collections.Iterator = function (arr) {
	var a = arr;
	var position = 0;
	this.element = a[position] || null;
	this.atEnd = function () {
		return (position >= a.length);
	};
	this.get = function () {
		if (this.atEnd()) {
			return null;
		}
		this.element = a[position++];
		return this.element;
	};
	this.map = function (fn, scope) {
		var s = scope || dj_global;
		if (Array.map) {
			return Array.map(a, fn, s);
		} else {
			var arr = [];
			for (var i = 0; i < a.length; i++) {
				arr.push(fn.call(s, a[i]));
			}
			return arr;
		}
	};
	this.reset = function () {
		position = 0;
		this.element = a[position];
	};
};
dojo.collections.DictionaryIterator = function (obj) {
	var a = [];
	var testObject = {};
	for (var p in obj) {
		if (!testObject[p]) {
			a.push(obj[p]);
		}
	}
	var position = 0;
	this.element = a[position] || null;
	this.atEnd = function () {
		return (position >= a.length);
	};
	this.get = function () {
		if (this.atEnd()) {
			return null;
		}
		this.element = a[position++];
		return this.element;
	};
	this.map = function (fn, scope) {
		var s = scope || dj_global;
		if (Array.map) {
			return Array.map(a, fn, s);
		} else {
			var arr = [];
			for (var i = 0; i < a.length; i++) {
				arr.push(fn.call(s, a[i]));
			}
			return arr;
		}
	};
	this.reset = function () {
		position = 0;
		this.element = a[position];
	};
};

