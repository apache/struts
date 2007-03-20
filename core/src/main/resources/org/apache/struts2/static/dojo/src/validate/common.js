/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.validate.common");

dojo.require("dojo.regexp");


dojo.validate.isText = function(/*String*/value, /*Object?*/flags){
// summary:
//	Checks if a string has non whitespace characters. 
//	Parameters allow you to constrain the length.
//
// value: A string
// flags: {length: Number, minlength: Number, maxlength: Number}
//    flags.length  If set, checks if there are exactly flags.length number of characters.
//    flags.minlength  If set, checks if there are at least flags.minlength number of characters.
//    flags.maxlength  If set, checks if there are at most flags.maxlength number of characters.

	flags = (typeof flags == "object") ? flags : {};

	// test for text
	if(/^\s*$/.test(value)){ return false; } // Boolean

	// length tests
	if(typeof flags.length == "number" && flags.length != value.length){ return false; } // Boolean
	if(typeof flags.minlength == "number" && flags.minlength > value.length){ return false; } // Boolean
	if(typeof flags.maxlength == "number" && flags.maxlength < value.length){ return false; } // Boolean

	return true; // Boolean
}

dojo.validate.isInteger = function(/*String*/value, /*Object?*/flags){
// summary:
//	Validates whether a string is in an integer format
//
// value  A string
// flags  {signed: Boolean|[true,false], separator: String}
//    flags.signed  The leading plus-or-minus sign.  Can be true, false, or [true, false].
//      Default is [true, false], (i.e. sign is optional).
//    flags.separator  The character used as the thousands separator.  Default is no separator.
//      For more than one symbol use an array, e.g. [",", ""], makes ',' optional.

	var re = new RegExp("^" + dojo.regexp.integer(flags) + "$");
	return re.test(value); // Boolean
}

dojo.validate.isRealNumber = function(/*String*/value, /*Object?*/flags){
// summary:
//	Validates whether a string is a real valued number. 
//	Format is the usual exponential notation.
//
// value: A string
// flags: {places: Number, decimal: String, exponent: Boolean|[true,false], eSigned: Boolean|[true,false], ...}
//    flags.places  The integer number of decimal places.
//      If not given, the decimal part is optional and the number of places is unlimited.
//    flags.decimal  The character used for the decimal point.  Default is ".".
//    flags.exponent  Express in exponential notation.  Can be true, false, or [true, false].
//      Default is [true, false], (i.e. the exponential part is optional).
//    flags.eSigned  The leading plus-or-minus sign on the exponent.  Can be true, false, 
//      or [true, false].  Default is [true, false], (i.e. sign is optional).
//    flags in regexp.integer can be applied.

	var re = new RegExp("^" + dojo.regexp.realNumber(flags) + "$");
	return re.test(value); // Boolean
}

dojo.validate.isCurrency = function(/*String*/value, /*Object?*/flags){
// summary:
//	Validates whether a string denotes a monetary value. 
// value: A string
// flags: {signed:Boolean|[true,false], symbol:String, placement:String, separator:String,
//	fractional:Boolean|[true,false], decimal:String}
//    flags.signed  The leading plus-or-minus sign.  Can be true, false, or [true, false].
//      Default is [true, false], (i.e. sign is optional).
//    flags.symbol  A currency symbol such as Yen "�", Pound "�", or the Euro sign "�".  
//      Default is "$".  For more than one symbol use an array, e.g. ["$", ""], makes $ optional.
//    flags.placement  The symbol can come "before" the number or "after".  Default is "before".
//    flags.separator  The character used as the thousands separator. The default is ",".
//    flags.fractional  The appropriate number of decimal places for fractional currency (e.g. cents)
//      Can be true, false, or [true, false].  Default is [true, false], (i.e. cents are optional).
//    flags.decimal  The character used for the decimal point.  Default is ".".

	var re = new RegExp("^" + dojo.regexp.currency(flags) + "$");
	return re.test(value); // Boolean
}

dojo.validate.isInRange = function(/*String*/value, /*Object?*/flags){
//summary:
//	Validates whether a string denoting an integer, 
//	real number, or monetary value is between a max and min. 
//
// value: A string
// flags: {max:Number, min:Number, decimal:String}
//    flags.max  A number, which the value must be less than or equal to for the validation to be true.
//    flags.min  A number, which the value must be greater than or equal to for the validation to be true.
//    flags.decimal  The character used for the decimal point.  Default is ".".

	//stripping the seperator allows NaN to perform as expected, if no separator, we assume ','
	//once i18n support is ready for this, instead of assuming, we default to i18n's recommended value
	value = value.replace((dojo.lang.has(flags,'separator'))?flags.separator:',','');
	if(isNaN(value)){
		return false; // Boolean
	}
	// assign default values to missing paramters
	flags = (typeof flags == "object") ? flags : {};
	var max = (typeof flags.max == "number") ? flags.max : Infinity;
	var min = (typeof flags.min == "number") ? flags.min : -Infinity;
	var dec = (typeof flags.decimal == "string") ? flags.decimal : ".";
	
	// splice out anything not part of a number
	var pattern = "[^" + dec + "\\deE+-]";
	value = value.replace(RegExp(pattern, "g"), "");

	// trim ends of things like e, E, or the decimal character
	value = value.replace(/^([+-]?)(\D*)/, "$1");
	value = value.replace(/(\D*)$/, "");

	// replace decimal with ".". The minus sign '-' could be the decimal!
	pattern = "(\\d)[" + dec + "](\\d)";
	value = value.replace(RegExp(pattern, "g"), "$1.$2");

	value = Number(value);
	if ( value < min || value > max ) { return false; } // Boolean

	return true; // Boolean
}

