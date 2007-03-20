/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojo.i18n.currency");

dojo.require("dojo.experimental");
dojo.experimental("dojo.i18n.currency");

dojo.require("dojo.regexp");
dojo.require("dojo.i18n.common");
dojo.require("dojo.i18n.number");
dojo.require("dojo.lang.common");

/**
* Method to Format and validate a given number a monetary value
*
* @param Number value
*	The number to be formatted and validated.
* @param String iso the ISO 4217 currency code
* @param Object flags
*   flags.places The number of decimal places to be included in the formatted number
* @param String locale the locale to determine formatting used.  By default, the locale defined by the
*   host environment: dojo.locale
* @return String
* 	the formatted currency of type String if successful; Nan if an
* 	invalid currency is provided or null if an unsupported locale value was provided.
**/
dojo.i18n.currency.format = function(value, iso, flags /*optional*/, locale /*optional*/){
	flags = (typeof flags == "object") ? flags : {};

	var formatData = dojo.i18n.currency._mapToLocalizedFormatData(dojo.i18n.currency.FORMAT_TABLE, iso, locale);
	if (typeof flags.places == "undefined") {flags.places = formatData.places;}
	if (typeof flags.places == "undefined") {flags.places = 2;}
	flags.signed = false;

	var result = dojo.i18n.number.format(value, flags, locale);

	var sym = formatData.symbol;
	if (formatData.adjSpace == "symbol"){ 
		if (formatData.placement == "after"){
			sym = " " + sym;// TODO: nbsp?
		}else{
			sym = sym + " ";// TODO: nbsp?
		}
	}

	if (value < 0){
		if (formatData.signPlacement == "before"){
			sym = "-" + sym;
		}else if (formatData.signPlacement == "after"){
			sym = sym + "-";
		}
	}

	var spc = (formatData.adjSpace == "number") ? " " : ""; // TODO: nbsp?
	if (formatData.placement == "after"){
		result = result + spc + sym;
	}else{
		result = sym + spc + result;
	}

	if (value < 0){
		if (formatData.signPlacement == "around"){
			result = "(" + result + ")";
		}else if (formatData.signPlacement == "end"){
			result = result + "-";
		}else if (!formatData.signPlacement || formatData.signPlacement == "begin"){
			result = "-" + result;
		}
	}

	return result;
};

/**
* Method to convert a properly formatted monetary value to a primative numeric value.
*
* @param String value
*	The int string to be convertted
  @param String iso the ISO 4217 currency code
* @param String locale the locale to determine formatting used.  By default, the locale defined by the
*   host environment: dojo.locale
* @param Object flags
*   flags.validate true to check the string for strict adherence to the locale settings for separator, sign, etc.
*     Default is true
* @return Number
* 	Returns a primative numeric value, Number.NaN if unable to convert to a number, or null if an unsupported locale is provided.
**/
dojo.i18n.currency.parse = function(value, iso, locale, flags /*optional*/){
	if (typeof flags.validate == "undefined") {flags.validate = true;}

	if (flags.validate && !dojo.i18n.number.isCurrency(value, iso, locale, flags)) {
		return Number.NaN;
	}

	var sign = (value.indexOf('-') != -1);
	var abs = abs.replace(/\-/, "");

	var formatData = dojo.i18n.currency._mapToLocalizedFormatData(dojo.i18n.currency.FORMAT_TABLE, iso, locale);
	abs = abs.replace(new RegExp("\\" + formatData.symbol), "");
	//TODO: trim?

	var number = dojo.i18n.number.parse(abs, locale, flags);
	if (sign){number = number * -1;}
	return number;
};

