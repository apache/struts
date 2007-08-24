/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.data.old.Value");
dojo.require("dojo.lang.assert");
dojo.data.old.Value = function (value) {
	this._value = value;
	this._type = null;
};
dojo.data.old.Value.prototype.toString = function () {
	return this._value.toString();
};
dojo.data.old.Value.prototype.getValue = function () {
	return this._value;
};
dojo.data.old.Value.prototype.getType = function () {
	dojo.unimplemented("dojo.data.old.Value.prototype.getType");
	return this._type;
};
dojo.data.old.Value.prototype.compare = function () {
	dojo.unimplemented("dojo.data.old.Value.prototype.compare");
};
dojo.data.old.Value.prototype.isEqual = function () {
	dojo.unimplemented("dojo.data.old.Value.prototype.isEqual");
};