dojo.validate.isNumberFormat = function(/*String*/value, /*Object?*/flags){
// summary:
//	Validates any sort of number based format
//
// description:
//	Use it for phone numbers, social security numbers, zip-codes, etc.
//	The value can be validated against one format or one of multiple formats.
//
//  Format
//    #        Stands for a digit, 0-9.
//    ?        Stands for an optional digit, 0-9 or nothing.
//    All other characters must appear literally in the expression.
//
//  Example   
//    "(###) ###-####"       ->   (510) 542-9742
//    "(###) ###-#### x#???" ->   (510) 542-9742 x153
//    "###-##-####"          ->   506-82-1089       i.e. social security number
//    "#####-####"           ->   98225-1649        i.e. zip code
//
// value: A string
// flags: {format:String}
//    flags.format  A string or an Array of strings for multiple formats.

	var re = new RegExp("^" + dojo.regexp.numberFormat(flags) + "$", "i");
	return re.test(value); // Boolean
}

dojo.validate.isValidLuhn = function(/*String*/value){
//summary: Compares value against the Luhn algorithm to verify its integrity
	var sum, parity, curDigit;
	if(typeof value!='string'){
		value = String(value);
	}
	value = value.replace(/[- ]/g,''); //ignore dashes and whitespaces
	parity = value.length%2;
	sum=0;
	for(var i=0;i<value.length;i++){
		curDigit = parseInt(value.charAt(i));
		if(i%2==parity){
			curDigit*=2;
		}
		if(curDigit>9){
			curDigit-=9;
		}
		sum+=curDigit;
	}
	return !(sum%10);
}

/**
	Procedural API Description

		The main aim is to make input validation expressible in a simple format.
		You define profiles which declare the required and optional fields and any constraints they might have.
		The results are provided as an object that makes it easy to handle missing and invalid input.

	Usage

		var results = dojo.validate.check(form, profile);

	Profile Object

		var profile = {
			// filters change the field value and are applied before validation.
			trim: ["tx1", "tx2"],
			uppercase: ["tx9"],
			lowercase: ["tx5", "tx6", "tx7"],
			ucfirst: ["tx10"],
			digit: ["tx11"],

			// required input fields that are blank will be reported missing.
			// required radio button groups and drop-down lists with no selection will be reported missing.
			// checkbox groups and selectboxes can be required to have more than one value selected.
			// List required fields by name and use this notation to require more than one value: {checkboxgroup: 2}, {selectboxname: 3}.
			required: ["tx7", "tx8", "pw1", "ta1", "rb1", "rb2", "cb3", "s1", {"doubledip":2}, {"tripledip":3}],

			// dependant/conditional fields are required if the target field is present and not blank.
			// At present only textbox, password, and textarea fields are supported.
			dependencies:	{
				cc_exp: "cc_no",	
				cc_type: "cc_no",	
			},

			// Fields can be validated using any boolean valued function.  
			// Use arrays to specify parameters in addition to the field value.
			constraints: {
				field_name1: myValidationFunction,
				field_name2: dojo.validate.isInteger,
				field_name3: [myValidationFunction, additional parameters],
				field_name4: [dojo.validate.isValidDate, "YYYY.MM.DD"],
				field_name5: [dojo.validate.isEmailAddress, false, true],
			},

			// Confirm is a sort of conditional validation.
			// It associates each field in its property list with another field whose value should be equal.
			// If the values are not equal, the field in the property list is reported as Invalid. Unless the target field is blank.
			confirm: {
				email_confirm: "email",	
				pw2: "pw1",	
			}
		};

	Results Object

		isSuccessful(): Returns true if there were no invalid or missing fields, else it returns false.
		hasMissing():  Returns true if the results contain any missing fields.
		getMissing():  Returns a list of required fields that have values missing.
		isMissing(field):  Returns true if the field is required and the value is missing.
		hasInvalid():  Returns true if the results contain fields with invalid data.
		getInvalid():  Returns a list of fields that have invalid values.
		isInvalid(field):  Returns true if the field has an invalid value.

*/
