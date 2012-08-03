/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.AdapterRegistry");
dojo.require("dojo.lang.func");
dojo.AdapterRegistry = function (returnWrappers) {
	this.pairs = [];
	this.returnWrappers = returnWrappers || false;
};
dojo.lang.extend(dojo.AdapterRegistry, {register:function (name, check, wrap, directReturn, override) {
	var type = (override) ? "unshift" : "push";
	this.pairs[type]([name, check, wrap, directReturn]);
}, match:function () {
	for (var i = 0; i < this.pairs.length; i++) {
		var pair = this.pairs[i];
		if (pair[1].apply(this, arguments)) {
			if ((pair[3]) || (this.returnWrappers)) {
				return pair[2];
			} else {
				return pair[2].apply(this, arguments);
			}
		}
	}
	throw new Error("No match found");
}, unregister:function (name) {
	for (var i = 0; i < this.pairs.length; i++) {
		var pair = this.pairs[i];
		if (pair[0] == name) {
			this.pairs.splice(i, 1);
			return true;
		}
	}
	return false;
}});

