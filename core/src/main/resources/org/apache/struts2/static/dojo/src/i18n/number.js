/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.i18n.number");

dojo.require("dojo.experimental");
dojo.experimental("dojo.i18n.number");

dojo.require("dojo.regexp");
dojo.require("dojo.i18n.common");
dojo.require("dojo.lang.common");

/**
* Method to Format and validate a given number
*
* @param Number value
*	The number to be formatted and validated.
* @param Object flags
*   flags.places number of decimal places to show, default is 0 (cannot be Infinity)
*   flags.round true to round the number, false to truncate
* @param String locale
*	The locale used to determine the number format.
* @return String
* 	the formatted number of type String if successful
*   or null if an unsupported locale value was provided
**/
dojo.i18n.number.format = function(value, flags /*optional*/, locale /*optional*/){
	flags = (typeof flags == "object") ? flags : {};

	var formatData = dojo.i18n.number._mapToLocalizedFormatData(dojo.i18n.number.FORMAT_TABLE, locale);
	if (typeof flags.separator == "undefined") {flags.separator = formatData[1];}
	if (typeof flags.decimal == "undefined") {flags.decimal = formatData[2];}
	if (typeof flags.groupSize == "undefined") {flags.groupSize = formatData[3];}
	if (typeof flags.groupSize2 == "undefined") {flags.groupSize2 = formatData[4];}
	if (typeof flags.round == "undefined") {flags.round = true;}
	if (typeof flags.signed == "undefined") {flags.signed = true;}

	var output = (flags.signed && (value < 0)) ? "-" : "";
	value = Math.abs(value);
	var whole = String((((flags.places > 0) || !flags.round) ? Math.floor : Math.round)(value));

	// Splits str into substrings of size count, starting from right to left.  Is there a more clever way to do this in JS?
	function splitSubstrings(str, count){
		for(var subs = []; str.length >= count; str = str.substr(0, str.length - count)){
			subs.push(str.substr(-count));
		}
		if (str.length > 0){subs.push(str);}
		return subs.reverse();
	}

	if (flags.groupSize2 && (whole.length > flags.groupSize)){
		var groups = splitSubstrings(whole.substr(0, whole.length - flags.groupSize), flags.groupSize2);
		groups.push(whole.substr(-flags.groupSize));
		output = output + groups.join(flags.separator);
	}else if (flags.groupSize){
		output = output + splitSubstrings(whole, flags.groupSize).join(flags.separator);
	}else{
		output = output + whole;
	}

//TODO: what if flags.places is Infinity?
	if (flags.places > 0){
	//Q: Is it safe to convert to a string and split on ".", or might that be locale dependent?  Use Math for now.
		var fract = value - Math.floor(value);
		fract = (flags.round ? Math.round : Math.floor)(fract * Math.pow(10, flags.places));
		output = output + flags.decimal + fract;
	}

//TODO: exp

	return output;
};

/**
* Method to convert a properly formatted int string to a primative numeric value.
*
* @param String value
*	The int string to be convertted
* @param string locale
*	The locale used to convert the number string
* @param Object flags
*   flags.validate true to check the string for strict adherence to the locale settings for separator, sign, etc.
*     Default is true
* @return Number
* 	Returns a value of type Number, Number.NaN if not a number, or null if locale is not supported.
**/
dojo.i18n.number.parse = function(value, locale /*optional*/, flags /*optional*/){
	flags = (typeof flags == "object") ? flags : {};

	var formatData = dojo.i18n.number._mapToLocalizedFormatData(dojo.i18n.number.FORMAT_TABLE, locale);
	if (typeof flags.separator == "undefined") {flags.separator = formatData[1];}
	if (typeof flags.decimal == "undefined") {flags.decimal = formatData[2];}
	if (typeof flags.groupSize == "undefined") {flags.groupSize = formatData[3];}
	if (typeof flags.groupSize2 == "undefined") {flags.groupSize2 = formatData[4];}
	if (typeof flags.validate == "undefined") {flags.validate = true;}

	if (flags.validate && !dojo.i18n.number.isReal(value, locale, flags)) {
		return Number.NaN;
	}

	var numbers = value.split(flags.decimal);
	if (numbers.length > 2){return Number.NaN; }
	var whole = Number(numbers[0].replace(new RegExp("\\" + flags.separator, "g"), ""));
	var fract = (numbers.length == 1) ? 0 : Number(numbers[1]) / Math.pow(10, String(numbers[1]).length); // could also do Number(whole + "." + numbers[1]) if whole != NaN

//TODO: exp

	return whole + fract;
};

