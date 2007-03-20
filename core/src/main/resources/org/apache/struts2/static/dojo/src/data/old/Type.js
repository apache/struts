/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.data.old.Type");
dojo.require("dojo.data.old.Item");

// -------------------------------------------------------------------
// Constructor
// -------------------------------------------------------------------
dojo.data.old.Type = function(/* dojo.data.old.provider.Base */ dataProvider) {
	/**
	 * summary:
	 * A Type represents a type of value, like Text, Number, Picture,
	 * or Varchar.
	 */
	dojo.data.old.Item.call(this, dataProvider);
};
dojo.inherits(dojo.data.old.Type, dojo.data.old.Item);
