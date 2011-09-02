/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.uri.cache");
dojo.uri.cache = {_cache:{}, set:function (uri, content) {
	this._cache[uri.toString()] = content;
	return uri;
}, remove:function (uri) {
	delete this._cache[uri.toString()];
}, get:function (uri) {
	var key = uri.toString();
	var value = this._cache[key];
	if (!value) {
		value = dojo.hostenv.getText(key);
		if (value) {
			this._cache[key] = value;
		}
	}
	return value;
}, allow:function (uri) {
	return uri;
}};

