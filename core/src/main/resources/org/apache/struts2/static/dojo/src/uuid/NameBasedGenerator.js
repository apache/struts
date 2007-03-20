/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.uuid.NameBasedGenerator");

dojo.uuid.NameBasedGenerator = new function() {
	this.generate = function(/* constructor? */ returnType) {
		// summary: 
		//   This function generates name-based UUIDs, meaning "version 3" 
		//   and "version 5" UUIDs.
		// returnType: The type of object to return. Usually String or dojo.uuid.Uuid

		// examples: 
		//   var string = dojo.uuid.NameBasedGenerator.generate();
		//   var string = dojo.uuid.NameBasedGenerator.generate(String);
		//   var uuid   = dojo.uuid.NameBasedGenerator.generate(dojo.uuid.Uuid);

		dojo.unimplemented('dojo.uuid.NameBasedGenerator.generate');
		
		// FIXME:
		// For an algorithm to generate name-based UUIDs, 
		// see sections 4.3 of RFC 4122:
		//  http://www.ietf.org/rfc/rfc4122.txt
		
		var returnValue = "00000000-0000-0000-0000-000000000000"; // FIXME
		if (returnType && (returnType != String)) {
			returnValue = new returnType(returnValue);
		}
		return returnValue; // object
	};
}();