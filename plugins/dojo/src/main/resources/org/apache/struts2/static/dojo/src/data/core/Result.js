/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.data.core.Result");
dojo.require("dojo.lang.declare");
dojo.require("dojo.experimental");
dojo.experimental("dojo.data.core.Result");
dojo.declare("dojo.data.core.Result", null, {initializer:function (keywordArgs, store) {
	this.fromKwArgs(keywordArgs || {});
	this.items = null;
	this.resultMetadata = null;
	this.length = -1;
	this.store = store;
	this._aborted = false;
	this._abortFunc = null;
}, sync:true, abort:function () {
	this._aborted = true;
	if (this._abortFunc) {
		this._abortFunc();
	}
}, fromKwArgs:function (kwArgs) {
	if (typeof kwArgs.saveResult == "undefined") {
		this.saveResult = kwArgs.onnext ? false : true;
	}
	dojo.lang.mixin(this, kwArgs);
}});

