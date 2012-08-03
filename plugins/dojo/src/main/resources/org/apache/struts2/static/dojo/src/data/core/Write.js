/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.data.core.Write");
dojo.require("dojo.data.core.Read");
dojo.require("dojo.lang.declare");
dojo.require("dojo.experimental");
dojo.experimental("dojo.data.core.Write");
dojo.declare("dojo.data.core.Write", dojo.data.core.Read, {newItem:function (keywordArgs) {
	var newItem;
	dojo.unimplemented("dojo.data.core.Write.newItem");
	return newItem;
}, deleteItem:function (item) {
	dojo.unimplemented("dojo.data.core.Write.deleteItem");
	return false;
}, set:function (item, attribute, value) {
	dojo.unimplemented("dojo.data.core.Write.set");
	return false;
}, setValues:function (item, attribute, values) {
	dojo.unimplemented("dojo.data.core.Write.setValues");
	return false;
}, unsetAttribute:function (item, attribute) {
	dojo.unimplemented("dojo.data.core.Write.clear");
	return false;
}, save:function () {
	dojo.unimplemented("dojo.data.core.Write.save");
	return false;
}, revert:function () {
	dojo.unimplemented("dojo.data.core.Write.revert");
	return false;
}, isDirty:function (item) {
	dojo.unimplemented("dojo.data.core.Write.isDirty");
	return false;
}});

