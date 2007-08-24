/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.collections.Dictionary");
dojo.require("dojo.collections.Collections");
dojo.collections.Dictionary = function (dictionary) {
	var items = {};
	this.count = 0;
	var testObject = {};
	this.add = function (k, v) {
		var b = (k in items);
		items[k] = new dojo.collections.DictionaryEntry(k, v);
		if (!b) {
			this.count++;
		}
	};
	this.clear = function () {
		items = {};
		this.count = 0;
	};
	this.clone = function () {
		return new dojo.collections.Dictionary(this);
	};
	this.contains = this.containsKey = function (k) {
		if (testObject[k]) {
			return false;
		}
		return (items[k] != null);
	};
	this.containsValue = function (v) {
		var e = this.getIterator();
		while (e.get()) {
			if (e.element.value == v) {
				return true;
			}
		}
		return false;
	};
	this.entry = function (k) {
		return items[k];
	};
	this.forEach = function (fn, scope) {
		var a = [];
		for (var p in items) {
			if (!testObject[p]) {
				a.push(items[p]);
			}
		}
		var s = scope || dj_global;
		if (Array.forEach) {
			Array.forEach(a, fn, s);
		} else {
			for (var i = 0; i < a.length; i++) {
				fn.call(s, a[i], i, a);
			}
		}
	};
	this.getKeyList = function () {
		return (this.getIterator()).map(function (entry) {
			return entry.key;
		});
	};
	this.getValueList = function () {
		return (this.getIterator()).map(function (entry) {
			return entry.value;
		});
	};
	this.item = function (k) {
		if (k in items) {
			return items[k].valueOf();
		}
		return undefined;
	};
	this.getIterator = function () {
		return new dojo.collections.DictionaryIterator(items);
	};
	this.remove = function (k) {
		if (k in items && !testObject[k]) {
			delete items[k];
			this.count--;
			return true;
		}
		return false;
	};
	if (dictionary) {
		var e = dictionary.getIterator();
		while (e.get()) {
			this.add(e.element.key, e.element.value);
		}
	}
};

