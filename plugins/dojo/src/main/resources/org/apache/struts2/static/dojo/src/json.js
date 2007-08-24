/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.json");
dojo.require("dojo.lang.func");
dojo.require("dojo.string.extras");
dojo.require("dojo.AdapterRegistry");
dojo.json = {jsonRegistry:new dojo.AdapterRegistry(), register:function (name, check, wrap, override) {
	dojo.json.jsonRegistry.register(name, check, wrap, override);
}, evalJson:function (json) {
	try {
		return eval("(" + json + ")");
	}
	catch (e) {
		dojo.debug(e);
		return json;
	}
}, serialize:function (o) {
	var objtype = typeof (o);
	if (objtype == "undefined") {
		return "undefined";
	} else {
		if ((objtype == "number") || (objtype == "boolean")) {
			return o + "";
		} else {
			if (o === null) {
				return "null";
			}
		}
	}
	if (objtype == "string") {
		return dojo.string.escapeString(o);
	}
	var me = arguments.callee;
	var newObj;
	if (typeof (o.__json__) == "function") {
		newObj = o.__json__();
		if (o !== newObj) {
			return me(newObj);
		}
	}
	if (typeof (o.json) == "function") {
		newObj = o.json();
		if (o !== newObj) {
			return me(newObj);
		}
	}
	if (objtype != "function" && typeof (o.length) == "number") {
		var res = [];
		for (var i = 0; i < o.length; i++) {
			var val = me(o[i]);
			if (typeof (val) != "string") {
				val = "undefined";
			}
			res.push(val);
		}
		return "[" + res.join(",") + "]";
	}
	try {
		window.o = o;
		newObj = dojo.json.jsonRegistry.match(o);
		return me(newObj);
	}
	catch (e) {
	}
	if (objtype == "function") {
		return null;
	}
	res = [];
	for (var k in o) {
		var useKey;
		if (typeof (k) == "number") {
			useKey = "\"" + k + "\"";
		} else {
			if (typeof (k) == "string") {
				useKey = dojo.string.escapeString(k);
			} else {
				continue;
			}
		}
		val = me(o[k]);
		if (typeof (val) != "string") {
			continue;
		}
		res.push(useKey + ":" + val);
	}
	return "{" + res.join(",") + "}";
}};

