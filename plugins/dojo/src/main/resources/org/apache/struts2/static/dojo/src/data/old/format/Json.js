/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.data.old.format.Json");
dojo.require("dojo.lang.assert");
dojo.data.old.format.Json = new function () {
	this.loadDataProviderFromFileContents = function (dataProvider, jsonFileContents) {
		dojo.lang.assertType(dataProvider, dojo.data.old.provider.Base);
		dojo.lang.assertType(jsonFileContents, String);
		var arrayOfJsonData = eval("(" + jsonFileContents + ")");
		this.loadDataProviderFromArrayOfJsonData(dataProvider, arrayOfJsonData);
	};
	this.loadDataProviderFromArrayOfJsonData = function (dataProvider, arrayOfJsonData) {
		dojo.lang.assertType(arrayOfJsonData, Array, {optional:true});
		if (arrayOfJsonData && (arrayOfJsonData.length > 0)) {
			var firstRow = arrayOfJsonData[0];
			dojo.lang.assertType(firstRow, [Array, "pureobject"]);
			if (dojo.lang.isArray(firstRow)) {
				_loadDataProviderFromArrayOfArrays(dataProvider, arrayOfJsonData);
			} else {
				dojo.lang.assertType(firstRow, "pureobject");
				_loadDataProviderFromArrayOfObjects(dataProvider, arrayOfJsonData);
			}
		}
	};
	this.getJsonStringFromResultSet = function (resultSet) {
		dojo.unimplemented("dojo.data.old.format.Json.getJsonStringFromResultSet");
		var jsonString = null;
		return jsonString;
	};
	function _loadDataProviderFromArrayOfArrays(dataProvider, arrayOfJsonData) {
		var arrayOfKeys = arrayOfJsonData[0];
		for (var i = 1; i < arrayOfJsonData.length; ++i) {
			var row = arrayOfJsonData[i];
			var item = dataProvider.getNewItemToLoad();
			for (var j in row) {
				var value = row[j];
				var key = arrayOfKeys[j];
				item.load(key, value);
			}
		}
	}
	function _loadDataProviderFromArrayOfObjects(dataProvider, arrayOfJsonData) {
		for (var i in arrayOfJsonData) {
			var row = arrayOfJsonData[i];
			var item = dataProvider.getNewItemToLoad();
			for (var key in row) {
				var value = row[key];
				if (dojo.lang.isArray(value)) {
					var arrayOfValues = value;
					for (var j in arrayOfValues) {
						value = arrayOfValues[j];
						item.load(key, value);
					}
				} else {
					item.load(key, value);
				}
			}
		}
	}
}();

