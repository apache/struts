/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.data.old.provider.Base");
dojo.require("dojo.lang.assert");
dojo.data.old.provider.Base = function () {
	this._countOfNestedTransactions = 0;
	this._changesInCurrentTransaction = null;
};
dojo.data.old.provider.Base.prototype.beginTransaction = function () {
	if (this._countOfNestedTransactions === 0) {
		this._changesInCurrentTransaction = [];
	}
	this._countOfNestedTransactions += 1;
};
dojo.data.old.provider.Base.prototype.endTransaction = function () {
	this._countOfNestedTransactions -= 1;
	dojo.lang.assert(this._countOfNestedTransactions >= 0);
	if (this._countOfNestedTransactions === 0) {
		var listOfChangesMade = this._saveChanges();
		this._changesInCurrentTransaction = null;
		if (listOfChangesMade.length > 0) {
			this._notifyObserversOfChanges(listOfChangesMade);
		}
	}
};
dojo.data.old.provider.Base.prototype.getNewItemToLoad = function () {
	return this._newItem();
};
dojo.data.old.provider.Base.prototype.newItem = function (itemName) {
	dojo.lang.assertType(itemName, String, {optional:true});
	var item = this._newItem();
	if (itemName) {
		item.set("name", itemName);
	}
	return item;
};
dojo.data.old.provider.Base.prototype.newAttribute = function (attributeId) {
	dojo.lang.assertType(attributeId, String, {optional:true});
	var attribute = this._newAttribute(attributeId);
	return attribute;
};
dojo.data.old.provider.Base.prototype.getAttribute = function (attributeId) {
	dojo.unimplemented("dojo.data.old.provider.Base");
	var attribute;
	return attribute;
};
dojo.data.old.provider.Base.prototype.getAttributes = function () {
	dojo.unimplemented("dojo.data.old.provider.Base");
	return this._arrayOfAttributes;
};
dojo.data.old.provider.Base.prototype.fetchArray = function () {
	dojo.unimplemented("dojo.data.old.provider.Base");
	return [];
};
dojo.data.old.provider.Base.prototype.fetchResultSet = function () {
	dojo.unimplemented("dojo.data.old.provider.Base");
	var resultSet;
	return resultSet;
};
dojo.data.old.provider.Base.prototype.noteChange = function (item, attribute, value) {
	var change = {item:item, attribute:attribute, value:value};
	if (this._countOfNestedTransactions === 0) {
		this.beginTransaction();
		this._changesInCurrentTransaction.push(change);
		this.endTransaction();
	} else {
		this._changesInCurrentTransaction.push(change);
	}
};
dojo.data.old.provider.Base.prototype.addItemObserver = function (item, observer) {
	dojo.lang.assertType(item, dojo.data.old.Item);
	item.addObserver(observer);
};
dojo.data.old.provider.Base.prototype.removeItemObserver = function (item, observer) {
	dojo.lang.assertType(item, dojo.data.old.Item);
	item.removeObserver(observer);
};
dojo.data.old.provider.Base.prototype._newItem = function () {
	var item = new dojo.data.old.Item(this);
	return item;
};
dojo.data.old.provider.Base.prototype._newAttribute = function (attributeId) {
	var attribute = new dojo.data.old.Attribute(this);
	return attribute;
};
dojo.data.old.provider.Base.prototype._saveChanges = function () {
	var arrayOfChangesMade = this._changesInCurrentTransaction;
	return arrayOfChangesMade;
};
dojo.data.old.provider.Base.prototype._notifyObserversOfChanges = function (arrayOfChanges) {
	var arrayOfResultSets = this._getResultSets();
	for (var i in arrayOfChanges) {
		var change = arrayOfChanges[i];
		var changedItem = change.item;
		var arrayOfItemObservers = changedItem.getObservers();
		for (var j in arrayOfItemObservers) {
			var observer = arrayOfItemObservers[j];
			observer.observedObjectHasChanged(changedItem, change);
		}
		for (var k in arrayOfResultSets) {
			var resultSet = arrayOfResultSets[k];
			var arrayOfResultSetObservers = resultSet.getObservers();
			for (var m in arrayOfResultSetObservers) {
				observer = arrayOfResultSetObservers[m];
				observer.observedObjectHasChanged(resultSet, change);
			}
		}
	}
};
dojo.data.old.provider.Base.prototype._getResultSets = function () {
	dojo.unimplemented("dojo.data.old.provider.Base");
	return [];
};

