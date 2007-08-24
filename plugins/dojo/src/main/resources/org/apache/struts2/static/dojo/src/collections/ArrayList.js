/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.collections.ArrayList");
dojo.require("dojo.collections.Collections");
dojo.collections.ArrayList = function (arr) {
	var items = [];
	if (arr) {
		items = items.concat(arr);
	}
	this.count = items.length;
	this.add = function (obj) {
		items.push(obj);
		this.count = items.length;
	};
	this.addRange = function (a) {
		if (a.getIterator) {
			var e = a.getIterator();
			while (!e.atEnd()) {
				this.add(e.get());
			}
			this.count = items.length;
		} else {
			for (var i = 0; i < a.length; i++) {
				items.push(a[i]);
			}
			this.count = items.length;
		}
	};
	this.clear = function () {
		items.splice(0, items.length);
		this.count = 0;
	};
	this.clone = function () {
		return new dojo.collections.ArrayList(items);
	};
	this.contains = function (obj) {
		for (var i = 0; i < items.length; i++) {
			if (items[i] == obj) {
				return true;
			}
		}
		return false;
	};
	this.forEach = function (fn, scope) {
		var s = scope || dj_global;
		if (Array.forEach) {
			Array.forEach(items, fn, s);
		} else {
			for (var i = 0; i < items.length; i++) {
				fn.call(s, items[i], i, items);
			}
		}
	};
	this.getIterator = function () {
		return new dojo.collections.Iterator(items);
	};
	this.indexOf = function (obj) {
		for (var i = 0; i < items.length; i++) {
			if (items[i] == obj) {
				return i;
			}
		}
		return -1;
	};
	this.insert = function (i, obj) {
		items.splice(i, 0, obj);
		this.count = items.length;
	};
	this.item = function (i) {
		return items[i];
	};
	this.remove = function (obj) {
		var i = this.indexOf(obj);
		if (i >= 0) {
			items.splice(i, 1);
		}
		this.count = items.length;
	};
	this.removeAt = function (i) {
		items.splice(i, 1);
		this.count = items.length;
	};
	this.reverse = function () {
		items.reverse();
	};
	this.sort = function (fn) {
		if (fn) {
			items.sort(fn);
		} else {
			items.sort();
		}
	};
	this.setByIndex = function (i, obj) {
		items[i] = obj;
		this.count = items.length;
	};
	this.toArray = function () {
		return [].concat(items);
	};
	this.toString = function (delim) {
		return items.join((delim || ","));
	};
};

