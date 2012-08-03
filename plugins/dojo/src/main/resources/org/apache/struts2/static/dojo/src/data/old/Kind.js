/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.data.old.Kind");
dojo.require("dojo.data.old.Item");
dojo.data.old.Kind = function (dataProvider) {
	dojo.data.old.Item.call(this, dataProvider);
};
dojo.inherits(dojo.data.old.Kind, dojo.data.old.Item);

