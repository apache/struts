/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.data.old.ResultSet");
dojo.require("dojo.lang.assert");
dojo.require("dojo.collections.Collections");
dojo.data.old.ResultSet = function (dataProvider, arrayOfItems) {
	dojo.lang.assertType(dataProvider, dojo.data.old.provider.Base, {optional:true});
	dojo.lang.assertType(arrayOfItems, Array, {optional:true});
	dojo.data.old.Observable.call(this);
	this._dataProvider = dataProvider;
	this._arrayOfItems = [];
	if (arrayOfItems) {
		this._arrayOfItems = arrayOfItems;
	}
};
dojo.inherits(dojo.data.old.ResultSet, dojo.data.old.Observable);
dojo.data.old.ResultSet.prototype.toString = function () {
	var returnString = this._arrayOfItems.join(", ");
	return returnString;
};
dojo.data.old.ResultSet.prototype.toArray = function () {
	return this._arrayOfItems;
};
dojo.data.old.ResultSet.prototype.getIterator = function () {
	return new dojo.collections.Iterator(this._arrayOfItems);
};
dojo.data.old.ResultSet.prototype.getLength = function () {
	return this._arrayOfItems.length;
};
dojo.data.old.ResultSet.prototype.getItemAt = function (index) {
	return this._arrayOfItems[index];
};
dojo.data.old.ResultSet.prototype.indexOf = function (item) {
	return dojo.lang.indexOf(this._arrayOfItems, item);
};
dojo.data.old.ResultSet.prototype.contains = function (item) {
	return dojo.lang.inArray(this._arrayOfItems, item);
};
dojo.data.old.ResultSet.prototype.getDataProvider = function () {
	return this._dataProvider;
};

