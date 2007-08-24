/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.data.core.Read");
dojo.require("dojo.data.core.Result");
dojo.require("dojo.lang.declare");
dojo.require("dojo.experimental");
dojo.experimental("dojo.data.core.Read");
dojo.declare("dojo.data.core.Read", null, {get:function (item, attribute, defaultValue) {
	dojo.unimplemented("dojo.data.core.Read.get");
	var attributeValue = null;
	return attributeValue;
}, getValues:function (item, attribute) {
	dojo.unimplemented("dojo.data.core.Read.getValues");
	var array = null;
	return array;
}, getAttributes:function (item) {
	dojo.unimplemented("dojo.data.core.Read.getAttributes");
	var array = null;
	return array;
}, hasAttribute:function (item, attribute) {
	dojo.unimplemented("dojo.data.core.Read.hasAttribute");
	return false;
}, containsValue:function (item, attribute, value) {
	dojo.unimplemented("dojo.data.core.Read.containsValue");
	return false;
}, isItem:function (something) {
	dojo.unimplemented("dojo.data.core.Read.isItem");
	return false;
}, isItemAvailable:function (something) {
	dojo.unimplemented("dojo.data.core.Read.isItemAvailable");
	return false;
}, find:function (keywordArgs) {
	dojo.unimplemented("dojo.data.core.Read.find");
	var result = null;
	return result;
}, getIdentity:function (item) {
	dojo.unimplemented("dojo.data.core.Read.getIdentity");
	var itemIdentifyString = null;
	return itemIdentifyString;
}, findByIdentity:function (identity) {
	dojo.unimplemented("dojo.data.core.Read.getByIdentity");
	var item = null;
	return item;
}});

