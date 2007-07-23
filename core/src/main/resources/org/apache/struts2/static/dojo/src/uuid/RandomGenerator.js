/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.uuid.RandomGenerator");

dojo.uuid.RandomGenerator = new function() {
	this.generate = function(/* constructor? */ returnType) {
		// summary: 
		//   This function generates random UUIDs, meaning "version 4" UUIDs.
		// description: 
		//   A typical generated value would be something like this:
		//   "3b12f1df-5232-4804-897e-917bf397618a"
		// returnType: The type of object to return. Usually String or dojo.uuid.Uuid

		// examples: 
		//   var string = dojo.uuid.RandomGenerator.generate();
		//   var string = dojo.uuid.RandomGenerator.generate(String);
		//   var uuid   = dojo.uuid.RandomGenerator.generate(dojo.uuid.Uuid);

		dojo.unimplemented('dojo.uuid.RandomGenerator.generate');
		// FIXME:
		// For an algorithm to generate a random UUID, see
		// sections 4.4 and 4.5 of RFC 4122:
		//  http://www.ietf.org/rfc/rfc4122.txt
		
		var returnValue = "00000000-0000-0000-0000-000000000000"; // FIXME
		if (returnType && (returnType != String)) {
			returnValue = new returnType(returnValue);
		}
		return returnValue; // object
	};
}();
