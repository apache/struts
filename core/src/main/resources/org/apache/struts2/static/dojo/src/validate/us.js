/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.validate.us");
dojo.require("dojo.validate.common");

dojo.validate.us.isCurrency = function(/*String*/value, /*Object?*/flags){
	// summary: Validates U.S. currency
	// value: the representation to check
	// flags: flags in validate.isCurrency can be applied.
	return dojo.validate.isCurrency(value, flags); // Boolean
}


dojo.validate.us.isState = function(/*String*/value, /*Object?*/flags){
	// summary: Validates US state and territory abbreviations.
	//
	// value: A two character string
	// flags: An object
	//    flags.allowTerritories  Allow Guam, Puerto Rico, etc.  Default is true.
	//    flags.allowMilitary  Allow military 'states', e.g. Armed Forces Europe (AE).  Default is true.

	var re = new RegExp("^" + dojo.regexp.us.state(flags) + "$", "i");
	return re.test(value); // Boolean
}

dojo.validate.us.isPhoneNumber = function(/*String*/value){
	// summary: Validates 10 US digit phone number for several common formats
	// value: The telephone number string

	var flags = {
		format: [
			"###-###-####",
			"(###) ###-####",
			"(###) ### ####",
			"###.###.####",
			"###/###-####",
			"### ### ####",
			"###-###-#### x#???",
			"(###) ###-#### x#???",
			"(###) ### #### x#???",
			"###.###.#### x#???",
			"###/###-#### x#???",
			"### ### #### x#???",
			"##########"
		]
	};

	return dojo.validate.isNumberFormat(value, flags); // Boolean
}

dojo.validate.us.isSocialSecurityNumber = function(/*String*/value){
// summary: Validates social security number
	var flags = {
		format: [
			"###-##-####",
			"### ## ####",
			"#########"
		]
	};

	return dojo.validate.isNumberFormat(value, flags); // Boolean
}

dojo.validate.us.isZipCode = function(/*String*/value){
// summary: Validates U.S. zip-code
	var flags = {
		format: [
			"#####-####",
			"##### ####",
			"#########",
			"#####"
		]
	};

	return dojo.validate.isNumberFormat(value, flags); // Boolean
}
