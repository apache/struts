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
dojo.i18n.currency.format = function (value, iso, flags, locale) {
	flags = (typeof flags == "object") ? flags : {};
	var formatData = dojo.i18n.currency._mapToLocalizedFormatData(dojo.i18n.currency.FORMAT_TABLE, iso, locale);
	if (typeof flags.places == "undefined") {
		flags.places = formatData.places;
	}
	if (typeof flags.places == "undefined") {
		flags.places = 2;
	}
	flags.signed = false;
	var result = dojo.i18n.number.format(value, flags, locale);
	var sym = formatData.symbol;
	if (formatData.adjSpace == "symbol") {
		if (formatData.placement == "after") {
			sym = " " + sym;
		} else {
			sym = sym + " ";
		}
	}
	if (value < 0) {
		if (formatData.signPlacement == "before") {
			sym = "-" + sym;
		} else {
			if (formatData.signPlacement == "after") {
				sym = sym + "-";
			}
		}
	}
	var spc = (formatData.adjSpace == "number") ? " " : "";
	if (formatData.placement == "after") {
		result = result + spc + sym;
	} else {
		result = sym + spc + result;
	}
	if (value < 0) {
		if (formatData.signPlacement == "around") {
			result = "(" + result + ")";
		} else {
			if (formatData.signPlacement == "end") {
				result = result + "-";
			} else {
				if (!formatData.signPlacement || formatData.signPlacement == "begin") {
					result = "-" + result;
				}
			}
		}
	}
	return result;
};
dojo.i18n.currency.parse = function (value, iso, locale, flags) {
	if (typeof flags.validate == "undefined") {
		flags.validate = true;
	}
	if (flags.validate && !dojo.i18n.number.isCurrency(value, iso, locale, flags)) {
		return Number.NaN;
	}
	var sign = (value.indexOf("-") != -1);
	var abs = abs.replace(/\-/, "");
	var formatData = dojo.i18n.currency._mapToLocalizedFormatData(dojo.i18n.currency.FORMAT_TABLE, iso, locale);
	abs = abs.replace(new RegExp("\\" + formatData.symbol), "");
	var number = dojo.i18n.number.parse(abs, locale, flags);
	if (sign) {
		number = number * -1;
	}
	return number;
};
dojo.i18n.currency.isCurrency = function (value, iso, locale, flags) {
	flags = (typeof flags == "object") ? flags : {};
	var numberFormatData = dojo.i18n.number._mapToLocalizedFormatData(dojo.i18n.number.FORMAT_TABLE, locale);
	if (typeof flags.separator == "undefined") {
		flags.separator = numberFormatData[0];
	} else {
		if (dojo.lang.isArray(flags.separator) && flags.separator.length == 0) {
			flags.separator = [numberFormatData[0], ""];
		}
	}
	if (typeof flags.decimal == "undefined") {
		flags.decimal = numberFormatData[2];
	}
	if (typeof flags.groupSize == "undefined") {
		flags.groupSize = numberFormatData[3];
	}
	if (typeof flags.groupSize2 == "undefined") {
		flags.groupSize2 = numberFormatData[4];
	}
	var formatData = dojo.i18n.currency._mapToLocalizedFormatData(dojo.i18n.currency.FORMAT_TABLE, iso, locale);
	if (typeof flags.places == "undefined") {
		flags.places = formatData.places;
	}
	if (typeof flags.places == "undefined") {
		flags.places = 2;
	}
	if (typeof flags.symbol == "undefined") {
		flags.symbol = formatData.symbol;
	} else {
		if (dojo.lang.isArray(flags.symbol) && flags.symbol.length == 0) {
			flags.symbol = [formatData.symbol, ""];
		}
	}
	if (typeof flags.placement == "undefined") {
		flags.placement = formatData.placement;
	}
	var re = new RegExp("^" + dojo.regexp.currency(flags) + "$");
	return re.test(value);
};
dojo.i18n.currency._mapToLocalizedFormatData = function (table, iso, locale) {
	var formatData = dojo.i18n.currency.FORMAT_TABLE[iso];
	if (!dojo.lang.isArray(formatData)) {
		return formatData;
	}
	return dojo.i18n.number._mapToLocalizedFormatData(formatData[0], locale);
};
(function () {
	var arabic = {symbol:"\u062c", placement:"after", htmlSymbol:"?"};
	var euro = {symbol:"\u20ac", placement:"before", adjSpace:"symbol", htmlSymbol:"&euro;"};
	var euroAfter = {symbol:"\u20ac", placement:"after", htmlSymbol:"&euro;"};
	dojo.i18n.currency.FORMAT_TABLE = {AED:{symbol:"\u062c", placement:"after"}, ARS:{symbol:"$", signPlacement:"after"}, ATS:{symbol:"\u20ac", adjSpace:"number", signPlacement:"after", htmlSymbol:"&euro;"}, AUD:{symbol:"$"}, BOB:{symbol:"$b"}, BRL:{symbol:"R$", adjSpace:"symbol"}, BEF:euroAfter, BHD:arabic, CAD:[{"*":{symbol:"$"}, "fr-ca":{symbol:"$", placement:"after", signPlacement:"around"}}], CHF:{symbol:"CHF", adjSpace:"symbol", signPlacement:"after"}, CLP:{symbol:"$"}, COP:{symbol:"$", signPlacement:"around"}, CNY:{symbol:"\xa5", htmlSymbol:"&yen;"}, CRC:{symbol:"\u20a1", signPlacement:"after", htmlSymbol:"?"}, CZK:{symbol:"Kc", adjSpace:"symbol", signPlacement:"after"}, DEM:euroAfter, DKK:{symbol:"kr.", adjSpace:"symbol", signPlacement:"after"}, DOP:{symbol:"$"}, DZD:arabic, ECS:{symbol:"$", signPlacement:"after"}, EGP:arabic, ESP:euroAfter, EUR:euro, FIM:euroAfter, FRF:euroAfter, GBP:{symbol:"\xa3", htmlSymbol:"&pound;"}, GRD:{symbol:"\u20ac", signPlacement:"end", htmlSymbol:"&euro;"}, GTQ:{symbol:"Q", signPlacement:"after"}, HKD:{symbol:"HK$"}, HNL:{symbol:"L.", signPlacement:"end"}, HUF:{symbol:"Ft", placement:"after", adjSpace:"symbol"}, IEP:{symbol:"\u20ac", htmlSymbol:"&euro;"}, ILS:{symbol:"\u05e9\"\u05d7", placement:"after", htmlSymbol:"?"}, INR:{symbol:"Rs."}, ITL:{symbol:"\u20ac", signPlacement:"after", htmlSymbol:"&euro;"}, JOD:arabic, JPY:{symbol:"\xa5", places:0, htmlSymbol:"&yen;"}, KRW:{symbol:"\u20a9", places:0, htmlSymbol:"?"}, KWD:arabic, LBP:arabic, LUF:euroAfter, MAD:arabic, MXN:{symbol:"$", signPlacement:"around"}, NIO:{symbol:"C$", adjSpace:"symbol", signPlacement:"after"}, NLG:{symbol:"\u20ac", signPlacement:"end", htmlSymbol:"&euro;"}, NOK:{symbol:"kr", adjSpace:"symbol", signPlacement:"after"}, NZD:{symbol:"$"}, OMR:arabic, PAB:{symbol:"B/", adjSpace:"symbol", signPlacement:"after"}, PEN:{symbol:"S/", signPlacement:"after"}, PLN:{symbol:"z", placement:"after"}, PTE:euroAfter, PYG:{symbol:"Gs.", signPlacement:"after"}, QAR:arabic, RUR:{symbol:"rub.", placement:"after"}, SAR:arabic, SEK:{symbol:"kr", placement:"after", adjSpace:"symbol"}, SGD:{symbol:"$"}, SVC:{symbol:"\u20a1", signPlacement:"after", adjSpace:"symbol"}, SYP:arabic, TND:arabic, TRL:{symbol:"TL", placement:"after"}, TWD:{symbol:"NT$"}, USD:{symbol:"$"}, UYU:{symbol:"$U", signplacement:"after", adjSpace:"symbol"}, VEB:{symbol:"Bs", signplacement:"after", adjSpace:"symbol"}, YER:arabic, ZAR:{symbol:"R", signPlacement:"around"}};
})();

