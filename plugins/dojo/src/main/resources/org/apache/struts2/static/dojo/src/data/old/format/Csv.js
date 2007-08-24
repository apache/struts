/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/



dojo.provide("dojo.data.old.format.Csv");
dojo.require("dojo.lang.assert");
dojo.data.old.format.Csv = new function () {
	this.getArrayStructureFromCsvFileContents = function (csvFileContents) {
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
	};
	this.loadDataProviderFromFileContents = function (dataProvider, csvFileContents) {
		dojo.lang.assertType(dataProvider, dojo.data.old.provider.Base);
		dojo.lang.assertType(csvFileContents, String);
		var arrayOfArrays = this.getArrayStructureFromCsvFileContents(csvFileContents);
		if (arrayOfArrays) {
			var arrayOfKeys = arrayOfArrays[0];
			for (var i = 1; i < arrayOfArrays.length; ++i) {
				var row = arrayOfArrays[i];
				var item = dataProvider.getNewItemToLoad();
				for (var j in row) {
					var value = row[j];
					var key = arrayOfKeys[j];
					item.load(key, value);
				}
			}
		}
	};
	this.getCsvStringFromResultSet = function (resultSet) {
		dojo.unimplemented("dojo.data.old.format.Csv.getCsvStringFromResultSet");
		var csvString = null;
		return csvString;
	};
}();

