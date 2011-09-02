/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.data.CsvStore");
dojo.require("dojo.data.core.RemoteStore");
dojo.require("dojo.lang.assert");
dojo.declare("dojo.data.CsvStore", dojo.data.core.RemoteStore, {_setupQueryRequest:function (result, requestKw) {
	var serverQueryUrl = this._serverQueryUrl ? this._serverQueryUrl : "";
	var queryUrl = result.query ? result.query : "";
	requestKw.url = serverQueryUrl + queryUrl;
	requestKw.method = "get";
}, _resultToQueryData:function (serverResponseData) {
	var csvFileContentString = serverResponseData;
	var arrayOfArrays = this._getArrayOfArraysFromCsvFileContents(csvFileContentString);
	var arrayOfObjects = this._getArrayOfObjectsFromArrayOfArrays(arrayOfArrays);
	var remoteStoreData = this._getRemoteStoreDataFromArrayOfObjects(arrayOfObjects);
	return remoteStoreData;
}, _setupSaveRequest:function (saveKeywordArgs, requestKw) {
}, _getArrayOfArraysFromCsvFileContents:function (csvFileContents) {
	dojo.lang.assertType(csvFileContents, String);
	var lineEndingCharacters = new RegExp("\r\n|\n|\r");
	var leadingWhiteSpaceCharacters = new RegExp("^\\s+", "g");
	var trailingWhiteSpaceCharacters = new RegExp("\\s+$", "g");
	var doubleQuotes = new RegExp("\"\"", "g");
	var arrayOfOutputRecords = [];
	var arrayOfInputLines = csvFileContents.split(lineEndingCharacters);
	for (var i in arrayOfInputLines) {
		var singleLine = arrayOfInputLines[i];
		if (singleLine.length > 0) {
			var listOfFields = singleLine.split(",");
			var j = 0;
			while (j < listOfFields.length) {
				var space_field_space = listOfFields[j];
				var field_space = space_field_space.replace(leadingWhiteSpaceCharacters, "");
				var field = field_space.replace(trailingWhiteSpaceCharacters, "");
				var firstChar = field.charAt(0);
				var lastChar = field.charAt(field.length - 1);
				var secondToLastChar = field.charAt(field.length - 2);
				var thirdToLastChar = field.charAt(field.length - 3);
				if ((firstChar == "\"") && ((lastChar != "\"") || ((lastChar == "\"") && (secondToLastChar == "\"") && (thirdToLastChar != "\"")))) {
					if (j + 1 === listOfFields.length) {
						return null;
					}
					var nextField = listOfFields[j + 1];
					listOfFields[j] = field_space + "," + nextField;
					listOfFields.splice(j + 1, 1);
				} else {
					if ((firstChar == "\"") && (lastChar == "\"")) {
						field = field.slice(1, (field.length - 1));
						field = field.replace(doubleQuotes, "\"");
					}
					listOfFields[j] = field;
					j += 1;
				}
			}
			arrayOfOutputRecords.push(listOfFields);
		}
	}
	return arrayOfOutputRecords;
}, _getArrayOfObjectsFromArrayOfArrays:function (arrayOfArrays) {
	dojo.lang.assertType(arrayOfArrays, Array);
	var arrayOfItems = [];
	if (arrayOfArrays.length > 1) {
		var arrayOfKeys = arrayOfArrays[0];
		for (var i = 1; i < arrayOfArrays.length; ++i) {
			var row = arrayOfArrays[i];
			var item = {};
			for (var j in row) {
				var value = row[j];
				var key = arrayOfKeys[j];
				item[key] = value;
			}
			arrayOfItems.push(item);
		}
	}
	return arrayOfItems;
}, _getRemoteStoreDataFromArrayOfObjects:function (arrayOfObjects) {
	dojo.lang.assertType(arrayOfObjects, Array);
	var output = {};
	for (var i = 0; i < arrayOfObjects.length; ++i) {
		var object = arrayOfObjects[i];
		for (var key in object) {
			var value = object[key];
			object[key] = [value];
		}
		output[i] = object;
	}
	return output;
}, newItem:function (attributes, keywordArgs) {
	dojo.unimplemented("dojo.data.CsvStore.newItem");
}, deleteItem:function (item) {
	dojo.unimplemented("dojo.data.CsvStore.deleteItem");
}, setValues:function (item, attribute, values) {
	dojo.unimplemented("dojo.data.CsvStore.setValues");
}, set:function (item, attribute, value) {
	dojo.unimplemented("dojo.data.CsvStore.set");
}, unsetAttribute:function (item, attribute) {
	dojo.unimplemented("dojo.data.CsvStore.unsetAttribute");
}, save:function (keywordArgs) {
	dojo.unimplemented("dojo.data.CsvStore.save");
}, revert:function () {
	dojo.unimplemented("dojo.data.CsvStore.revert");
}, isDirty:function (item) {
	dojo.unimplemented("dojo.data.CsvStore.isDirty");
}});

