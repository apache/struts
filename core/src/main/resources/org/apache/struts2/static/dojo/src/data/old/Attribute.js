/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.data.old.Attribute");
dojo.require("dojo.data.old.Item");
dojo.require("dojo.lang.assert");

// -------------------------------------------------------------------
// Constructor
// -------------------------------------------------------------------
dojo.data.old.Attribute = function(/* dojo.data.old.provider.Base */ dataProvider, /* string */ attributeId) {
	/**
	 * summary:
	 * An Attribute object represents something like a column in 
	 * a relational database.
	 */
	dojo.lang.assertType(dataProvider, dojo.data.old.provider.Base, {optional: true});
	dojo.lang.assertType(attributeId, String);
	dojo.data.old.Item.call(this, dataProvider);
	this._attributeId = attributeId;
};
dojo.inherits(dojo.data.old.Attribute, dojo.data.old.Item);

// -------------------------------------------------------------------
// Public instance methods
// -------------------------------------------------------------------
dojo.data.old.Attribute.prototype.toString = function() {
	return this._attributeId; // string
};

dojo.data.old.Attribute.prototype.getAttributeId = function() {
	/**
	 * summary: 
	 * Returns the string token that uniquely identifies this
	 * attribute within the context of a data provider.
	 * For a data provider that accesses relational databases,
	 * typical attributeIds might be tokens like "name", "age", 
	 * "ssn", or "dept_key".
	 */ 
	return this._attributeId; // string
};

dojo.data.old.Attribute.prototype.getType = function() {
	/**
	 * summary: Returns the data type of the values of this attribute.
	 */ 
	return this.get('type'); // dojo.data.old.Type or null
};

dojo.data.old.Attribute.prototype.setType = function(/* dojo.data.old.Type or null */ type) {
	/**
	 * summary: Sets the data type for this attribute.
	 */ 
	this.set('type', type);
};
