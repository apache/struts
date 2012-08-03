/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.data.csv.CsvStore");
dojo.require("dojo.data.Read");
dojo.require("dojo.lang.assert");

dojo.declare("dojo.data.csv.CsvStore", dojo.data.Read, {
	/* summary:
	 *   The CsvStore implements the dojo.data.Read API.  
	 */
	 
	/* examples:
	 *   var csvStore = new dojo.data.csv.CsvStore({url:"movies.csv");
	 *   var csvStore = new dojo.data.csv.CsvStore({url:"http://example.com/movies.csv");
	 *   var fileContents = dojo.hostenv.getText("movies.csv");
	 *   var csvStore = new dojo.data.csv.CsvStore({string:fileContents);
	 */
	initializer: 
		function(/* object */ keywordParameters) {
			// keywordParameters: {string: String, url: String}
			this._arrayOfItems = [];
			this._loadFinished = false;
			this._csvFileUrl = keywordParameters["url"];
			this._csvFileContents = keywordParameters["string"];
		},
	get:
		function(/* item */ item, /* attribute or attribute-name-string */ attribute, /* value? */ defaultValue) {
			// summary: See dojo.data.Read.get()
			var attributeValue = item[attribute] || defaultValue;
			return attributeValue; // a literal, an item, null, or undefined (never an array)
		},
	getValues:
		function(/* item */ item, /* attribute or attribute-name-string */ attribute) {
			// summary: See dojo.data.Read.getValues()
			var array = [this.get(item, attribute)];
			return array; // an array that may contain literals and items
		},
	getAttributes:
		function(/* item */ item) {
			// summary: See dojo.data.Read.getAttributes()
			var array = this._arrayOfKeys;
			return array; // array
		},
	hasAttribute:
		function(/* item */ item, /* attribute or attribute-name-string */ attribute) {
			// summary: See dojo.data.Read.hasAttribute()
			for (var i in this._arrayOfKeys) {
				if (this._arrayOfKeys[i] == attribute) {
					return true;
				}
			}
			return false; // boolean
		},
	hasAttributeValue:
		function(/* item */ item, /* attribute or attribute-name-string */ attribute, /* anything */ value) {
			// summary: See dojo.data.Read.hasAttributeValue()
			return (this.get(item, attribute) == value); // boolean
		},
	isItem:
		function(/* anything */ something) {
			// summary: See dojo.data.Read.isItem()
			for (var i in this._arrayOfItems) {
				if (this._arrayOfItems[i] == something) {
					return true;
				}
			}
			return false; // boolean
		},
	find:
		function(/* implementation-dependent */ query, /* object */ optionalKeywordArgs ) {
			// summary: See dojo.data.Read.find()
			if (!this._loadFinished) {
				if (this._csvFileUrl) {
					this._csvFileContents = dojo.hostenv.getText(this._csvFileUrl);
				}
				var arrayOfArrays = this._getArrayOfArraysFromCsvFileContents(this._csvFileContents);
				if (arrayOfArrays.length == 0) {
					this._arrayOfKeys = [];
				} else {
					this._arrayOfKeys = arrayOfArrays[0];
				}
				this._arrayOfItems = this._getArrayOfItemsFromArrayOfArrays(arrayOfArrays);
			}
			var result = new dojo.data.csv.Result(this._arrayOfItems, this);
			return result; // dojo.data.csv.Result
		},
	getIdentity:
		function(/* item */ item) {
			// summary: See dojo.data.Read.getIdentity()
			for (var i in this._arrayOfItems) {
				if (this._arrayOfItems[i] == item) {
					return i;
				}
			}
			return null; // boolean
		},
	getByIdentity:
		function(/* string */ id) {
			// summary: See dojo.data.Read.getByIdentity()
			var i = parseInt(id);
			if (i < this._arrayOfItems.length) {
				return this._arrayOfItems[i];
			} else {
				return null;
			}
		},

	// -------------------------------------------------------------------
	// Private methods
	_getArrayOfArraysFromCsvFileContents:
		function(/* string */ csvFileContents) {
			/* summary:
			 *   Parses a string of CSV records into a nested array structure.
			 * description:
			 *   Given a string containing CSV records, this method parses
			 *   the string and returns a data structure containing the parsed
			 *   content.  The data structure we return is an array of length
			 *   R, where R is the number of rows (lines) in the CSV data.  The 
			 *   return array contains one sub-array for each CSV line, and each 
			 *   sub-array contains C string values, where C is the number of 
			 *   columns in the CSV data.
			 */
			 
			/* example:
			 *   For example, given this CSV string as input:
			 *     "Title, Year, Producer \n Alien, 1979, Ridley Scott \n Blade Runner, 1982, Ridley Scott"
			 *   We will return this data structure:
			 *     [["Title", "Year", "Producer"]
			 *      ["Alien", "1979", "Ridley Scott"],  
			 *      ["Blade Runner", "1982", "Ridley Scott"]]
			 */
			dojo.lang.assertType(csvFileContents, String);
			
			var lineEndingCharacters = new RegExp("\r\n|\n|\r");
			var leadingWhiteSpaceCharacters = new RegExp("^\\s+",'g');
			var trailingWhiteSpaceCharacters = new RegExp("\\s+$",'g');
			var doubleQuotes = new RegExp('""','g');
			var arrayOfOutputRecords = [];
			
			var arrayOfInputLines = csvFileContents.split(lineEndingCharacters);
			for (var i in arrayOfInputLines) {
				var singleLine = arrayOfInputLines[i];
				if (singleLine.length > 0) {
					var listOfFields = singleLine.split(',');
					var j = 0;
					while (j < listOfFields.length) {
						var space_field_space = listOfFields[j];
						var field_space = space_field_space.replace(leadingWhiteSpaceCharacters, ''); // trim leading whitespace
						var field = field_space.replace(trailingWhiteSpaceCharacters, ''); // trim trailing whitespace
						var firstChar = field.charAt(0);
						var lastChar = field.charAt(field.length - 1);
						var secondToLastChar = field.charAt(field.length - 2);
						var thirdToLastChar = field.charAt(field.length - 3);
						if ((firstChar == '"') && 
								((lastChar != '"') || 
								 ((lastChar == '"') && (secondToLastChar == '"') && (thirdToLastChar != '"')) )) {
							if (j+1 === listOfFields.length) {
								// alert("The last field in record " + i + " is corrupted:\n" + field);
								return null;
							}
							var nextField = listOfFields[j+1];
							listOfFields[j] = field_space + ',' + nextField;
							listOfFields.splice(j+1, 1); // delete element [j+1] from the list
						} else {
							if ((firstChar == '"') && (lastChar == '"')) {
								field = field.slice(1, (field.length - 1)); // trim the " characters off the ends
								field = field.replace(doubleQuotes, '"');   // replace "" with "
							}
							listOfFields[j] = field;
							j += 1;
						}
					}
					arrayOfOutputRecords.push(listOfFields);
				}
			}
			return arrayOfOutputRecords; // Array
		},

	_getArrayOfItemsFromArrayOfArrays:
		function(/* array */ arrayOfArrays) {
			/* summary:
			 *   Converts a nested array structure into an array of keyword objects.
			 */
			 
			/* example:
			 *   For example, given this as input:
			 *     [["Title", "Year", "Producer"]
			 *      ["Alien", "1979", "Ridley Scott"],  
			 *      ["Blade Runner", "1982", "Ridley Scott"]]
			 *   We will return this as output:
			 *     [{"Title":"Alien", "Year":"1979", "Producer":"Ridley Scott"},
			 *      {"Title":"Blade Runner", "Year":"1982", "Producer":"Ridley Scott"}]
			 */
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
			return arrayOfItems; // Array
		}
});

