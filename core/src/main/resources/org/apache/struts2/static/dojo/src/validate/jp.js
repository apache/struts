/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.validate.jp");
dojo.require("dojo.validate.common");

dojo.validate.isJapaneseCurrency = function(/*String*/value) {
	//summary: checks to see if 'value' is a valid representation of Japanese currency
	var flags = {
		symbol: "\u00a5",
		fractional: false
	};
	return dojo.validate.isCurrency(value, flags); // Boolean
}


