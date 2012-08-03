/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.data.old.Observable");
dojo.require("dojo.lang.common");
dojo.require("dojo.lang.assert");
dojo.data.old.Observable = function () {
};
dojo.data.old.Observable.prototype.addObserver = function (observer) {
	dojo.lang.assertType(observer, Object);
	dojo.lang.assertType(observer.observedObjectHasChanged, Function);
	if (!this._arrayOfObservers) {
		this._arrayOfObservers = [];
	}
	if (!dojo.lang.inArray(this._arrayOfObservers, observer)) {
		this._arrayOfObservers.push(observer);
	}
};
dojo.data.old.Observable.prototype.removeObserver = function (observer) {
	if (!this._arrayOfObservers) {
		return;
	}
	var index = dojo.lang.indexOf(this._arrayOfObservers, observer);
	if (index != -1) {
		this._arrayOfObservers.splice(index, 1);
	}
};
dojo.data.old.Observable.prototype.getObservers = function () {
	return this._arrayOfObservers;
};