/**
  Validates whether a string denotes a monetary value. 

  @param value  A string
  @param iso the ISO 4217 currency code
  @param locale the locale to determine formatting used.  By default, the locale defined by the
    host environment: dojo.locale
  @param flags  An object
    flags.symbol  A currency symbol such as Yen "�", Pound "�", or the Euro sign "�".  
        The default is specified by the iso code.  For more than one symbol use an array, e.g. ["$", ""], makes $ optional.
        The empty array [] makes the default currency symbol optional.
    flags.placement  The symbol can come "before" or "after".  The default is specified by the iso code.
    flags.signed  The leading plus-or-minus sign.  Can be true, false, or [true, false].
      Default is [true, false], (i.e. sign is optional).
    flags.signPlacement  The sign can come "before" or "after" the symbol or "around" the whole expression
    	with parenthesis, such as CAD: (123$).  The default is specified by the iso code.
    flags.separator  The character used as the thousands separator. The default is specified by the locale.
        The empty array [] makes the default separator optional.
    flags.fractional  The appropriate number of decimal places for fractional currency (e.g. cents)
      Can be true, false, or [true, false].  Default is [true, false], (i.e. cents are optional).
    flags.places  The integer number of decimal places.
      If not given, an amount appropriate to the iso code is used.
    flags.fractional  The appropriate number of decimal places for fractional currency (e.g. cents)
      Can be true, false, or [true, false].  Default is [true, false], (i.e. cents are optional).
    flags.decimal  The character used for the decimal point.  The default is specified by the locale.
  @return  true or false.
*/
dojo.i18n.currency.isCurrency = function(value, iso, locale /*optional*/, flags){
	flags = (typeof flags == "object") ? flags : {};

	var numberFormatData = dojo.i18n.number._mapToLocalizedFormatData(dojo.i18n.number.FORMAT_TABLE, locale);
	if (typeof flags.separator == "undefined") {flags.separator = numberFormatData[0];}
	else if (dojo.lang.isArray(flags.separator) && flags.separator.length == 0){flags.separator = [numberFormatData[0],""];}
	if (typeof flags.decimal == "undefined") {flags.decimal = numberFormatData[2];}
	if (typeof flags.groupSize == "undefined") {flags.groupSize = numberFormatData[3];}
	if (typeof flags.groupSize2 == "undefined") {flags.groupSize2 = numberFormatData[4];}

	var formatData = dojo.i18n.currency._mapToLocalizedFormatData(dojo.i18n.currency.FORMAT_TABLE, iso, locale);
	if (typeof flags.places == "undefined") {flags.places = formatData.places;}
	if (typeof flags.places == "undefined") {flags.places = 2;}
	if (typeof flags.symbol == "undefined") {flags.symbol = formatData.symbol;}
	else if (dojo.lang.isArray(flags.symbol) && flags.symbol.length == 0){flags.symbol = [formatData.symbol,""];}
	if (typeof flags.placement == "undefined") {flags.placement = formatData.placement;}
	//TODO more... or mixin?

	var re = new RegExp("^" + dojo.regexp.currency(flags) + "$");
//dojo.debug(value+":"+dojo.regexp.currency(flags)+"="+re.test(value));
	return re.test(value);
};

dojo.i18n.currency._mapToLocalizedFormatData = function(table, iso, locale /*optional*/){
	var formatData = dojo.i18n.currency.FORMAT_TABLE[iso];
	if (!dojo.lang.isArray(formatData)){
		return formatData;
	}

	return dojo.i18n.number._mapToLocalizedFormatData(formatData[0], locale);
};