/**
  Validates whether a string is in an integer format. 

  @param value  A string.
  @param locale the locale to determine formatting used.  By default, the locale defined by the
    host environment: dojo.locale
  @param flags  An object.
    flags.signed  The leading plus-or-minus sign.  Can be true, false, or [true, false].
      Default is [true, false], (i.e. sign is optional).
    flags.separator  The character used as the thousands separator.  Default is specified by the locale.
      For more than one symbol use an array, e.g. [",", ""], makes ',' optional.
      The empty array [] makes the default separator optional.   
  @return  true or false.
*/
dojo.i18n.number.isInteger = function(value, locale /*optional*/, flags /*optional*/) {
	flags = (typeof flags == "object") ? flags : {};

	var formatData = dojo.i18n.number._mapToLocalizedFormatData(dojo.i18n.number.FORMAT_TABLE, locale);
	if (typeof flags.separator == "undefined") {flags.separator = formatData[1];}
	else if (dojo.lang.isArray(flags.separator) && flags.separator.length ===0){
		flags.separator = [formatData[1],""];
	}
	if (typeof flags.groupSize == "undefined") {flags.groupSize = formatData[3];}
	if (typeof flags.groupSize2 == "undefined") {flags.groupSize2 = formatData[4];}

	var re = new RegExp("^" + dojo.regexp.integer(flags) + "$");
	return re.test(value);
};

/**
  Validates whether a string is a real valued number. 
  Format is the usual exponential notation.

  @param value  A string.
  @param locale the locale to determine formatting used.  By default, the locale defined by the
    host environment: dojo.locale
  @param flags  An object.
    flags.places  The integer number of decimal places.
      If not given, the decimal part is optional and the number of places is unlimited.
    flags.decimal  The character used for the decimal point.  The default is specified by the locale.
    flags.exponent  Express in exponential notation.  Can be true, false, or [true, false].
      Default is [true, false], (i.e. the exponential part is optional).
    flags.eSigned  The leading plus-or-minus sign on the exponent.  Can be true, false, 
      or [true, false].  Default is [true, false], (i.e. sign is optional).
    flags in regexp.integer can be applied.
  @return  true or false.
*/
dojo.i18n.number.isReal = function(value, locale /*optional*/, flags /*optional*/) {
	flags = (typeof flags == "object") ? flags : {};

	var formatData = dojo.i18n.number._mapToLocalizedFormatData(dojo.i18n.number.FORMAT_TABLE, locale);
	if (typeof flags.separator == "undefined") {flags.separator = formatData[1];}
	else if (dojo.lang.isArray(flags.separator) && flags.separator.length ===0){
		flags.separator = [formatData[1],""];
	}
	if (typeof flags.decimal == "undefined") {flags.decimal = formatData[2];}
	if (typeof flags.groupSize == "undefined") {flags.groupSize = formatData[3];}
	if (typeof flags.groupSize2 == "undefined") {flags.groupSize2 = formatData[4];}

	var re = new RegExp("^" + dojo.regexp.realNumber(flags) + "$");
	return re.test(value);
};

