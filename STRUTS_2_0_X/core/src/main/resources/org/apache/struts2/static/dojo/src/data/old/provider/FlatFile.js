/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.data.old.provider.FlatFile");
dojo.require("dojo.data.old.provider.Base");
dojo.require("dojo.data.old.Item");
dojo.require("dojo.data.old.Attribute");
dojo.require("dojo.data.old.ResultSet");
dojo.require("dojo.data.old.format.Json");
dojo.require("dojo.data.old.format.Csv");
dojo.require("dojo.lang.assert");

// -------------------------------------------------------------------
// Constructor
// -------------------------------------------------------------------
dojo.data.old.provider.FlatFile = function(/* keywords */ keywordParameters) {
	/**
	 * summary:
	 * A Json Data Provider knows how to read in simple JSON data
	 * tables and make their contents accessable as Items.
	 */
	dojo.lang.assertType(keywordParameters, "pureobject", {optional: true});
	dojo.data.old.provider.Base.call(this);
	this._arrayOfItems = [];
	this._resultSet = null;
	this._dictionaryOfAttributes = {};

	if (keywordParameters) {
		var jsonObjects = keywordParameters["jsonObjects"];
		var jsonString  = keywordParameters["jsonString"];
		var fileUrl     = keywordParameters["url"];
		if (jsonObjects) {
			dojo.data.old.format.Json.loadDataProviderFromArrayOfJsonData(this, jsonObjects);
		}
		if (jsonString) {
			dojo.data.old.format.Json.loadDataProviderFromFileContents(this, jsonString);
		}
		if (fileUrl) {
			var arrayOfParts = fileUrl.split('.');
			var lastPart = arrayOfParts[(arrayOfParts.length - 1)];
			var formatParser = null;
			if (lastPart == "json") {
				formatParser = dojo.data.old.format.Json;
			}
			if (lastPart == "csv") {
				formatParser = dojo.data.old.format.Csv;
			}
			if (formatParser) {
				var fileContents = dojo.hostenv.getText(fileUrl);
				formatParser.loadDataProviderFromFileContents(this, fileContents);
			} else {
				dojo.lang.assert(false, "new dojo.data.old.provider.FlatFile({url: }) was passed a file without a .csv or .json suffix");
			}
		}
	}
};
dojo.inherits(dojo.data.old.provider.FlatFile, dojo.data.old.provider.Base);

// -------------------------------------------------------------------
// Public instance methods
// -------------------------------------------------------------------
dojo.data.old.provider.FlatFile.prototype.getProviderCapabilities = function(/* string */ keyword) {
	dojo.lang.assertType(keyword, String, {optional: true});
	if (!this._ourCapabilities) {
		this._ourCapabilities = {
			transactions: false,
			undo: false,
			login: false,
			versioning: false,
			anonymousRead: true,
			anonymousWrite: false,
			permissions: false,
			queries: false,
			strongTyping: false,
			datatypes: [String, Date, Number]
		};
	}
	if (keyword) {
		return this._ourCapabilities[keyword];
	} else {
		return this._ourCapabilities;
	}
};

dojo.data.old.provider.FlatFile.prototype.registerAttribute = function(/* string or dojo.data.old.Attribute */ attributeId) {
	var registeredAttribute = this.getAttribute(attributeId);
	if (!registeredAttribute) {
		var newAttribute = new dojo.data.old.Attribute(this, attributeId);
		this._dictionaryOfAttributes[attributeId] = newAttribute;
		registeredAttribute = newAttribute;
	}
	return registeredAttribute; // dojo.data.old.Attribute
};

dojo.data.old.provider.FlatFile.prototype.getAttribute = function(/* string or dojo.data.old.Attribute */ attributeId) {
	var attribute = (this._dictionaryOfAttributes[attributeId] || null);
	return attribute; // dojo.data.old.Attribute or null
};

dojo.data.old.provider.FlatFile.prototype.getAttributes = function() {
	var arrayOfAttributes = [];
	for (var key in this._dictionaryOfAttributes) {
		var attribute = this._dictionaryOfAttributes[key];
		arrayOfAttributes.push(attribute);
	}
	return arrayOfAttributes; // Array
};

dojo.data.old.provider.FlatFile.prototype.fetchArray = function(query) {
	/**
	 * summary: Returns an Array containing all of the Items.
	 */ 
	return this._arrayOfItems; // Array
};

dojo.data.old.provider.FlatFile.prototype.fetchResultSet = function(query) {
	/**
	 * summary: Returns a ResultSet containing all of the Items.
	 */ 
	if (!this._resultSet) {
		this._resultSet = new dojo.data.old.ResultSet(this, this.fetchArray(query));
	}
	return this._resultSet; // dojo.data.old.ResultSet
};

// -------------------------------------------------------------------
// Private instance methods
// -------------------------------------------------------------------
dojo.data.old.provider.FlatFile.prototype._newItem = function() {
	var item = new dojo.data.old.Item(this);
	this._arrayOfItems.push(item);
	return item; // dojo.data.old.Item
};

dojo.data.old.provider.FlatFile.prototype._newAttribute = function(/* String */ attributeId) {
	dojo.lang.assertType(attributeId, String);
	dojo.lang.assert(this.getAttribute(attributeId) === null);
	var attribute = new dojo.data.old.Attribute(this, attributeId);
	this._dictionaryOfAttributes[attributeId] = attribute;
	return attribute; // dojo.data.old.Attribute
};

dojo.data.old.provider.Base.prototype._getResultSets = function() {
	return [this._resultSet]; // Array
};

