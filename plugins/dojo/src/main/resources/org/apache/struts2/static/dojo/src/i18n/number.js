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
dojo.i18n.number.format = function (value, flags, locale) {
	flags = (typeof flags == "object") ? flags : {};
	var formatData = dojo.i18n.number._mapToLocalizedFormatData(dojo.i18n.number.FORMAT_TABLE, locale);
	if (typeof flags.separator == "undefined") {
		flags.separator = formatData[1];
	}
	if (typeof flags.decimal == "undefined") {
		flags.decimal = formatData[2];
	}
	if (typeof flags.groupSize == "undefined") {
		flags.groupSize = formatData[3];
	}
	if (typeof flags.groupSize2 == "undefined") {
		flags.groupSize2 = formatData[4];
	}
	if (typeof flags.round == "undefined") {
		flags.round = true;
	}
	if (typeof flags.signed == "undefined") {
		flags.signed = true;
	}
	var output = (flags.signed && (value < 0)) ? "-" : "";
	value = Math.abs(value);
	var whole = String((((flags.places > 0) || !flags.round) ? Math.floor : Math.round)(value));
	function splitSubstrings(str, count) {
		for (var subs = []; str.length >= count; str = str.substr(0, str.length - count)) {
			subs.push(str.substr(-count));
		}
		if (str.length > 0) {
			subs.push(str);
		}
		return subs.reverse();
	}
	if (flags.groupSize2 && (whole.length > flags.groupSize)) {
		var groups = splitSubstrings(whole.substr(0, whole.length - flags.groupSize), flags.groupSize2);
		groups.push(whole.substr(-flags.groupSize));
		output = output + groups.join(flags.separator);
	} else {
		if (flags.groupSize) {
			output = output + splitSubstrings(whole, flags.groupSize).join(flags.separator);
		} else {
			output = output + whole;
		}
	}
	if (flags.places > 0) {
		var fract = value - Math.floor(value);
		fract = (flags.round ? Math.round : Math.floor)(fract * Math.pow(10, flags.places));
		output = output + flags.decimal + fract;
	}
	return output;
};
dojo.i18n.number.parse = function (value, locale, flags) {
	flags = (typeof flags == "object") ? flags : {};
	var formatData = dojo.i18n.number._mapToLocalizedFormatData(dojo.i18n.number.FORMAT_TABLE, locale);
	if (typeof flags.separator == "undefined") {
		flags.separator = formatData[1];
	}
	if (typeof flags.decimal == "undefined") {
		flags.decimal = formatData[2];
	}
	if (typeof flags.groupSize == "undefined") {
		flags.groupSize = formatData[3];
	}
	if (typeof flags.groupSize2 == "undefined") {
		flags.groupSize2 = formatData[4];
	}
	if (typeof flags.validate == "undefined") {
		flags.validate = true;
	}
	if (flags.validate && !dojo.i18n.number.isReal(value, locale, flags)) {
		return Number.NaN;
	}
	var numbers = value.split(flags.decimal);
	if (numbers.length > 2) {
		return Number.NaN;
	}
	var whole = Number(numbers[0].replace(new RegExp("\\" + flags.separator, "g"), ""));
	var fract = (numbers.length == 1) ? 0 : Number(numbers[1]) / Math.pow(10, String(numbers[1]).length);
	return whole + fract;
};
dojo.i18n.number.isInteger = function (value, locale, flags) {
	flags = (typeof flags == "object") ? flags : {};
	var formatData = dojo.i18n.number._mapToLocalizedFormatData(dojo.i18n.number.FORMAT_TABLE, locale);
	if (typeof flags.separator == "undefined") {
		flags.separator = formatData[1];
	} else {
		if (dojo.lang.isArray(flags.separator) && flags.separator.length === 0) {
			flags.separator = [formatData[1], ""];
		}
	}
	if (typeof flags.groupSize == "undefined") {
		flags.groupSize = formatData[3];
	}
	if (typeof flags.groupSize2 == "undefined") {
		flags.groupSize2 = formatData[4];
	}
	var re = new RegExp("^" + dojo.regexp.integer(flags) + "$");
	return re.test(value);
};
dojo.i18n.number.isReal = function (value, locale, flags) {
	flags = (typeof flags == "object") ? flags : {};
	var formatData = dojo.i18n.number._mapToLocalizedFormatData(dojo.i18n.number.FORMAT_TABLE, locale);
	if (typeof flags.separator == "undefined") {
		flags.separator = formatData[1];
	} else {
		if (dojo.lang.isArray(flags.separator) && flags.separator.length === 0) {
			flags.separator = [formatData[1], ""];
		}
	}
	if (typeof flags.decimal == "undefined") {
		flags.decimal = formatData[2];
	}
	if (typeof flags.groupSize == "undefined") {
		flags.groupSize = formatData[3];
	}
	if (typeof flags.groupSize2 == "undefined") {
		flags.groupSize2 = formatData[4];
	}
	var re = new RegExp("^" + dojo.regexp.realNumber(flags) + "$");
	return re.test(value);
};
(function () {
	dojo.i18n.number.FORMAT_TABLE = {"ar-ae":["", "", ",", 1], "ar-bh":["", "", ",", 1], "ar-dz":["", "", ",", 1], "ar-eg":["", "", ",", 1], "ar-jo":["", "", ",", 1], "ar-kw":["", "", ",", 1], "ar-lb":["", "", ",", 1], "ar-ma":["", "", ",", 1], "ar-om":["", "", ",", 1], "ar-qa":["", "", ",", 1], "ar-sa":["", "", ",", 1], "ar-sy":["", "", ",", 1], "ar-tn":["", "", ",", 1], "ar-ye":["", "", ",", 1], "cs-cz":[".", ".", ",", 3], "da-dk":[".", ".", ",", 3], "de-at":[".", ".", ",", 3], "de-de":[".", ".", ",", 3], "de-lu":[".", ".", ",", 3], "de-ch":["'", "'", ".", 3], "el-gr":[".", ".", ",", 3], "en-au":[",", ",", ".", 3], "en-ca":[",", ",", ".", 3], "en-gb":[",", ",", ".", 3], "en-hk":[",", ",", ".", 3], "en-ie":[",", ",", ".", 3], "en-in":[",", ",", ".", 3, 2], "en-nz":[",", ",", ".", 3], "en-us":[",", ",", ".", 3], "en-za":[",", ",", ".", 3], "es-ar":[".", ".", ",", 3], "es-bo":[".", ".", ",", 3], "es-cl":[".", ".", ",", 3], "es-co":[".", ".", ",", 3], "es-cr":[".", ".", ",", 3], "es-do":[".", ".", ",", 3], "es-ec":[".", ".", ",", 3], "es-es":[".", ".", ",", 3], "es-gt":[",", ",", ".", 3], "es-hn":[",", ",", ".", 3], "es-mx":[",", ",", ".", 3], "es-ni":[",", ",", ".", 3], "es-pa":[",", ",", ".", 3], "es-pe":[",", ",", ".", 3], "es-pr":[",", ",", ".", 3], "es-py":[".", ".", ",", 3], "es-sv":[",", ",", ".", 3], "es-uy":[".", ".", ",", 3], "es-ve":[".", ".", ",", 3], "fi-fi":[" ", " ", ",", 3], "fr-be":[".", ".", ",", 3], "fr-ca":[" ", " ", ",", 3], "fr-ch":[" ", " ", ".", 3], "fr-fr":[" ", " ", ",", 3], "fr-lu":[".", ".", ",", 3], "he-il":[",", ",", ".", 3], "hu-hu":[" ", " ", ",", 3], "it-ch":[" ", " ", ".", 3], "it-it":[".", ".", ",", 3], "ja-jp":[",", ",", ".", 3], "ko-kr":[",", ",", ".", 3], "no-no":[".", ".", ",", 3], "nl-be":[" ", " ", ",", 3], "nl-nl":[".", ".", ",", 3], "pl-pl":[".", ".", ",", 3], "pt-br":[".", ".", ",", 3], "pt-pt":[".", ".", "$", 3], "ru-ru":[" ", " ", ",", 3], "sv-se":[".", " ", ",", 3], "tr-tr":[".", ".", ",", 3], "zh-cn":[",", ",", ".", 3], "zh-hk":[",", ",", ".", 3], "zh-tw":[",", ",", ".", 3], "*":[",", ",", ".", 3]};
})();
dojo.i18n.number._mapToLocalizedFormatData = function (table, locale) {
	locale = dojo.hostenv.normalizeLocale(locale);
	var data = table[locale];
	if (typeof data == "undefined") {
		data = table["*"];
	}
	return data;
};