//TODO: hide in a closure?
//TODO: change to use hashes and mixins, rather than arrays
//Q: fallback algorithm/how to structure table:
// does it make sense to look by country code most of the time (wildcard match on
// language, except where it's relevant) and provide default country when only
// a language is given?
(function() {

dojo.i18n.number.FORMAT_TABLE = {
	//0: thousand seperator for monetary, 1: thousand seperator for number, 2: decimal seperator, 3: group size, 4: second group size because of india
	'ar-ae': ["","", ",", 1],
	'ar-bh': ["","",",", 1],
	'ar-dz': ["","",",", 1],
	'ar-eg': ["","", ",", 1],
	'ar-jo': ["","",",", 1],
	'ar-kw': ["","", ",", 1],
	'ar-lb': ["","", ",", 1],
	'ar-ma': ["","", ",", 1],
	'ar-om': ["","", ",", 1],
	'ar-qa': ["","", ",", 1],
	'ar-sa': ["","", ",", 1],
	'ar-sy': ["","", ",", 1],
	'ar-tn': ["","", ",", 1],
	'ar-ye': ["","", ",", 1],
	'cs-cz': [".",".", ",", 3],
	'da-dk': [".",".", ",", 3],
	'de-at': [".",".", ",", 3],
	'de-de': [".",".", ",", 3],
	'de-lu': [".",".", ",", 3],
	//IBM JSL defect 51278. right now we have problem with single quote. //IBM: explain?
	'de-ch': ["'","'", ".", 3], //Q: comma as decimal separator for currency??
	//'de-ch': [".",".", ",", 3],
	'el-gr': [".",".", ",", 3],
	'en-au': [",",",", ".", 3],
	'en-ca': [",",",", ".", 3],
	'en-gb': [",",",", ".", 3],
	'en-hk': [",",",", ".", 3],
	'en-ie': [",",",", ".", 3],
	'en-in': [",",",", ".", 3,2],//india-english, 1,23,456.78
	'en-nz': [",",",", ".", 3],
	'en-us': [",",",", ".", 3],
	'en-za': [",",",", ".", 3],
	
	'es-ar': [".",".", ",", 3],
	'es-bo': [".",".", ",", 3],
	'es-cl': [".",".", ",", 3],
	'es-co': [".",".", ",", 3],
	'es-cr': [".",".", ",", 3],
	'es-do': [".",".", ",", 3],
	'es-ec': [".",".", ",", 3],
	'es-es': [".",".", ",", 3],
	'es-gt': [",",",", ".", 3],
	'es-hn': [",",",", ".", 3],
	'es-mx': [",",",", ".", 3],
	'es-ni': [",",",", ".", 3],
	'es-pa': [",",",", ".", 3],
	'es-pe': [",",",", ".", 3],
	'es-pr': [",",",", ".", 3],
	'es-py': [".",".",",", 3],
	'es-sv': [",", ",",".", 3],
	'es-uy': [".",".",",", 3],
	'es-ve': [".",".", ",", 3],
	
	'fi-fi': [" "," ", ",", 3],
	
	'fr-be': [".",".",",", 3],
	'fr-ca': [" ", " ", ",", 3],
	
	'fr-ch': [" ", " ",".", 3],
	
	'fr-fr': [" "," ", ",", 3],
	'fr-lu': [".",".", ",", 3],
	
	'he-il': [",",",", ".", 3],
	
	'hu-hu': [" ", " ",",", 3],
	
	'it-ch': [" "," ", ".", 3],
	
	'it-it': [".",".", ",", 3],
	'ja-jp': [",",",", ".", 3],
	'ko-kr': [",", ",",".", 3],
	
	'no-no': [".",".", ",", 3],
	
	'nl-be': [" "," ", ",", 3],
	'nl-nl': [".",".", ",", 3],
	'pl-pl': [".", ".",",", 3],
	
	'pt-br': [".",".", ",", 3],
	'pt-pt': [".",".", "$", 3],
	'ru-ru': [" ", " ",",", 3],
	
	'sv-se': ["."," ", ",", 3],
	
	'tr-tr': [".",".", ",", 3],
	
	'zh-cn': [",",",", ".", 3],
	'zh-hk': [",",",",".", 3],
	'zh-tw': [",", ",",".", 3],
	'*': [",",",", ".", 3]
};
})();

dojo.i18n.number._mapToLocalizedFormatData = function(table, locale){
	locale = dojo.hostenv.normalizeLocale(locale);
//TODO: most- to least-specific search? search by country code?
//TODO: implement aliases to simplify and shorten tables
	var data = table[locale];
	if (typeof data == 'undefined'){data = table['*'];}
	return data;
}