(function() {
	var arabic = {symbol: "\u062C", placement: "after", htmlSymbol: "?"};
	var euro = {symbol: "\u20AC", placement: "before", adjSpace: "symbol", htmlSymbol: "&euro;"};
	var euroAfter = {symbol: "\u20AC", placement: "after", htmlSymbol: "&euro;"};

//Q: Do European countries still use their old ISO symbols instead of just EUR?
//Q: are signPlacement and currency symbol placement ISO-dependent or are they really locale-dependent?
//TODO: htmlSymbol is for html entities, need images? (IBM: why? why can't we just use unicode everywhere?)
//TODO: hide visibility of this table?
//for html entities, need a image for arabic symbol "BHD" as "DZD", "EGP", "JOD", "KWD" "LBP", "MAD", "OMR", "QAR", "SAR", "SYP", "TND", "AED", "YER"
//Note: html entities not used at the moment
//placement: placement of currency symbol, before or after number
//signPlacement: placement of negative sign, before or after symbol, or begin or end of expression, or around with parentheses
// This table assumes defaults of
//	places: 2, placement: "before", signPlacement: "begin", adjSpace: undefined, htmlSymbol: undefined]
dojo.i18n.currency.FORMAT_TABLE = {
	AED: {symbol: "\u062c", placement: "after"},
	ARS: {symbol: "$", signPlacement: "after"},
	//Old ATS: {symbol: "S", adjSpace: "symbol"},
	ATS: {symbol: "\u20AC", adjSpace: "number", signPlacement: "after", htmlSymbol: "&euro;"}, 	//Austria using "EUR" // neg should read euro + sign + space + number
	AUD: {symbol: "$"},
	BOB: {symbol: "$b"},
	BRL: {symbol: "R$", adjSpace: "symbol"},
	//Old BEF: {symbol: "BF", placement: "after", adjSpace: "symbol"},
	BEF: euroAfter,	//Belgium using "EUR"
	//Old BHD: {symbol: "\u062C", signPlacement: "end", places: 3, htmlSymbol: "?"},
	BHD: arabic,
	//TODO: I'm suspicious that all the other entries have locale-specific data in them, too?
	//Q: which attributes are iso-specific and which are locale specific?
	CAD: [{
			'*' : {symbol: "$"},
			'fr-ca' : {symbol: "$", placement: "after", signPlacement: "around"}
		}],
	CHF: {symbol: "CHF", adjSpace: "symbol", signPlacement: "after"},
	CLP: {symbol: "$"},
	COP: {symbol: "$", signPlacement: "around"},
	CNY: {symbol: "\u00A5", htmlSymbol: "&yen;"},
	//// Costa Rica  - Spanish slashed C. need to find out the html entity image
	CRC: {symbol: "\u20A1", signPlacement: "after", htmlSymbol: "?"},
	// Czech Republic  - Czech //need image for html entities
	CZK: {symbol: "Kc", adjSpace: "symbol", signPlacement: "after"},
	DEM: euroAfter,
	DKK: {symbol: "kr.", adjSpace: "symbol", signPlacement: "after"},
	DOP: {symbol: "$"},
	//for html entities, need a image, bidi, using "rtl", so from the link, symbol is suffix
	//Old DZD: {symbol: "\u062C", signPlacement: "end", places: 3, htmlSymbol: "?"},
	DZD: arabic,
	//Ecuador using "USD"
	ECS: {symbol: "$", signPlacement: "after"},
	EGP: arabic,
	//Old ESP: {symbol: "Pts", placement: "after", adjSpace: "symbol", places: 0},
	ESP: euroAfter,	//spain using "EUR"
	EUR: euro,
	//Old FIM: {symbol: "mk", placement: "after", adjSpace: "symbol"},
	FIM: euroAfter,	//Finland using "EUR"
	//Old FRF: {symbol: "F", placement: "after", adjSpace: "symbol"},
	FRF: euroAfter,	//France using "EUR"
	GBP: {symbol: "\u00A3", htmlSymbol: "&pound;"},
	GRD: {symbol: "\u20AC", signPlacement: "end", htmlSymbol: "&euro;"},
	GTQ: {symbol: "Q", signPlacement: "after"},
	//Hong Kong need "HK$" and "$". Now only support "HK$"
	HKD: {symbol: "HK$"},
	HNL: {symbol: "L.", signPlacement: "end"},
	HUF: {symbol: "Ft", placement: "after", adjSpace: "symbol"},
	//IEP: {symbol: "\u00A3", htmlSymbol: "&pound;"},
	IEP: {symbol: "\u20AC", htmlSymbol: "&euro;"},	//ireland using "EUR" at the front.
	//couldn't know what Israel - Hebrew symbol, some sites use "NIS", bidi, using "rtl", so from the link, symbol is suffix (IBM: huh?)
	//ILS: {symbol: "\u05E9\u0022\u05D7", signPlacement: "end", htmlSymbol: "?"},
	ILS: {symbol: "\u05E9\u0022\u05D7", placement: "after", htmlSymbol: "?"},
	INR: {symbol: "Rs."},
	//ITL: {symbol: "L", adjSpace: "symbol", signPlacement: "after", places: 0},
	ITL: {symbol: "\u20AC", signPlacement: "after", htmlSymbol: "&euro;"},	//Italy using "EUR"
	JOD: arabic,
	JPY: {symbol: "\u00a5", places: 0, htmlSymbol: "&yen;"},
	KRW: {symbol: "\u20A9", places: 0, htmlSymbol: "?"},
	KWD: arabic,
	LBP: arabic,
	//Old LUF: {symbol: "LUF", placement: "after", adjSpace: "symbol"},
	//for Luxembourg,using "EUR"
	LUF: euroAfter,
	MAD: arabic,
	MXN: {symbol: "$", signPlacement: "around"},
	NIO: {symbol: "C$", adjSpace: "symbol", signPlacement: "after"},
	//Old NLG: {symbol: "f", adjSpace: "symbol", signPlacement: "end"},
	//Netherlands, using "EUR"
	NLG: {symbol: "\u20AC", signPlacement: "end", htmlSymbol: "&euro;"},
	NOK: {symbol: "kr", adjSpace: "symbol", signPlacement: "after"},
	NZD: {symbol: "$"},
	OMR: arabic,
	PAB: {symbol: "B/", adjSpace: "symbol", signPlacement: "after"},
	PEN: {symbol: "S/", signPlacement: "after"},
	//couldn't know what the symbol is from ibm link. (IBM: what does this mean?  Is the symbol 'z' wrong?)
	PLN: {symbol: "z", placement: "after"},
	//Old PTE: {symbol: "Esc.", placement: "after", adjSpace: "symbol", places: 0},
	PTE: euroAfter,
	PYG: {symbol: "Gs.", signPlacement: "after"},
	QAR: arabic,
	RUR: {symbol: "rub.", placement: "after"},
	SAR: arabic,
	SEK: {symbol: "kr", placement: "after", adjSpace: "symbol"},
	SGD: {symbol: "$"},
	//// El Salvador - Spanish slashed C. need to find out. (IBM: need to find out what?)
	SVC: {symbol: "\u20A1", signPlacement: "after", adjSpace: "symbol"},
	//for html entities, need a image
	SYP: arabic,
	TND: arabic,
	TRL: {symbol: "TL", placement: "after"},
	TWD: {symbol: "NT$"},
	USD: {symbol: "$"},
	UYU: {symbol: "$U", signplacement: "after", adjSpace: "symbol"},
	VEB: {symbol: "Bs", signplacement: "after", adjSpace: "symbol"},
	YER: arabic,
	ZAR: {symbol: "R", signPlacement: "around"}
};

})();
